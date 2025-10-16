# Copilot Instructions for pagos

## Project Overview
This is a Java Spring Boot project for payment processing. The main application entry point is `src/main/java/org/pagos/pagos/PagosApplication.java`. The project structure follows standard Maven/Gradle conventions, with source code in `src/main/java` and tests in `src/test/java`.

## Architecture & Key Components
- **Main Application**: `PagosApplication.java` is the Spring Boot entry point.
- **Controllers**: Place REST API controllers in `infrastucture/api/` (note: 'infrastucture' is likely a typo for 'infrastructure').
- **Configuration**: Use `application.properties` in `src/main/resources` for app settings.
- **Templates/Static**: Place web templates in `resources/templates` and static assets in `resources/static`.

## Developer Workflows
- **Build**: Use `./gradlew build` (or `gradlew.bat build` on Windows) to build the project.
- **Run**: Use `./gradlew bootRun` to start the application.
- **Test**: Run tests with `./gradlew test`.
- **JAR Output**: Built JARs are in `build/libs/`.
- **Reports**: Test and problem reports are in `build/reports/`.

## Project Conventions
- **Package Naming**: All Java code is under `org.pagos.pagos`.
- **Class Placement**: Controllers and other components are grouped by type under `infrastucture/`.
- **Testing**: Tests mirror the main package structure under `src/test/java`.
- **Generated Code**: Ignore files in `build/` and `bin/` for manual edits.

## Integration & Dependencies
- **Spring Boot**: Main framework for dependency injection, REST, and configuration.
- **Gradle**: Build tool; dependencies managed in `build.gradle`.
- **No database config**: No obvious DB integration in the visible structure; add DB config in `application.properties` if needed.

## Patterns & Examples
- **REST Controller Example**: Place controllers in `infrastucture/api/` and annotate with `@RestController`.
- **Application Properties**: Use `application.properties` for environment-specific settings.

## Special Notes
- The folder `infrastucture` is likely a typo; consider renaming to `infrastructure` for consistency.
- No custom scripts or nonstandard workflows detected; use standard Spring Boot/Gradle practices.

---

For more details, see `build.gradle`, `PagosApplication.java`, and the `infrastucture/api/` directory for entry points and examples.
