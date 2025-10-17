## 🏗️ Architecture

This project follows **Clean Architecture** principles as proposed by Robert C. Martin
and adapted by Bancolombia's Scaffold Clean Architecture.

### Design Decisions

**Package-based vs Multimodule:**
Due to the 3-day time constraint, I implemented Clean Architecture using a 
**package-based structure** instead of Gradle multimodules, while strictly 
following the **dependency rule** and **separation of concerns**.

**Layers:**
- **Domain** (`domain/`): Business logic, entities, and ports (interfaces)
  - `model/`: Domain entities
  - `usecase/`: Use cases (application logic)
  - `gateway/`: Repository interfaces (ports)

- **Infrastructure** (`infrastructure/`):
  - `entrypoint/rest/`: REST controllers (entry points)
  - `drivenadapter/mongo/`: MongoDB implementations (driven adapters)

- **Application** (`application/`): Main class and configuration

## 📊 Domain Model

- **Franchise** (Franquicia): Represents a franchise with multiple branches
- **Branch** (Sucursal): Represents a branch office of a franchise
- **Product** (Producto): Represents a product offered in a branch