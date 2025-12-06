# Library Management System

A complete Library Management System built with Java and MySQL, implementing CRUD operations, authentication, and role-based access control. The project follows clean OOP principles with a layered architecture that separates concerns between UI, business logic, and data access layers.

## Features

- **User Authentication**: Secure login with password hashing (SHA-256)
- **Role-Based Access Control**: Three roles - ADMIN, LIBRARIAN, and MEMBER
- **Book Management**: Full CRUD operations for books with search functionality
- **Member Management**: Manage library members (students/users)
- **Circulation System**: Issue and return books with automatic fine calculation
- **Reports**: View overdue books, active loans, and loan history
- **Search**: Search books by title, author, or ISBN

## Tech Stack

- **Java 17+**: Core programming language
- **MySQL 8.0+**: Database
- **JDBC**: Database connectivity
- **Maven**: Build tool and dependency management

## Project Structure

```
src/main/java/com/librarymanagement/
├── config/
│   └── DatabaseConfig.java          # Database connection management
├── model/
│   ├── Role.java                     # User role enum
│   ├── User.java                     # User entity
│   ├── Book.java                     # Book entity
│   ├── Member.java                   # Member entity
│   └── Loan.java                     # Loan entity
├── dao/
│   ├── BaseDAO.java                  # Base DAO interface
│   ├── UserDAO.java                  # User DAO interface
│   ├── UserDAOImpl.java              # User DAO implementation
│   ├── BookDAO.java                  # Book DAO interface
│   ├── BookDAOImpl.java              # Book DAO implementation
│   ├── MemberDAO.java                # Member DAO interface
│   ├── MemberDAOImpl.java            # Member DAO implementation
│   ├── LoanDAO.java                  # Loan DAO interface
│   └── LoanDAOImpl.java              # Loan DAO implementation
├── service/
│   ├── AuthService.java              # Authentication service
│   ├── UserService.java              # User management service
│   ├── BookService.java              # Book management service
│   ├── MemberService.java            # Member management service
│   └── LoanService.java              # Loan/circulation service
├── exception/
│   ├── EntityNotFoundException.java
│   ├── AuthenticationException.java
│   ├── AuthorizationException.java
│   └── ValidationException.java
├── util/
│   ├── PasswordUtils.java            # Password hashing utilities
│   ├── InputValidator.java          # Input validation utilities
│   └── DateUtils.java                # Date utilities
└── ui/
    ├── ConsoleApp.java               # Main application entry point
    ├── MenuRenderer.java             # Console UI utilities
    ├── AdminMenu.java                # Admin menu handler
    ├── LibrarianMenu.java            # Librarian menu handler
    └── MemberMenu.java               # Member menu handler
```

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup Instructions

### 1. Database Setup

1. **Start MySQL Server**
   ```bash
   # On Windows (if MySQL is installed as a service, it should start automatically)
   # On Linux/Mac:
   sudo systemctl start mysql
   # or
   sudo service mysql start
   ```

2. **Create Database and Tables**
   ```bash
   mysql -u root -p < schema.sql
   ```
   
   Or manually:
   - Open MySQL command line or MySQL Workbench
   - Run the SQL commands from `schema.sql`

3. **Verify Database**
   ```sql
   USE library_db;
   SHOW TABLES;
   SELECT * FROM users;
   ```

### 2. Configure Database Connection

Edit `database.properties` in the project root:

```properties
db.url=jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=your_mysql_password
```

Update `db.user` and `db.password` according to your MySQL configuration.

### 3. Build the Project

```bash
mvn clean install
```

This will:
- Compile all Java source files
- Run tests (if any)
- Package the application into a JAR file

### 4. Run the Application

**Option 1: Using Maven Exec Plugin**
```bash
mvn exec:java
```

**Option 2: Using the JAR file**
```bash
java -jar target/library-management-system-1.0.0.jar
```

**Option 3: Direct Java execution**
```bash
java -cp target/classes:target/dependency/* com.librarymanagement.ui.ConsoleApp
```

## Default Login Credentials

The following users are created by default in the database:

