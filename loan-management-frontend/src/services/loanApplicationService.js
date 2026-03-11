import api from './api';

class LoanApplicationService {
  async getAllApplications() {
    const response = await api.get('/loan-applications');
    return response.data;
  }

  async getApplicationById(id) {
    const response = await api.get(`/loan-applications/${id}`);
    return response.data;
  }

  async createApplication(applicationData) {
    const response = await api.post('/loan-applications', applicationData);
    return response.data;
  }

  async getApplicationsByCustomer(customerId) {
    const response = await api.get(`/loan-applications/customer/${customerId}`);
    return response.data;
  }

  async getApplicationsByStatus(status) {
    const response = await api.get(`/loan-applications/status/${status}`);
    return response.data;
  }

  async updateApplicationStatus(id, status, comments) {
    const response = await api.put(`/loan-applications/${id}/status`, {
      status,
      comments
    });
    return response.data;
  }

  async getAutoApprovalEligible() {
    const response = await api.get('/loan-applications/auto-approval-eligible');
    return response.data;
  }

  async getApplicationsByRiskLevel(riskLevel) {
    const response = await api.get(`/loan-applications/risk-level/${riskLevel}`);
    return response.data;
  }
}

export default new LoanApplicationService();