import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  CircularProgress,
  Alert,
  IconButton,
  Chip,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  CheckCircle as ApproveIcon,
  Cancel as RejectIcon,
} from '@mui/icons-material';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import loanApplicationService from '../services/loanApplicationService';
import customerService from '../services/customerService';

const schema = yup.object({
  customerId: yup.number().required('Customer is required'),
  loanAmount: yup.number().positive('Loan amount must be positive').required('Loan amount is required'),
  loanPurpose: yup.string().required('Loan purpose is required'),
  loanTermMonths: yup.number().positive('Term must be positive').required('Loan term is required'),
  interestRate: yup.number().min(0).max(50).required('Interest rate is required'),
});

const LoanApplications = () => {
  const [applications, setApplications] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [open, setOpen] = useState(false);
  const [editingApplication, setEditingApplication] = useState(null);
  const [selectedApplication, setSelectedApplication] = useState(null);
  const [approvalDialogOpen, setApprovalDialogOpen] = useState(false);
  const [approvalAction, setApprovalAction] = useState('');
  const [approvalComments, setApprovalComments] = useState('');

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    fetchApplications();
    fetchCustomers();
  }, []);

  const fetchApplications = async () => {
    try {
      setLoading(true);
      const data = await loanApplicationService.getAllApplications();
      setApplications(data);
    } catch (err) {
      setError('Failed to load loan applications');
      console.error('Applications error:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCustomers = async () => {
    try {
      const data = await customerService.getAllCustomers();
      setCustomers(data);
    } catch (err) {
      console.error('Customers error:', err);
    }
  };

  const handleOpenDialog = (application = null) => {
    setEditingApplication(application);
    if (application) {
      reset({
        customerId: application.customer?.id || '',
        loanAmount: application.loanAmount || '',
        loanPurpose: application.loanPurpose || '',
        loanTermMonths: application.loanTermMonths || '',
        interestRate: application.interestRate || '',
      });
    } else {
      reset({
        customerId: '',
        loanAmount: '',
        loanPurpose: '',
        loanTermMonths: '',
        interestRate: '',
      });
    }
    setOpen(true);
  };

  const handleCloseDialog = () => {
    setOpen(false);
    setEditingApplication(null);
  };

  const onSubmit = async (data) => {
    try {
      if (editingApplication) {
        await loanApplicationService.updateApplication(editingApplication.id, data);
      } else {
        await loanApplicationService.createApplication(data);
      }
      fetchApplications();
      handleCloseDialog();
    } catch (err) {
      setError('Failed to save loan application');
      console.error('Save application error:', err);
    }
  };

  const handleApprovalDialog = (application, action) => {
    setSelectedApplication(application);
    setApprovalAction(action);
    setApprovalComments('');
    setApprovalDialogOpen(true);
  };

  const handleApprovalSubmit = async () => {
    try {
      if (approvalAction === 'approve') {
        await loanApplicationService.approveApplication(selectedApplication.id, approvalComments);
      } else {
        await loanApplicationService.rejectApplication(selectedApplication.id, approvalComments);
      }
      fetchApplications();
      setApprovalDialogOpen(false);
    } catch (err) {
      setError('Failed to process approval');
      console.error('Approval error:', err);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'warning';
      case 'APPROVED': return 'success';
      case 'REJECTED': return 'error';
      case 'AUTO_APPROVED': return 'success';
      case 'UNDER_REVIEW': return 'info';
      default: return 'default';
    }
  };

  const getRiskColor = (risk) => {
    switch (risk) {
      case 'LOW': return 'success';
      case 'MEDIUM': return 'warning';
      case 'HIGH': return 'error';
      default: return 'default';
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Loan Applications</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpenDialog()}
        >
          New Application
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Customer</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Purpose</TableCell>
              <TableCell>Term (Months)</TableCell>
              <TableCell>Risk Level</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Applied Date</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {applications.map((application) => (
              <TableRow key={application.id}>
                <TableCell>
                  {application.customer
                    ? `${application.customer.firstName} ${application.customer.lastName}`
                    : 'N/A'
                  }
                </TableCell>
                <TableCell>${application.loanAmount?.toLocaleString()}</TableCell>
                <TableCell>{application.loanPurpose}</TableCell>
                <TableCell>{application.loanTermMonths}</TableCell>
                <TableCell>
                  <Chip
                    label={application.riskLevel || 'N/A'}
                    color={getRiskColor(application.riskLevel)}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  <Chip
                    label={application.status?.replace('_', ' ')}
                    color={getStatusColor(application.status)}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {application.applicationDate
                    ? new Date(application.applicationDate).toLocaleDateString()
                    : 'N/A'
                  }
                </TableCell>
                <TableCell>
                  <IconButton
                    color="primary"
                    onClick={() => handleOpenDialog(application)}
                  >
                    <EditIcon />
                  </IconButton>
                  {application.status === 'PENDING' && (
                    <>
                      <IconButton
                        color="success"
                        onClick={() => handleApprovalDialog(application, 'approve')}
                      >
                        <ApproveIcon />
                      </IconButton>
                      <IconButton
                        color="error"
                        onClick={() => handleApprovalDialog(application, 'reject')}
                      >
                        <RejectIcon />
                      </IconButton>
                    </>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Create/Edit Dialog */}
      <Dialog open={open} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogTitle>
            {editingApplication ? 'Edit Loan Application' : 'New Loan Application'}
          </DialogTitle>
          <DialogContent>
            <Box display="flex" flexDirection="column" gap={2} pt={1}>
              <FormControl fullWidth error={!!errors.customerId}>
                <InputLabel>Customer</InputLabel>
                <Select
                  {...register('customerId')}
                  label="Customer"
                  defaultValue={editingApplication?.customer?.id || ''}
                >
                  {customers.map((customer) => (
                    <MenuItem key={customer.id} value={customer.id}>
                      {`${customer.firstName} ${customer.lastName} (${customer.email})`}
                    </MenuItem>
                  ))}
                </Select>
                {errors.customerId && (
                  <Typography variant="caption" color="error">
                    {errors.customerId.message}
                  </Typography>
                )}
              </FormControl>

              <Box display="flex" gap={2}>
                <TextField
                  {...register('loanAmount')}
                  label="Loan Amount"
                  type="number"
                  fullWidth
                  error={!!errors.loanAmount}
                  helperText={errors.loanAmount?.message}
                />
                <TextField
                  {...register('loanTermMonths')}
                  label="Term (Months)"
                  type="number"
                  fullWidth
                  error={!!errors.loanTermMonths}
                  helperText={errors.loanTermMonths?.message}
                />
              </Box>

              <TextField
                {...register('loanPurpose')}
                label="Loan Purpose"
                fullWidth
                error={!!errors.loanPurpose}
                helperText={errors.loanPurpose?.message}
              />

              <TextField
                {...register('interestRate')}
                label="Interest Rate (%)"
                type="number"
                step="0.01"
                fullWidth
                error={!!errors.interestRate}
                helperText={errors.interestRate?.message}
              />
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog}>Cancel</Button>
            <Button type="submit" variant="contained">
              {editingApplication ? 'Update' : 'Create'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Approval Dialog */}
      <Dialog open={approvalDialogOpen} onClose={() => setApprovalDialogOpen(false)}>
        <DialogTitle>
          {approvalAction === 'approve' ? 'Approve Application' : 'Reject Application'}
        </DialogTitle>
        <DialogContent>
          <TextField
            label="Comments"
            multiline
            rows={4}
            fullWidth
            value={approvalComments}
            onChange={(e) => setApprovalComments(e.target.value)}
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setApprovalDialogOpen(false)}>Cancel</Button>
          <Button
            onClick={handleApprovalSubmit}
            variant="contained"
            color={approvalAction === 'approve' ? 'success' : 'error'}
          >
            {approvalAction === 'approve' ? 'Approve' : 'Reject'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default LoanApplications;