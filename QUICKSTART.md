# SecureBank - Quick Start Guide

## Overview
A complete Spring Boot banking demo application with user authentication, account management, and transaction tracking. The UI is adapted from a modern React financial application design.

## Running the Application

### Option 1: Using Maven (Recommended for Development)
```bash
mvn spring-boot:run
```

### Option 2: Build and Run JAR
```bash
mvn clean package
java -jar target/Banking-1.0-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

### Option 3: Run with Environment Variables
```bash
# Set organization ID and API key for session validation
ORG_ID=your-org-id-here API_KEY=your-api-key-here mvn spring-boot:run

# Or export them first
export ORG_ID=your-org-id-here
export API_KEY=your-api-key-here
mvn spring-boot:run
```

## Environment Variables

The application supports the following environment variables:

| Variable | Description | Default | Usage |
|----------|-------------|---------|-------|
| `ORG_ID` | Organization ID for landing page profiling script | `your-org-id-here` | Used in landing.html for third-party profiling integration |
| `API_KEY` | API key for session validation service | `your-api-key-here` | Used by SessionQueryService to validate user sessions after login |

**Setting Environment Variables:**

```bash
# Linux/Mac
export ORG_ID=ctvkbfxp
export API_KEY=your-api-key-123
mvn spring-boot:run

# Windows Command Prompt
set ORG_ID=ctvkbfxp
set API_KEY=your-api-key-123
mvn spring-boot:run

# Windows PowerShell
$env:ORG_ID="ctvkbfxp"
$env:API_KEY="your-api-key-123"
mvn spring-boot:run

# Inline (Linux/Mac)
ORG_ID=ctvkbfxp API_KEY=your-api-key-123 mvn spring-boot:run

# Via Maven command line
mvn spring-boot:run -Dapp.org.id=ctvkbfxp -Dapp.api.key=your-api-key-123
```

## Application Structure

### Pages
1. **Landing Page** (`/`) - Marketing page with features and call-to-action
2. **Login Page** (`/login`) - User authentication
3. **Signup Page** (`/signup`) - New user registration
4. **Dashboard** (`/dashboard`) - User's accounts and transactions (requires login)

### Demo Accounts
Three users are pre-created with sample data:

| Email | Password | Accounts |
|-------|----------|----------|
| john.doe@securebank.com | password123 | Checking, Savings, Investment |
| jane.smith@securebank.com | password123 | Checking, Savings, Investment |
| demo@securebank.com | password123 | Checking, Savings, Investment |

Each user has:
- **Checking Account**: ~$45,230
- **Savings Account**: ~$67,890 (4.5% APY)
- **Investment Account**: ~$11,469 (+12.5% growth)
- **Transaction History**: 10 sample transactions

## Features Implemented

### Authentication System
- ✅ Email-based login (not username)
- ✅ Password hashing with BCrypt
- ✅ Session management
- ✅ User registration with validation
- ✅ Error handling for invalid credentials
- ✅ Protected routes (dashboard requires login)
- ✅ Logout functionality
- ✅ External session validation via API after login
- ✅ Session ID tracking from landing page to login

### Account Management
- ✅ Multiple account types (Checking, Savings, Investment)
- ✅ Account balances and available funds
- ✅ APY display for savings accounts
- ✅ Performance tracking for investment accounts
- ✅ Account details (institution, account number)

### Transaction Tracking
- ✅ Transaction history with categories
- ✅ Amount, date, and status tracking
- ✅ Pending vs completed transaction states
- ✅ Transaction icons and categorization

### User Interface
- ✅ Modern financial application design
- ✅ Responsive layout
- ✅ Landing page with marketing content
- ✅ Clean authentication forms
- ✅ Professional dashboard layout
- ✅ Adapted from React reference design

## Technology Stack

- **Backend**: Spring Boot 3.2.2, Java 21
- **Security**: Spring Security with BCrypt
- **Database**: H2 in-memory database
- **ORM**: Spring Data JPA with Hibernate
- **Templates**: Thymeleaf
- **Build**: Maven

## Project Structure

```
src/main/java/org/itnaf/banking/
├── BankingApplication.java          # Main application entry point
├── config/
│   └── DataInitializer.java         # Populates demo data on startup
├── controller/
│   ├── AuthController.java          # Handles user registration
│   ├── DashboardController.java     # Dashboard view and data
│   └── ViewController.java          # Landing, login, signup pages
├── model/
│   ├── User.java                    # User entity with credentials
│   ├── BankAccount.java             # Bank account entity
│   └── Transaction.java             # Transaction entity
├── repository/
│   ├── UserRepository.java          # User data access
│   ├── BankAccountRepository.java   # Account data access
│   └── TransactionRepository.java   # Transaction data access
├── security/
│   ├── SecurityConfig.java          # Spring Security configuration
│   └── CustomUserDetailsService.java # User authentication
└── service/
    ├── UserService.java             # User business logic
    └── BankAccountService.java      # Account business logic

