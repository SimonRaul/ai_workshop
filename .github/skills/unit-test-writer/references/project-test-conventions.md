# Project test conventions

Use this file as a quick repository-specific reference, then inspect the current neighboring tests because conventions can evolve.

The representative test is:
`onlineshopapi/src/test/java/msg/onlineshopapi/unit/service/strategy/MostAbundantStrategyTest.java`

Observed style:

- Tests are under the same base package as the unit and commonly use a package-private test class.
- JUnit 5 is used with `org.junit.jupiter.api.Test`.
- Mockito extension setup uses `@ExtendWith(MockitoExtension.class)`, field-level `@Mock`, and `@InjectMocks`.
- Assertions use AssertJ static imports, especially `assertThat` and `assertThatThrownBy`.
- Mockito uses static imports for `when`, `verify`, and argument matchers such as `anyList`.
- Test names follow `method_expectedBehavior_whenCondition`, for example `findStocks_returnsMostAbundantStock_whenSufficientQuantityAvailable`.
- Tests arrange realistic domain objects, invoke the public method, then assert results, exceptions, and meaningful repository interactions.
- Repeated domain setup is placed in private helpers such as `product`, `location`, `stock`, and `orderDetail`.
- Collection results use order-insensitive AssertJ assertions when ordering is not part of the contract.

These are examples, not a reason to copy irrelevant details. Match the nearest tests and the implementation's actual contract.

