package msg.onlineshopapi.service;

import lombok.RequiredArgsConstructor;
import msg.onlineshopapi.exception.ProductInOrderException;
import msg.onlineshopapi.exception.ResourceNotFoundException;
import msg.onlineshopapi.model.Product;
import msg.onlineshopapi.model.Supplier;
import msg.onlineshopapi.repository.OrderDetailRepository;
import msg.onlineshopapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final SupplierService supplierService;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        product.setSupplier(resolveSupplier(product.getSupplier()));
        return productRepository.save(product);
    }

    public Product update(UUID id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setWeight(product.getWeight());
        existing.setCategory(product.getCategory());
        existing.setSupplier(resolveSupplier(product.getSupplier()));
        existing.setImageUrl(product.getImageUrl());
        return productRepository.save(existing);
    }

    private Supplier resolveSupplier(Supplier supplier) {
        UUID supplierId = supplier == null ? null : supplier.getId();
        if (supplierId == null) {
            throw new ResourceNotFoundException("Supplier not found with id: null");
        }

        return supplierService.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));
    }

    public void deleteById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (orderDetailRepository.existsByProduct_Id(id)) {
            throw new ProductInOrderException("Cannot delete product '" + product.getName()
                    + "' because it is referenced by existing orders.");
        }

        productRepository.delete(product);
    }
}