src/main/resources/
├── application.properties           # App configuration
├── static/css/
│   └── styles.css                   # Complete UI styling
└── templates/
    ├── landing.html                 # Landing page
    ├── login.html                   # Login form
    ├── signup.html                  # Registration form
    └── dashboard.html               # User dashboard
```

## Key Implementation Details

### Password Requirements
- Minimum 6 characters
- Hashed with BCrypt before storage
- Validated on both client and server side

### Session Management
- Form-based authentication
- Server-side session with JSESSIONID cookie
- Sessions persist until logout or browser close

### Database
- H2 in-memory database (data resets on restart)
- Perfect for demos and testing
- Access H2 console at: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:bankingdb`
  - Username: `sa`
  - Password: (leave blank)

### Security Configuration
- Public routes: `/`, `/login`, `/signup`, `/css/**`, `/error`
- Protected routes: All other pages require authentication
- Logout URL: `/logout` (clears session, redirects to `/`)

## Creating New Users

### Via Signup Form
1. Navigate to http://localhost:8080/signup
2. Enter name, email, and password (min 6 characters)
3. Submit form
4. You'll be redirected to login

Note: New users won't have accounts or transactions initially. Only demo users have pre-populated data.

## Customization

### Changing Port
Edit `src/main/resources/application.properties`:
```properties
server.port=3000
```

### Setting Organization ID
The landing page includes profiling script integration. Configure the organization ID:

**Via Environment Variable (Recommended):**
```bash
ORG_ID=your-org-id mvn spring-boot:run
```

**Via Application Properties:**
Edit `src/main/resources/application.properties`:
```properties
app.org.id=your-org-id
```

**Via Command Line:**
```bash
mvn spring-boot:run -Dapp.org.id=your-org-id
```

### Setting API Key for Session Validation
The application validates user sessions with an external API after login. Configure the API key:

**Via Environment Variable (Recommended):**
```bash
API_KEY=your-api-key mvn spring-boot:run
```

**Via Application Properties:**
Edit `src/main/resources/application.properties`:
```properties
app.api.key=your-api-key
```

**Via Command Line:**
```bash
mvn spring-boot:run -Dapp.api.key=your-api-key
```

**Complete Example with Both Variables:**
```bash
ORG_ID=ctvkbfxp API_KEY=your-api-key-123 mvn spring-boot:run
```

### Password Requirements
Edit `src/main/java/org/itnaf/banking/model/User.java`:
```java
@Size(min = 8, message = "Password must be at least 8 characters")
```

### Adding More Demo Users
Edit `src/main/java/org/itnaf/banking/config/DataInitializer.java` and add users in the `run()` method.

## Troubleshooting

### Port 8080 already in use
Either stop the application using that port, or change the port in `application.properties`

### Application won't start
- Ensure Java 21 is installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- Try: `mvn clean install`

### Can't log in with demo accounts
- Verify the application started successfully
- Check console logs for DataInitializer messages
- Demo users are created on startup

## Next Steps

For production use, you should:
1. Replace H2 with a persistent database (PostgreSQL, MySQL)
2. Add stronger password requirements
3. Implement email verification
4. Add two-factor authentication
5. Set up HTTPS/SSL
6. Configure proper session timeout
7. Add CSRF protection enhancements
8. Implement proper error logging

## Support

See `CLAUDE.md` for detailed architecture documentation and development guidelines.
