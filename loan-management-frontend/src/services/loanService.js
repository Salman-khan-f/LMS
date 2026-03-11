import api from './api';

const loanService = {
  getAllLoans: async () => {
    const response = await api.get('/loans');
    return response.data;
  },

  getLoanById: async (id) => {
    const response = await api.get(`/loans/${id}`);
    return response.data;
  },

  createLoan: async (loanData) => {
    const response = await api.post('/loans', loanData);
    return response.data;
  },

  updateLoan: async (id, loanData) => {
    const response = await api.put(`/loans/${id}`, loanData);
    return response.data;
  },

  deleteLoan: async (id) => {
    await api.delete(`/loans/${id}`);
  },

  getLoansByCustomer: async (customerId) => {
    const response = await api.get(`/loans/customer/${customerId}`);
    return response.data;
  },

  calculateLoanSchedule: async (loanId) => {
    const response = await api.get(`/loans/${loanId}/schedule`);
    return response.data;
  },

  getLoanPayments: async (loanId) => {
    const response = await api.get(`/loans/${loanId}/payments`);
    return response.data;
  },
};

export default loanService;