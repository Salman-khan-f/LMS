import api from './api';

class CustomerService {
  async getAllCustomers() {
    const response = await api.get('/customers');
    return response.data;
  }

  async getCustomerById(id) {
    const response = await api.get(`/customers/${id}`);
    return response.data;
  }

  async createCustomer(customerData) {
    const response = await api.post('/customers', customerData);
    return response.data;
  }

  async updateCustomer(id, customerData) {
    const response = await api.put(`/customers/${id}`, customerData);
    return response.data;
  }

  async deleteCustomer(id) {
    await api.delete(`/customers/${id}`);
  }

  async searchCustomersByLastName(lastName) {
    const response = await api.get(`/customers/search`, {
      params: { lastName }
    });
    return response.data;
  }

  async getCustomersByIncomeRange(minIncome, maxIncome) {
    const response = await api.get('/customers/income-range', {
      params: { minIncome, maxIncome }
    });
    return response.data;
  }

  async getCustomersByCreditScore(minScore) {
    const response = await api.get('/customers/credit-score', {
      params: { minScore }
    });
    return response.data;
  }
}

export default new CustomerService();