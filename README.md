# SecureBank

A modern banking demo application built with Spring Boot 3.2.2 and Java 21. Includes user authentication, account management, transaction tracking, and session profiling via ThreatMetrix.

## Prerequisites

- Java 21+
- Maven 3.x

## Quick Start

```bash
# 1. Clone and enter the repo
git clone https://github.com/marcofanti/BankingDemo.git
cd BankingDemo

# 2. Copy the env template and fill in real values
cp .env.local .env

# 3. Run
mvn spring-boot:run
```

The app starts on **port 7654**: [http://localhost:7654](http://localhost:7654)

## Environment Variables

All configuration is driven by environment variables. See [`.env.local`](.env.local) for the full list with descriptions. Key variables:

| Variable | Purpose |
|---|---|
| `ORG_ID` | ThreatMetrix organisation ID |
| `PAGE_ID` | Profiling page identifier |
| `PROFILING_SERVER` | ThreatMetrix profiling host |
| `API_KEY` | Session-query API key |
| `API_BASE_URL` | Session-query API base URL |
| `USER4_NAME` / `USER4_EMAIL` / `USER4_PASSWORD` | Optional 4th demo user (created only when `USER4_EMAIL` is set) |
| `VALIDATION_IGNORE_EMAILS` | Comma-separated emails that bypass session validation |

## Demo Accounts

Three accounts are pre-loaded on startup (password: `password123`):

| Email | Name |
|---|---|
| `john.doe@securebank.com` | John Doe |
| `jane.smith@securebank.com` | Jane Smith |
| `demo@securebank.com` | Demo User |

Each account includes a Checking, Savings, and Investment account with sample transaction history. A fourth user can be added at runtime via the `USER4_*` environment variables.

## Pages

### Public
- `/` — Landing page with profiling
- `/login` — Email / password login
- `/signup` — New account registration

### Authenticated
- `/dashboard` — Account overview and transactions
- `/transfer` — Transfer between accounts or recipients
- `/recipients` — Manage saved recipients
- `/change-password` — Update password
- `/logout-confirm` — Logout confirmation

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2.2 |
| Security | Spring Security, BCrypt |
| Database | H2 (in-memory) |
| Templating | Thymeleaf |
| Build | Maven |

## Project Structure

```
src/main/java/org/itnaf/banking/
├── config/          # DataInitializer – demo data on startup
├── controller/      # MVC controllers (views & endpoints)
├── model/           # JPA entities (User, BankAccount, Transaction, Recipient)
├── repository/      # Spring Data JPA repositories
├── security/        # SecurityConfig, UserDetailsService, auth success handler
└── service/         # Business logic (transfers, session-query API)
```

## H2 Console

Available at [http://localhost:7654/h2-console](http://localhost:7654/h2-console) during development.

- **JDBC URL:** `jdbc:h2:mem:bankingdb`
- **User:** `sa`
- **Password:** *(leave blank)*

> The database is recreated on every restart. All data is populated by `DataInitializer` at boot.

## Notes

- Transfers are logged but do **not** update balances — this is a demo only.
- Session profiling calls ThreatMetrix on the landing and login pages; set the env vars to a valid organisation to receive real scores.
