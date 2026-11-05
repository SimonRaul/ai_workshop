---
name: unit-test-writer
description: Write or extend unit tests for Java Spring Boot features, methods, services, strategies, and other testable units. Use this skill whenever the user asks to write unit tests, generate tests, add test coverage, cover edge cases, test a service, create JUnit tests, or investigate behavior through tests, even when they do not explicitly name this skill. Use JUnit 5 with Mockito and AssertJ for Spring Boot unit tests, inspect nearby tests for conventions, target at least 80% meaningful coverage when measurable, and leave bug-revealing tests failing rather than changing production code.
---

# Unit Test Writer

Create focused, maintainable JUnit 5 unit tests for the requested Java/Spring Boot unit. The input may be a feature, public method, service, strategy, class, or an entire service area. Tests should exercise observable behavior and expose defects, not merely increase line counts.

## Workflow

1. **Identify the unit and its contract.** Read the target implementation, its collaborators, DTOs/entities/value objects, custom exceptions, and callers when needed. Determine inputs, outputs, side effects, collaborator interactions, validation rules, and error behavior. Do not infer behavior from the request alone.
2. **Inspect test conventions before writing.** Find the nearest tests in the same package or module. Match their package, class visibility, naming, fixture style, Mockito setup, assertion style, ordering conventions, and test location. Use `references/project-test-conventions.md` as a repository-specific example, but treat current neighboring tests as authoritative if they differ.
3. **List scenarios before coding.** Present or record a concise scenario matrix covering:
   - the normal success path;
   - boundary values and empty or singleton collections;
   - invalid, missing, null, or unsupported inputs where the contract permits them;
   - each relevant exception or failure path;
   - multiple items and interaction/order variations;
   - collaborator failures or important side effects;
   - suspected defects discovered while reading the implementation.
4. **Write one test per scenario.** Use JUnit 5. For Spring Boot services, prefer a pure unit test with Mockito rather than loading the Spring context unless the requested behavior specifically depends on Spring wiring. Use AssertJ for state and exception assertions and Mockito for stubbing and interaction verification. Keep each test focused on one behavior.
5. **Add private helpers.** Extract repeated fixture construction into private helper methods or builders in the test class. Helpers should make the scenario intent obvious and avoid hiding the assertion or the important input variation.
6. **Check coverage and quality.** Run the smallest relevant test command, then use the repository's configured coverage tool when available. Aim for at least 80% meaningful line/branch coverage of the requested unit, while prioritizing edge cases and behavior over artificial coverage. If coverage cannot be measured, state that explicitly and explain the scenario coverage instead.
7. **Preserve discovered bugs.** If a test demonstrates a production bug, keep the test and do not modify production code to make it pass. Clearly report the failing test, expected behavior, observed behavior, and likely production location. Do not weaken the test or mark it as disabled unless the repository convention explicitly requires that and the user asks for it.
8. **Report the result.** Summarize the scenarios covered, files changed, validation command and result, measured coverage if available, and any intentionally failing bug-revealing tests. Do not claim 80% coverage without a measurement or defensible repository tool output.

## Test design rules

- Test public behavior through the unit's public API; avoid testing private implementation details.
- Prefer deterministic fixtures and explicit values over random values unless uniqueness is part of the behavior.
- Assert returned values and domain state with AssertJ (`assertThat`, `assertThatThrownBy`, and related assertions).
- Verify repository/gateway calls when they are part of the unit's contract, including meaningful arguments. Avoid verifying every incidental call.
- Stub only collaborators; do not mock value objects, entities, collections, or the class under test.
- Use `@ExtendWith(MockitoExtension.class)`, `@Mock`, and `@InjectMocks` when that matches nearby tests.
- Name tests with the existing local convention, commonly `method_condition` (for example, `findStocks_returnsMostAbundantStock_whenSufficientQuantityAvailable`).
- Keep tests isolated. Do not use a real database, network, filesystem, or Spring context for a unit test unless the target contract requires it.
- If the requested unit cannot be tested as a pure unit because of static, final, time, or framework coupling, explain the constraint and use the smallest repository-supported alternative rather than silently changing production design.

## Expected output

Unless the user asks for a different format:

1. Create or update tests in the repository's conventional test directory.
2. Give the scenario list before or alongside the implementation summary.
3. State whether tests pass, fail because of a discovered bug, or could not run.
4. Include coverage evidence or the precise limitation preventing measurement.
5. Never fix production bugs as part of this skill's test-writing task.

