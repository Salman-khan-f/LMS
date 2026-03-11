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
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import loanService from '../services/loanService';
import customerService from '../services/customerService';
import loanApplicationService from '../services/loanApplicationService';

const schema = yup.object({
  customerId: yup.number().required('Customer is required'),
  loanApplicationId: yup.number().required('Loan application is required'),
  principalAmount: yup.number().positive('Principal must be positive').required('Principal amount is required'),
  interestRate: yup.number().min(0).max(50).required('Interest rate is required'),
  termMonths: yup.number().positive('Term must be positive').required('Term is required'),
  disbursementDate: yup.date().required('Disbursement date is required'),
});

const Loans = () => {
  const [loans, setLoans] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [open, setOpen] = useState(false);
  const [editingLoan, setEditingLoan] = useState(null);

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    fetchLoans();
    fetchCustomers();
    fetchApplications();
  }, []);

  const fetchLoans = async () => {
    try {
      setLoading(true);
      const data = await loanService.getAllLoans();
      setLoans(data);
    } catch (err) {
      setError('Failed to load loans');
      console.error('Loans error:', err);
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

  const fetchApplications = async () => {
    try {
      const data = await loanApplicationService.getAllApplications();
      setApplications(data.filter(app => app.status === 'APPROVED' || app.status === 'AUTO_APPROVED'));
    } catch (err) {
      console.error('Applications error:', err);
    }
  };

  const handleOpenDialog = (loan = null) => {
    setEditingLoan(loan);
    if (loan) {
      reset({
        customerId: loan.customer?.id || '',
        loanApplicationId: loan.loanApplication?.id || '',
        principalAmount: loan.principalAmount || '',
        interestRate: loan.interestRate || '',
        termMonths: loan.termMonths || '',
        disbursementDate: loan.disbursementDate ? new Date(loan.disbursementDate).toISOString().split('T')[0] : '',
      });
    } else {
      reset({
        customerId: '',
        loanApplicationId: '',
        principalAmount: '',
        interestRate: '',
        termMonths: '',
        disbursementDate: '',
      });
    }
    setOpen(true);
  };

  const handleCloseDialog = () => {
    setOpen(false);
    setEditingLoan(null);
  };

  const onSubmit = async (data) => {
    try {
      if (editingLoan) {
        await loanService.updateLoan(editingLoan.id, data);
      } else {
        await loanService.createLoan(data);
      }
      fetchLoans();
      handleCloseDialog();
    } catch (err) {
      setError('Failed to save loan');
      console.error('Save loan error:', err);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this loan?')) {
      try {
        await loanService.deleteLoan(id);
        fetchLoans();
      } catch (err) {
        setError('Failed to delete loan');
        console.error('Delete loan error:', err);
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'CLOSED': return 'default';
      case 'DEFAULTED': return 'error';
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
        <Typography variant="h4">Loans</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpenDialog()}
        >
          Add Loan
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
              <TableCell>Principal</TableCell>
              <TableCell>Interest Rate</TableCell>
              <TableCell>Term (Months)</TableCell>
              <TableCell>Outstanding Balance</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Disbursement Date</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loans.map((loan) => (
              <TableRow key={loan.id}>
                <TableCell>
                  {loan.customer
                    ? `${loan.customer.firstName} ${loan.customer.lastName}`
                    : 'N/A'
                  }
                </TableCell>
                <TableCell>${loan.principalAmount?.toLocaleString()}</TableCell>
                <TableCell>{loan.interestRate}%</TableCell>
                <TableCell>{loan.termMonths}</TableCell>
                <TableCell>${loan.outstandingBalance?.toLocaleString()}</TableCell>
                <TableCell>
                  <Chip
                    label={loan.status?.replace('_', ' ')}
                    color={getStatusColor(loan.status)}
                    size="small"
                  />
                </TableCell>
                <TableCell>
                  {loan.disbursementDate
                    ? new Date(loan.disbursementDate).toLocaleDateString()
                    : 'N/A'
                  }
                </TableCell>
                <TableCell>
                  <IconButton
                    color="primary"
                    onClick={() => handleOpenDialog(loan)}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    color="error"
                    onClick={() => handleDelete(loan.id)}
                  >
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogTitle>
            {editingLoan ? 'Edit Loan' : 'Add New Loan'}
          </DialogTitle>
          <DialogContent>
            <Box display="flex" flexDirection="column" gap={2} pt={1}>
              <FormControl fullWidth error={!!errors.customerId}>
                <InputLabel>Customer</InputLabel>
                <Select
                  {...register('customerId')}
                  label="Customer"
                  defaultValue={editingLoan?.customer?.id || ''}
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

              <FormControl fullWidth error={!!errors.loanApplicationId}>
                <InputLabel>Loan Application</InputLabel>
                <Select
                  {...register('loanApplicationId')}
                  label="Loan Application"
                  defaultValue={editingLoan?.loanApplication?.id || ''}
                >
                  {applications.map((app) => (
                    <MenuItem key={app.id} value={app.id}>
                      {`Application #${app.id} - $${app.loanAmount} (${app.customer?.firstName} ${app.customer?.lastName})`}
                    </MenuItem>
                  ))}
                </Select>
                {errors.loanApplicationId && (
                  <Typography variant="caption" color="error">
                    {errors.loanApplicationId.message}
                  </Typography>
                )}
              </FormControl>

              <Box display="flex" gap={2}>
                <TextField
                  {...register('principalAmount')}
                  label="Principal Amount"
                  type="number"
                  fullWidth
                  error={!!errors.principalAmount}
                  helperText={errors.principalAmount?.message}
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

              <Box display="flex" gap={2}>
                <TextField
                  {...register('termMonths')}
                  label="Term (Months)"
                  type="number"
                  fullWidth
                  error={!!errors.termMonths}
                  helperText={errors.termMonths?.message}
                />
                <TextField
                  {...register('disbursementDate')}
                  label="Disbursement Date"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  fullWidth
                  error={!!errors.disbursementDate}
                  helperText={errors.disbursementDate?.message}
                />
              </Box>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog}>Cancel</Button>
            <Button type="submit" variant="contained">
              {editingLoan ? 'Update' : 'Create'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

export default Loans;