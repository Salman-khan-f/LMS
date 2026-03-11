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
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
} from '@mui/icons-material';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import customerService from '../services/customerService';

const schema = yup.object({
  firstName: yup.string().required('First name is required'),
  lastName: yup.string().required('Last name is required'),
  email: yup.string().email('Invalid email').required('Email is required'),
  phoneNumber: yup.string().required('Phone number is required'),
  dateOfBirth: yup.date().required('Date of birth is required'),
  address: yup.string().required('Address is required'),
  city: yup.string().required('City is required'),
  state: yup.string().required('State is required'),
  zipCode: yup.string().required('Zip code is required'),
  annualIncome: yup.number().positive('Income must be positive').required('Annual income is required'),
  employmentStatus: yup.string().required('Employment status is required'),
  creditScore: yup.number().min(300).max(850).required('Credit score is required'),
});

const Customers = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [open, setOpen] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const data = await customerService.getAllCustomers();
      setCustomers(data);
    } catch (err) {
      setError('Failed to load customers');
      console.error('Customers error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDialog = (customer = null) => {
    setEditingCustomer(customer);
    if (customer) {
      reset(customer);
    } else {
      reset({
        firstName: '',
        lastName: '',
        email: '',
        phoneNumber: '',
        dateOfBirth: '',
        address: '',
        city: '',
        state: '',
        zipCode: '',
        annualIncome: '',
        employmentStatus: 'FULL_TIME',
        creditScore: '',
      });
    }
    setOpen(true);
  };

  const handleCloseDialog = () => {
    setOpen(false);
    setEditingCustomer(null);
  };

  const onSubmit = async (data) => {
    try {
      if (editingCustomer) {
        await customerService.updateCustomer(editingCustomer.id, data);
      } else {
        await customerService.createCustomer(data);
      }
      fetchCustomers();
      handleCloseDialog();
    } catch (err) {
      // Surface server-side validation or conflict messages when available
      const resp = err?.response?.data;
      let msg = 'Failed to save customer';
      if (resp) {
        if (Array.isArray(resp.details)) msg = resp.details.join('; ');
        else if (resp.message) msg = resp.message;
        else msg = JSON.stringify(resp);
      }
      setError(msg);
      console.error('Save customer error:', err);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this customer?')) {
      try {
        await customerService.deleteCustomer(id);
        fetchCustomers();
      } catch (err) {
        setError('Failed to delete customer');
        console.error('Delete customer error:', err);
      }
    }
  };

  const handleSearch = async () => {
    if (searchTerm.trim()) {
      try {
        const results = await customerService.searchCustomersByLastName(searchTerm);
        setCustomers(results);
      } catch (err) {
        setError('Search failed');
      }
    } else {
      fetchCustomers();
    }
  };

  const getCreditScoreColor = (score) => {
    if (score >= 750) return 'success';
    if (score >= 650) return 'warning';
    return 'error';
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
        <Typography variant="h4">Customers</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpenDialog()}
        >
          Add Customer
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Box display="flex" gap={2} mb={3}>
        <TextField
          label="Search by Last Name"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
        />
        <Button
          variant="outlined"
          startIcon={<SearchIcon />}
          onClick={handleSearch}
        >
          Search
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Email</TableCell>
              <TableCell>Phone</TableCell>
              <TableCell>Credit Score</TableCell>
              <TableCell>Annual Income</TableCell>
              <TableCell>Employment</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {customers.map((customer) => (
              <TableRow key={customer.id}>
                <TableCell>{`${customer.firstName} ${customer.lastName}`}</TableCell>
                <TableCell>{customer.email}</TableCell>
                <TableCell>{customer.phoneNumber}</TableCell>
                <TableCell>
                  <Chip
                    label={customer.creditScore}
                    color={getCreditScoreColor(customer.creditScore)}
                    size="small"
                  />
                </TableCell>
                <TableCell>${customer.annualIncome?.toLocaleString()}</TableCell>
                <TableCell>{customer.employmentStatus?.replace('_', ' ')}</TableCell>
                <TableCell>
                  <IconButton
                    color="primary"
                    onClick={() => handleOpenDialog(customer)}
                  >
                    <EditIcon />
                  </IconButton>
                  <IconButton
                    color="error"
                    onClick={() => handleDelete(customer.id)}
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
            {editingCustomer ? 'Edit Customer' : 'Add New Customer'}
          </DialogTitle>
          <DialogContent>
            <Box display="flex" flexDirection="column" gap={2} pt={1}>
              <Box display="flex" gap={2}>
                <TextField
                  {...register('firstName')}
                  label="First Name"
                  fullWidth
                  error={!!errors.firstName}
                  helperText={errors.firstName?.message}
                />
                <TextField
                  {...register('lastName')}
                  label="Last Name"
                  fullWidth
                  error={!!errors.lastName}
                  helperText={errors.lastName?.message}
                />
              </Box>

              <Box display="flex" gap={2}>
                <TextField
                  {...register('email')}
                  label="Email"
                  fullWidth
                  error={!!errors.email}
                  helperText={errors.email?.message}
                />
                <TextField
                  {...register('phoneNumber')}
                  label="Phone Number"
                  fullWidth
                  error={!!errors.phoneNumber}
                  helperText={errors.phoneNumber?.message}
                />
              </Box>

              <TextField
                {...register('dateOfBirth')}
                label="Date of Birth"
                type="date"
                InputLabelProps={{ shrink: true }}
                fullWidth
                error={!!errors.dateOfBirth}
                helperText={errors.dateOfBirth?.message}
              />

              <TextField
                {...register('address')}
                label="Address"
                fullWidth
                error={!!errors.address}
                helperText={errors.address?.message}
              />

              <Box display="flex" gap={2}>
                <TextField
                  {...register('city')}
                  label="City"
                  fullWidth
                  error={!!errors.city}
                  helperText={errors.city?.message}
                />
                <TextField
                  {...register('state')}
                  label="State"
                  fullWidth
                  error={!!errors.state}
                  helperText={errors.state?.message}
                />
                <TextField
                  {...register('zipCode')}
                  label="Zip Code"
                  fullWidth
                  error={!!errors.zipCode}
                  helperText={errors.zipCode?.message}
                />
              </Box>

              <Box display="flex" gap={2}>
                <TextField
                  {...register('annualIncome')}
                  label="Annual Income"
                  type="number"
                  fullWidth
                  error={!!errors.annualIncome}
                  helperText={errors.annualIncome?.message}
                />
                <TextField
                  {...register('creditScore')}
                  label="Credit Score"
                  type="number"
                  fullWidth
                  error={!!errors.creditScore}
                  helperText={errors.creditScore?.message}
                />
              </Box>

              <TextField
                {...register('employmentStatus')}
                label="Employment Status"
                select
                fullWidth
                error={!!errors.employmentStatus}
                helperText={errors.employmentStatus?.message}
                SelectProps={{ native: true }}
              >
                <option value="FULL_TIME">Full Time</option>
                <option value="PART_TIME">Part Time</option>
                <option value="SELF_EMPLOYED">Self Employed</option>
                <option value="UNEMPLOYED">Unemployed</option>
              </TextField>
            </Box>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog}>Cancel</Button>
            <Button type="submit" variant="contained">
              {editingCustomer ? 'Update' : 'Create'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};

export default Customers;