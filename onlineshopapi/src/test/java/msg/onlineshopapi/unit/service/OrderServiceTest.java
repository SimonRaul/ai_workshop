package msg.onlineshopapi.unit.service;

import msg.onlineshopapi.exception.OrderNotProcessableException;
import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.Location;
import msg.onlineshopapi.model.Order;
import msg.onlineshopapi.model.OrderDetail;
import msg.onlineshopapi.model.Product;
import msg.onlineshopapi.model.Stock;
import msg.onlineshopapi.model.StockId;
import msg.onlineshopapi.model.User;
import msg.onlineshopapi.repository.OrderRepository;
import msg.onlineshopapi.repository.StockRepository;
import msg.onlineshopapi.repository.UserRepository;
import msg.onlineshopapi.service.OrderService;
import msg.onlineshopapi.service.strategy.OrderStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String CUSTOMER_EMAIL = "customer@example.com";

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderStrategy orderStrategy;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_returnsCreatedOrderAndDeductsStock_whenCustomerAndStockAreValid() {
        UUID orderId = UUID.randomUUID();
        Product product = product();
        User user = user();
        Order order = order(orderId, orderDetail(product, 3));
        Stock stock = stock(product, 10);

        when(userRepository.findByEmail(CUSTOMER_EMAIL)).thenReturn(Optional.of(user));
        when(orderStrategy.findStocks(any())).thenReturn(List.of(stock));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(order, CUSTOMER_EMAIL);

        assertThat(result).isSameAs(order);
        assertThat(result.getUser()).isSameAs(user);
        assertThat(result.getOrderDetails()).singleElement()
                .satisfies(detail -> {
                    assertThat(detail.getOrder()).isSameAs(order);
                    assertThat(detail.getProduct()).isSameAs(product);
                    assertThat(detail.getQuantity()).isEqualTo(3);
                    assertThat(detail.getShippedFrom()).isSameAs(stock.getLocation());
                    assertThat(detail.getId().getOrderId()).isEqualTo(orderId);
                    assertThat(detail.getId().getProductId()).isEqualTo(product.getId());
                });
        assertThat(stock.getQuantity()).isEqualTo(7);
        verify(orderRepository, times(2)).save(order);
        verify(stockRepository).save(stock);
    }

    @Test
    void createOrder_mergesDuplicateProductQuantities_beforeFindingStock() {
        Product product = product();
        User user = user();
        Order order = order(UUID.randomUUID(), orderDetail(product, 2), orderDetail(product, 3));
        Stock stock = stock(product, 10);

        when(userRepository.findByEmail(CUSTOMER_EMAIL)).thenReturn(Optional.of(user));
        when(orderStrategy.findStocks(any())).thenReturn(List.of(stock));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(order, CUSTOMER_EMAIL);

        verify(orderStrategy).findStocks(argThat(argThatSetHasQuantity(5, product)));
        assertThat(order.getOrderDetails()).singleElement().extracting(OrderDetail::getQuantity).isEqualTo(5);
        assertThat(stock.getQuantity()).isEqualTo(5);
    }

    @Test
    void createOrder_propagatesInsufficientStockFailure_withoutPersistingOrder() {
        Product product = product();
        Order order = order(UUID.randomUUID(), orderDetail(product, 5));
        OrderNotProcessableException failure =
                new OrderNotProcessableException("Insufficient stock for product");

        when(userRepository.findByEmail(CUSTOMER_EMAIL)).thenReturn(Optional.of(user()));
        when(orderStrategy.findStocks(any())).thenThrow(failure);

        assertThatThrownBy(() -> orderService.createOrder(order, CUSTOMER_EMAIL))
                .isSameAs(failure);

        verify(orderRepository, never()).save(any(Order.class));
        verifyNoInteractions(stockRepository);
    }

    @Test
    void createOrder_throwsResourceNotFound_whenCustomerDoesNotExist() {
        Order order = order(UUID.randomUUID());

        when(userRepository.findByEmail(CUSTOMER_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(order, CUSTOMER_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: " + CUSTOMER_EMAIL);

        verifyNoInteractions(orderStrategy, orderRepository, stockRepository);
    }

    @Test
    void createOrder_createsOrderWithoutStockChanges_whenCartIsEmpty() {
        User user = user();
        Order order = order(UUID.randomUUID());

        when(userRepository.findByEmail(CUSTOMER_EMAIL)).thenReturn(Optional.of(user));
        when(orderStrategy.findStocks(Set.of())).thenReturn(List.of());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(order, CUSTOMER_EMAIL);

        assertThat(result.getUser()).isSameAs(user);
        assertThat(result.getOrderDetails()).isEmpty();
        verify(orderStrategy).findStocks(Set.of());
        verify(orderRepository, times(2)).save(order);
        verifyNoInteractions(stockRepository);
    }

    private Product product() {
        return Product.builder().id(UUID.randomUUID()).name("Product").build();
    }

    private User user() {
        return User.builder().id(UUID.randomUUID()).email(CUSTOMER_EMAIL).build();
    }

    private Order order(UUID id, OrderDetail... details) {
        return Order.builder().id(id).orderDetails(new HashSet<>(Set.of(details))).build();
    }

    private OrderDetail orderDetail(Product product, int quantity) {
        return OrderDetail.builder().product(product).quantity(quantity).build();
    }

    private Stock stock(Product product, int quantity) {
        Location location = Location.builder().id(UUID.randomUUID()).name("Warehouse").build();
        return Stock.builder()
                .id(StockId.builder().productId(product.getId()).locationId(location.getId()).build())
                .product(product)
                .location(location)
                .quantity(quantity)
                .build();
    }

    private static org.mockito.ArgumentMatcher<Set<OrderDetail>> argThatSetHasQuantity(
            int quantity, Product product) {
        return details -> details.size() == 1
                && details.iterator().next().getProduct() == product
                && details.iterator().next().getQuantity() == quantity;
    }
}