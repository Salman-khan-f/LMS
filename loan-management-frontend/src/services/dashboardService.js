import api from './api';

class DashboardService {
  async getDashboardSummary() {
    const response = await api.get('/dashboard/summary');
    return response.data;
  }

  async getRiskAnalysis() {
    const response = await api.get('/dashboard/risk-analysis');
    return response.data;
  }
}

export default new DashboardService();