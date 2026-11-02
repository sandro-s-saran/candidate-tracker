# Learning log: JUnit 5 and test-driven development

JUnit 5 and test-driven development (TDD) were both new to me for this exercise.

## What I read and used

- A walkthrough of JUnit 5 basics (`@Test`, assertions, `assertThrows`, `@BeforeEach`) and the red, green, refactor loop.
- Spring's MockMvc for testing controllers and `MockRestServiceServer` for testing the HTTP client without a network.
- Java's `java.time.Clock` for time-dependent code.

## What confused me

- **Where files go.** Production code is in `src/main/java` and tests are in `src/test/java`, in packages with the same names but in separate folders. I also wasn't sure whether to put my tests in the generated `DemoApplicationTests`. That class starts the whole Spring app (slow), so my plain domain tests got their own classes.
- **Tests that depend on time.** My first notes test computed the expected timestamp with `LocalDateTime.now()` after the call, so it could fail if the clock ticked to the next second. The fix was passing the time in and using a fixed clock in tests.
- **Choosing what is created before the tests and deciding what to create in each test.** Finding the line between what can be reused and what can't took some trial and error.
- **How Maven builds and runs tests.** I was confused about when tests actually run during the build lifecycle. Realizing commands like `./mvnw test` compile both directories and run JUnit automatically without needing a live server—while commands like `./mvnw spring-boot:run` skip tests by default—helped me speed up my TDD feedback loop.

## What I'd tell the next engineer

- Write one failing test, run it and check it fails for the right reason before writing code. A compile error counts as red.
- Keep domain tests as plain Java with no Spring, so they run in milliseconds and you'll actually run them often.
- One test class per class under test, and commit after each green.
- Never let a test read the real time or call a real service. Pass the time in, and put the provider behind an interface so tests can use a fake.
- If a change "does nothing", check your test data before the code. Here, identical timestamps made a sort invisible.
- Query parameter and JSON field names are part of the API contract. Use `@RequestParam` explicitly and check what the front end really sends (the browser's Network tab).
- On Windows PowerShell use `.\mvnw`. If `npm install` reports a peer-dependency conflict, pin the package that is too new rather than using `--force`.
