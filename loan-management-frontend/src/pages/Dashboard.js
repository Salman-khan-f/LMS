import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  People as PeopleIcon,
  Assignment as AssignmentIcon,
  AccountBalance as LoanIcon,
  Payment as PaymentIcon,
  TrendingUp as TrendingUpIcon,
  Warning as WarningIcon,
} from '@mui/icons-material';
import dashboardService from '../services/dashboardService';

const Dashboard = () => {
  const [summary, setSummary] = useState(null);
  const [riskAnalysis, setRiskAnalysis] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [summaryData, riskData] = await Promise.all([
          dashboardService.getDashboardSummary(),
          dashboardService.getRiskAnalysis(),
        ]);
        setSummary(summaryData);
        setRiskAnalysis(riskData);
      } catch (err) {
        setError('Failed to load dashboard data');
        console.error('Dashboard error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  const StatCard = ({ title, value, icon, color = 'primary' }) => (
    <Card>
      <CardContent>
        <Box display="flex" alignItems="center" justifyContent="space-between">
          <Box>
            <Typography color="textSecondary" gutterBottom>
              {title}
            </Typography>
            <Typography variant="h4" component="div">
              {value}
            </Typography>
          </Box>
          <Box color={`${color}.main`}>
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>

      <Grid container spacing={3}>
        {/* Customer Statistics */}
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Customers"
            value={summary?.customers?.total || 0}
            icon={<PeopleIcon fontSize="large" />}
          />
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="New Customers (This Month)"
            value={summary?.customers?.newThisMonth || 0}
            icon={<TrendingUpIcon fontSize="large" />}
            color="success"
          />
        </Grid>

        {/* Application Statistics */}
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Applications"
            value={summary?.applications?.total || 0}
            icon={<AssignmentIcon fontSize="large" />}
          />
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Pending Applications"
            value={summary?.applications?.pending || 0}
            icon={<WarningIcon fontSize="large" />}
            color="warning"
          />
        </Grid>

        {/* Loan Statistics */}
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Active Loans"
            value={summary?.loans?.active || 0}
            icon={<LoanIcon fontSize="large" />}
          />
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Outstanding Balance"
            value={`$${(summary?.loans?.totalOutstandingBalance || 0).toLocaleString()}`}
            icon={<PaymentIcon fontSize="large" />}
            color="secondary"
          />
        </Grid>

        {/* Risk Analysis */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Risk Assessment Distribution
              </Typography>
              <Box display="flex" justifyContent="space-around" mt={2}>
                <Box textAlign="center">
                  <Typography variant="h4" color="success.main">
                    {riskAnalysis?.riskDistribution?.low || 0}
                  </Typography>
                  <Typography color="textSecondary">Low Risk</Typography>
                </Box>
                <Box textAlign="center">
                  <Typography variant="h4" color="warning.main">
                    {riskAnalysis?.riskDistribution?.medium || 0}
                  </Typography>
                  <Typography color="textSecondary">Medium Risk</Typography>
                </Box>
                <Box textAlign="center">
                  <Typography variant="h4" color="error.main">
                    {riskAnalysis?.riskDistribution?.high || 0}
                  </Typography>
                  <Typography color="textSecondary">High Risk</Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Auto Approval Stats */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Smart Features
              </Typography>
              <Box mt={2}>
                <Box display="flex" justifyContent="space-between" mb={2}>
                  <Typography>Auto-Approved Applications</Typography>
                  <Typography variant="h6" color="success.main">
                    {summary?.riskAssessment?.autoApprovalEligible || 0}
                  </Typography>
                </Box>
                <Box display="flex" justifyContent="space-between" mb={2}>
                  <Typography>Pending Approvals</Typography>
                  <Typography variant="h6" color="warning.main">
                    {summary?.approvals?.pending || 0}
                  </Typography>
                </Box>
                <Box display="flex" justifyContent="space-between">
                  <Typography>Recent Payments</Typography>
                  <Typography variant="h6" color="primary.main">
                    ${(summary?.payments?.recentPayments || 0).toLocaleString()}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard;