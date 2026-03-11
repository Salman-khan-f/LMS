# Smart Loan Management & Approval Workflow System

A comprehensive loan management system built with Spring Boot that features intelligent risk assessment, automated approval workflows, and real-time loan processing.

## Features

### 🧠 Smart Features
- **AI-Powered Risk Assessment**: Automated credit scoring and risk evaluation
- **Auto-Approval System**: Low-risk applications are automatically approved
- **Dynamic Interest Rates**: Rates adjusted based on risk profiles
- **Real-time Processing**: Instant application evaluation

### 📊 Core Functionality
- **Customer Management**: Complete customer profile management
- **Loan Applications**: Streamlined application process
- **Approval Workflows**: Hierarchical approval system (Junior → Senior → Manager)
- **Loan Management**: Full loan lifecycle tracking
- **Payment Processing**: Comprehensive payment tracking and management

### 🔒 Security & Compliance
- **Role-based Access Control**: Secure API endpoints
- **Data Validation**: Comprehensive input validation
- **Audit Trail**: Complete transaction logging

## Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: H2 (development) / Configurable for production
- **Security**: Spring Security
- **API**: RESTful APIs with OpenAPI documentation
- **Build Tool**: Maven

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

1. **Clone and navigate to the project**:
   ```bash
   cd loan-management-system
   ```

2. **Run with Maven wrapper**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**:
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:loanmgmt`
     - Username: `sa`
     - Password: `password`

## API Endpoints

### Authentication
```
Username: admin
Password: admin123
```

### Customer Management
- `GET /api/customers` - Get all customers
- `POST /api/customers` - Create new customer
- `GET /api/customers/{id}` - Get customer by ID
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer

### Loan Applications
- `GET /api/loan-applications` - Get all applications
- `POST /api/loan-applications` - Submit new application
- `GET /api/loan-applications/{id}` - Get application by ID
- `PUT /api/loan-applications/{id}/status` - Update application status

### Loans
- `GET /api/loans` - Get all loans
- `GET /api/loans/{id}` - Get loan by ID
- `GET /api/loans/active` - Get active loans
- `GET /api/loans/dashboard/summary` - Loan summary statistics

### Payments
- `GET /api/payments` - Get all payments
- `POST /api/payments` - Process new payment
- `GET /api/payments/loan/{loanId}` - Get payments for a loan

### Approval Workflows
- `GET /api/approvals` - Get all approvals
- `GET /api/approvals/pending/role/{role}` - Get pending approvals by role
- `PUT /api/approvals/{id}/review` - Review and approve/reject application

### Dashboard
- `GET /api/dashboard/summary` - System-wide statistics
- `GET /api/dashboard/risk-analysis` - Risk assessment analytics

## Smart Features in Action

### Risk Assessment Algorithm
The system evaluates applications based on:
- **Credit Score** (40% weight): FICO score analysis
- **Income Ratio** (30% weight): Debt-to-income analysis
- **Loan Amount** (20% weight): Amount-based risk evaluation
- **Employment Status** (10% weight): Stability assessment

### Auto-Approval Rules
Applications are automatically approved if:
- Risk score ≥ 150 points
- Loan amount ≤ $100,000
- Risk level = "LOW"

### Hierarchical Approval
For non-auto-approved applications:
1. **Junior Officer**: Initial review (Level 1)
2. **Senior Officer**: Secondary review (Level 2)
3. **Manager**: Final approval (Level 3)

## Sample API Usage

### Create a Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "dateOfBirth": "1985-05-15",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "annualIncome": 75000,
    "employmentStatus": "FULL_TIME",
    "creditScore": 720
  }'
```

### Submit Loan Application
```bash
curl -X POST http://localhost:8080/api/loan-applications \
  -H "Content-Type: application/json" \
  -u admin:admin123 \
  -d '{
    "customerId": 1,
    "loanType": "PERSONAL",
    "loanAmount": 25000,
    "loanTermMonths": 36,
    "interestRate": 8.5,
    "purpose": "Home improvement"
  }'
```

## Database Schema

The system uses the following main entities:
- **Customer**: Customer information and credit details
- **LoanApplication**: Loan application with smart scoring
- **ApprovalWorkflow**: Hierarchical approval process
- **Loan**: Approved loan details and lifecycle
- **Payment**: Payment tracking and processing

## Development

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package
```

### Code Quality
- Uses Lombok for reducing boilerplate code
- Comprehensive validation with Bean Validation
- Exception handling with proper HTTP status codes

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please open an issue in the GitHub repository.