| Username | Password | Role |
|----------|----------|------|
| viraj | Viraj@123 | ADMIN |
| lokesh | Lokesh@1234 | LIBRARIAN |
| lokesh1 | Lokesh@123321 | MEMBER |
| viraj1 | Virah@123321 | MEMBER |

**Note**: These are default credentials for testing. Change them in production!

### Generating Password Hashes

If you need to generate password hashes for new users, you can use the `PasswordHashGenerator` utility:

```bash
# Compile and run the utility
mvn compile
java -cp target/classes com.librarymanagement.util.PasswordHashGenerator
```

This will output SHA-256 hashes for common passwords. You can then use these hashes in your SQL INSERT statements or update the `schema.sql` file.

## Usage Guide

### Admin Role

Admins have full system access:
- **Manage Users**: Create, update, delete users and assign roles
- **Manage Books**: Full CRUD operations on books
- **Manage Members**: Full CRUD operations on members
- **View Reports**: Overdue books, all issued books, loan history

### Librarian Role

Librarians can:
- **Manage Books**: Create, update, delete, and search books
- **Manage Members**: Create, update, delete members
- **Issue Books**: Issue books to members
- **Return Books**: Process book returns and calculate fines
- **View Overdue Books**: See all overdue loans

### Member Role

Members can:
- **View All Books**: Browse the entire book catalog
- **Search Books**: Search by title, author, or ISBN
- **View Active Loans**: See their currently borrowed books
- **View Loan History**: See their complete borrowing history

## Business Rules

1. **Book Issuance**:
   - Maximum 5 active loans per member
   - Default loan period: 14 days
   - Fine per day: $10.00 (for overdue books)

2. **Book Availability**:
   - System tracks total copies and available copies
   - Available copies decrease when a book is issued
   - Available copies increase when a book is returned

3. **Fine Calculation**:
   - Fines are calculated only if a book is returned after the due date
   - Fine = (days late) × $10.00

## Architecture Highlights

### Design Patterns

- **DAO Pattern**: Separates data access logic from business logic
- **Service Layer**: Encapsulates business rules and validation
- **Dependency Inversion**: Services depend on DAO interfaces, not implementations

### SOLID Principles

- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Easy to extend with new features (e.g., new roles)
- **Liskov Substitution**: DAO implementations are interchangeable
- **Interface Segregation**: Focused interfaces (BaseDAO, specific DAOs)
- **Dependency Inversion**: High-level modules depend on abstractions

### Security

- Passwords are hashed using SHA-256 (consider bcrypt for production)
- Prepared statements prevent SQL injection
- Role-based authorization enforced at service layer

## Extending the System

The architecture is designed to be extensible:

1. **Adding a GUI**: The UI layer is separate from business logic. You can replace `ConsoleApp` with a Swing/JavaFX application without changing services or DAOs.

2. **Adding New Roles**: 
   - Add new role to `Role` enum
   - Create new menu class in `ui` package
   - Update `ConsoleApp.showRoleBasedMenu()`

3. **Adding New Entities**:
   - Create model class
   - Create DAO interface and implementation
   - Create service class
   - Add UI menu options

## Troubleshooting

### Database Connection Issues

- Verify MySQL is running: `mysqladmin -u root -p status`
- Check database.properties configuration
- Ensure database `library_db` exists
- Verify user has proper permissions

### Compilation Issues

- Ensure Java 17+ is installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- Clean and rebuild: `mvn clean install`

### Runtime Issues

- Check MySQL JDBC driver is in classpath
- Verify all dependencies are downloaded: `mvn dependency:resolve`
- Check console for error messages

## Future Enhancements

- [ ] Connection pooling (HikariCP)
- [ ] Better password hashing (bcrypt)
- [ ] GUI using Swing or JavaFX
- [ ] Email notifications for overdue books
- [ ] Book reservations
- [ ] Advanced reporting and analytics
- [ ] Export reports to PDF/Excel

## License

This project is provided as-is for educational and portfolio purposes.

## Author

Built as a portfolio project demonstrating:
- Java backend development
- MySQL database design
- Clean architecture and OOP principles
- CRUD operations and business logic implementation

