# Smart Loan Management System - Frontend

A modern React frontend for the Smart Loan Management & Approval Workflow System.

## Features

- **Dashboard**: Real-time analytics and risk assessment visualization
- **Customer Management**: Complete CRUD operations for customer data
- **Loan Applications**: Application submission, approval workflow, and status tracking
- **Loan Management**: Active loan monitoring and payment tracking
- **Authentication**: Secure login with JWT tokens
- **Responsive Design**: Material-UI components with mobile support

## Tech Stack

- **React 18** - Modern React with hooks
- **Material-UI** - Component library for consistent design
- **React Router** - Client-side routing
- **Axios** - HTTP client for API calls
- **React Hook Form** - Form handling with validation
- **Yup** - Schema validation
- **Context API** - State management for authentication

## Getting Started

### Prerequisites

- Node.js 16+ and npm
- Backend API running on http://localhost:8081

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd loan-management-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The application will be available at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

## Project Structure

```
src/
├── components/          # Reusable UI components
│   └── Layout.js       # Main layout with navigation
├── context/            # React Context for state management
│   └── AuthContext.js  # Authentication context
├── pages/              # Page components
│   ├── Dashboard.js    # Main dashboard
│   ├── Customers.js    # Customer management
│   ├── LoanApplications.js # Application management
│   └── Loans.js        # Loan management
├── services/           # API service layer
│   ├── api.js          # Axios configuration
│   ├── authService.js  # Authentication API
│   ├── customerService.js # Customer API
│   ├── loanApplicationService.js # Application API
│   ├── loanService.js  # Loan API
│   └── dashboardService.js # Dashboard API
├── App.js              # Main application component
└── index.js            # Application entry point
```

## API Integration

The frontend communicates with the Spring Boot backend API. Configure the base URL in `src/services/api.js`:

```javascript
const API_BASE_URL = 'http://localhost:8081/api';
```

## Authentication

The application uses Basic Authentication. Login credentials are stored in the AuthContext and automatically included in API requests.

## Features Overview

### Smart Risk Assessment
- Real-time risk calculation based on credit score, income, and loan amount
- Color-coded risk levels (Low, Medium, High)
- Auto-approval for low-risk applications

### Approval Workflow
- Hierarchical approval process (Junior → Senior → Manager)
- Comment tracking for approval decisions
- Status tracking with visual indicators

### Dashboard Analytics
- Customer statistics
- Application status distribution
- Risk assessment overview
- Loan portfolio summary

## Development

### Adding New Pages

1. Create a new component in `src/pages/`
2. Add the route to `src/App.js`
3. Add navigation item to `src/components/Layout.js`

### API Services

Create new service files in `src/services/` following the existing pattern:

```javascript
import api from './api';

const newService = {
  getData: async () => {
    const response = await api.get('/endpoint');
    return response.data;
  },
  // ... other methods
};

export default newService;
```
