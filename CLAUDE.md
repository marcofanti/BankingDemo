# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**SecureBank** - A modern banking demo application built with Spring Boot 3.2.2 and Java 21. Features complete user authentication, account management, and transaction tracking with a financial application UI adapted from a React reference design.

**Group ID:** org.itnaf
**Artifact ID:** Banking
**Java Version:** 21
**Spring Boot:** 3.2.2

## Running the Application

```bash
# Run the application (default port: 8080)
mvn spring-boot:run

# Package and run as JAR
mvn clean package
java -jar target/Banking-1.0-SNAPSHOT.jar

# Access the application
# Landing page: http://localhost:8080
# Login: http://localhost:8080/login
# Dashboard: http://localhost:8080/dashboard (requires authentication)

# H2 Database Console (for debugging)
# URL: http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:bankingdb
# Username: sa
# Password: (leave blank)
```

## Demo Accounts

Three pre-populated demo accounts are available (all use password: `password123`):
- `john.doe@securebank.com`
- `jane.smith@securebank.com`
- `demo@securebank.com`

Each demo user has:
- Primary Checking account (~$45,230)
- High-Yield Savings account (~$67,890) with 4.5% APY
- Investment Portfolio (~$11,469) with 12.5% growth
- 10 sample transactions

## Build Commands

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Run a single test method
mvn test -Dtest=ClassName#methodName

# Package the application
mvn package

# Clean build artifacts
mvn clean
```

## Architecture Overview

### Technology Stack
- **Backend Framework:** Spring Boot 3.2.2
  - Spring Web (REST controllers, MVC)
  - Spring Security (authentication & authorization)
  - Spring Data JPA (database operations)
  - Thymeleaf (server-side HTML templating)
- **Database:** H2 in-memory database
- **Security:** BCrypt password hashing
- **Build Tool:** Maven

### Package Structure

```
org.itnaf.banking/
├── config/          # Application configuration
│   └── DataInitializer.java  # Demo data population
├── controller/      # MVC controllers
│   ├── AuthController.java          # User registration
│   ├── DashboardController.java     # Dashboard view
│   ├── ProfileController.java       # Logout confirm, change password
│   ├── RecipientController.java     # Manage saved recipients
│   ├── TransferController.java      # All transfer operations
│   └── ViewController.java          # Landing, login, signup pages
├── model/           # JPA entities
│   ├── User.java
│   ├── BankAccount.java
│   ├── Transaction.java
│   └── Recipient.java               # Saved transfer recipients
├── repository/      # Data access layer
│   ├── UserRepository.java
│   ├── BankAccountRepository.java
│   ├── TransactionRepository.java
│   └── RecipientRepository.java
├── security/        # Spring Security configuration
│   ├── SecurityConfig.java
│   └── CustomUserDetailsService.java
└── service/         # Business logic
    ├── UserService.java
    ├── BankAccountService.java
    ├── RecipientService.java        # Manage recipients
    └── TransferService.java         # Demo transfer logic
```

### Key Design Decisions

**1. Authentication Flow:**
- Users log in with email (not username) and password
- Spring Security handles login via form-based authentication
- Passwords are hashed with BCrypt before storage
- Sessions are managed server-side with JSESSIONID cookie
- Logout shows confirmation page before signing out
- Users can change password from dashboard (requires current password)

**2. Data Model:**
- `User` entity: Stores user profile and credentials
- `BankAccount` entity: Supports CHECKING, SAVINGS, and INVESTMENT types
- `Transaction` entity: Tracks all account activity with categories
- One-to-many relationships: User → BankAccounts → Transactions

**3. Security Configuration (SecurityConfig.java:41-67):**
- Public routes: `/`, `/login`, `/signup`, `/css/**`, `/error`
- Protected routes: Everything else (requires authentication)
- Login endpoint: POST `/login` with `email` and `password` parameters
- Success redirect: `/dashboard`
- Failure redirect: `/login?error=true`

**4. In-Memory Database:**
- H2 database recreated on each restart (ddl-auto=create-drop)
- DataInitializer populates demo data on startup
- Perfect for demos; replace with persistent DB for production

**5. Transfer System (Demo Only):**
- Transfers are logged but do NOT update account balances
- Supports 3 transfer types: between own accounts, to saved recipients, and quick transfers
- Recipients can be saved for future use
- All transfer validation is performed (sufficient funds, etc.) but no actual money movement
- TransferService includes clear logging for demo purposes

**6. UI Design:**
- HTML templates in `src/main/resources/templates/`
- CSS styles in `src/main/resources/static/css/styles.css`
- Design adapted from React reference app (Auth0 demo)
- Thymeleaf for server-side rendering with Spring Security integration
- Dashboard includes Transfer button in navigation for quick access
- User dropdown menu provides access to Change Password and Sign Out

## Common Development Tasks

### Adding a New User Manually
Users are typically created via the signup form, but you can create them programmatically:
```java
User user = new User("Name", "email@example.com", "plainPassword");
userService.registerUser(user); // Password is automatically hashed
```

### Creating New Bank Account Types
Account types are defined in `BankAccount.AccountType` enum. To add a new type:
1. Add enum value in `BankAccount.java:58`
2. Update dashboard template to handle new type styling
3. Add appropriate fields (like `apy` for savings, `changePercent` for investments)

### Modifying Password Requirements
Password validation is in `User.java:31-32`:
```java
@Size(min = 6, message = "Password must be at least 6 characters")
```
Update the `@Size` annotation to change requirements.

### Changing Session Timeout
Add to `application.properties`:
```properties
server.servlet.session.timeout=30m
```

## Frontend Templates

### Public Pages
- **landing.html** - Marketing page with features, hero section, and CTAs
- **login.html** - Email/password login form with error handling
- **signup.html** - User registration form (name, email, password)

### Protected Pages (Require Login)
- **dashboard.html** - Main view showing accounts and transactions
- **transfer.html** - Main transfer page with 3 transfer options
- **transfer-own.html** - Transfer between user's own accounts
- **transfer-recipient.html** - Transfer to saved recipients
- **transfer-quick.html** - One-time transfer to new recipient
- **recipients.html** - View and manage saved recipients
- **add-recipient.html** - Add new recipient form
- **change-password.html** - Change password form
- **logout-confirm.html** - Logout confirmation page

All templates use Thymeleaf expressions (`th:*`) for dynamic content and Spring Security integration.

## Important Files

- **SecurityConfig.java** - Defines authentication rules and protected routes
- **DataInitializer.java** - Creates demo users and sample data on startup
- **application.properties** - Database config, server port, logging levels
- **styles.css** - Complete UI styling adapted from React reference design

## Testing Notes

The application uses an in-memory H2 database, so all data is reset on restart. This makes it perfect for demos and testing, but unsuitable for production without migrating to a persistent database (PostgreSQL, MySQL, etc.).
