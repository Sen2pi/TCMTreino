import React, { useState, useEffect } from "react";
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Chip,
  Fab,
  Tooltip,
  Alert,
  Snackbar
} from "@mui/material";
import {
  Add,
  Edit,
  Delete,
  AccountBalance,
  SwapHoriz,
  Refresh
} from "@mui/icons-material";
import { motion } from "framer-motion";
import { treasuryService } from "../services/treasury";
import AnimatedButton from "../components/common/AnimatedButton";
import LoaderBars from "../components/common/LoaderBars";

const accountTypes = ["CHECKING", "SAVINGS", "INVESTMENT", "MONEY_MARKET"];
const accountStatuses = ["ACTIVE", "INACTIVE", "CLOSED"];

export default function Treasury() {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [openDialog, setOpenDialog] = useState(false);
  const [openTransferDialog, setOpenTransferDialog] = useState(false);
  const [editingAccount, setEditingAccount] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
  
  const [accountForm, setAccountForm] = useState({
    accountNumber: "",
    accountName: "",
    accountType: "CHECKING",
    balance: "",
    currency: "USD",
    accountStatus: "ACTIVE"
  });

  const [transferForm, setTransferForm] = useState({
    fromAccountId: "",
    toAccountId: "",
    amount: ""
  });

  const loadAccounts = async () => {
    try {
      setLoading(true);
      const data = await treasuryService.getAllAccounts();
      setAccounts(data);
    } catch (error) {
      showSnackbar("Failed to load accounts", "error");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAccounts();
  }, []);

  const showSnackbar = (message, severity = "success") => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const handleOpenDialog = (account = null) => {
    if (account) {
      setEditingAccount(account);
      setAccountForm({
        accountNumber: account.accountNumber,
        accountName: account.accountName,
        accountType: account.accountType,
        balance: account.balance.toString(),
        currency: account.currency,
        accountStatus: account.accountStatus
      });
    } else {
      setEditingAccount(null);
      setAccountForm({
        accountNumber: "",
        accountName: "",
        accountType: "CHECKING",
        balance: "",
        currency: "USD",
        accountStatus: "ACTIVE"
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingAccount(null);
  };

  const handleSubmit = async () => {
    try {
      const formData = {
        ...accountForm,
        balance: parseFloat(accountForm.balance)
      };

      if (editingAccount) {
        await treasuryService.updateAccount(editingAccount.id, formData);
        showSnackbar("Account updated successfully");
      } else {
        await treasuryService.createAccount(formData);
        showSnackbar("Account created successfully");
      }
      
      handleCloseDialog();
      loadAccounts();
    } catch (error) {
      showSnackbar(error.response?.data || "Operation failed", "error");
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this account?")) {
      try {
        await treasuryService.deleteAccount(id);
        showSnackbar("Account deleted successfully");
        loadAccounts();
      } catch (error) {
        showSnackbar("Failed to delete account", "error");
      }
    }
  };

  const handleTransfer = async () => {
    try {
      await treasuryService.transferFunds({
        fromAccountId: parseInt(transferForm.fromAccountId),
        toAccountId: parseInt(transferForm.toAccountId),
        amount: parseFloat(transferForm.amount)
      });
      
      showSnackbar("Funds transferred successfully");
      setOpenTransferDialog(false);
      setTransferForm({ fromAccountId: "", toAccountId: "", amount: "" });
      loadAccounts();
    } catch (error) {
      showSnackbar(error.response?.data || "Transfer failed", "error");
    }
  };

  const formatCurrency = (amount, currency = "USD") => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency
    }).format(amount);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "ACTIVE": return "success";
      case "INACTIVE": return "warning";
      case "CLOSED": return "error";
      default: return "default";
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <LoaderBars />
      </Box>
    );
  }

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 3 }}>
        <Box>
          <Typography variant="h4" gutterBottom>
            Treasury Management
          </Typography>
          <Typography variant="body1" color="textSecondary">
            Manage treasury accounts and fund transfers
          </Typography>
        </Box>
        
        <Box sx={{ display: "flex", gap: 2 }}>
          <Button
            variant="outlined"
            startIcon={<SwapHoriz />}
            onClick={() => setOpenTransferDialog(true)}
            disabled={accounts.length < 2}
          >
            Transfer Funds
          </Button>
          
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={loadAccounts}
          >
            Refresh
          </Button>
        </Box>
      </Box>

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={4}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
          >
            <Card>
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <AccountBalance color="primary" sx={{ fontSize: 40 }} />
                  <Box>
                    <Typography variant="h6">Total Accounts</Typography>
                    <Typography variant="h4">{accounts.length}</Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
          >
            <Card>
              <CardContent>
                <Typography variant="h6">Total Balance</Typography>
                <Typography variant="h4">
                  {formatCurrency(accounts.reduce((sum, acc) => sum + acc.balance, 0))}
                </Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
        
        <Grid item xs={12} md={4}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <Card>
              <CardContent>
                <Typography variant="h6">Active Accounts</Typography>
                <Typography variant="h4">
                  {accounts.filter(acc => acc.accountStatus === "ACTIVE").length}
                </Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
      </Grid>

      {/* Accounts Table */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4 }}
      >
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Account Number</TableCell>
                <TableCell>Account Name</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Balance</TableCell>
                <TableCell>Currency</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {accounts.map((account) => (
                <TableRow key={account.id} hover>
                  <TableCell>{account.accountNumber}</TableCell>
                  <TableCell>{account.accountName}</TableCell>
                  <TableCell>{account.accountType}</TableCell>
                  <TableCell>{formatCurrency(account.balance, account.currency)}</TableCell>
                  <TableCell>{account.currency}</TableCell>
                  <TableCell>
                    <Chip 
                      label={account.accountStatus} 
                      color={getStatusColor(account.accountStatus)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <IconButton
                      size="small"
                      onClick={() => handleOpenDialog(account)}
                      color="primary"
                    >
                      <Edit />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleDelete(account.id)}
                      color="error"
                    >
                      <Delete />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </motion.div>

      {/* Floating Action Button */}
      <Tooltip title="Add New Account">
        <Fab
          color="primary"
          aria-label="add"
          onClick={() => handleOpenDialog()}
          sx={{
            position: "fixed",
            bottom: 24,
            right: 24,
          }}
        >
          <Add />
        </Fab>
      </Tooltip>

      {/* Account Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          {editingAccount ? "Edit Account" : "Create New Account"}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Account Number"
                value={accountForm.accountNumber}
                onChange={(e) => setAccountForm({...accountForm, accountNumber: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Account Name"
                value={accountForm.accountName}
                onChange={(e) => setAccountForm({...accountForm, accountName: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                select
                label="Account Type"
                value={accountForm.accountType}
                onChange={(e) => setAccountForm({...accountForm, accountType: e.target.value})}
              >
                {accountTypes.map((type) => (
                  <MenuItem key={type} value={type}>
                    {type}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="number"
                label="Balance"
                value={accountForm.balance}
                onChange={(e) => setAccountForm({...accountForm, balance: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Currency"
                value={accountForm.currency}
                onChange={(e) => setAccountForm({...accountForm, currency: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                select
                label="Status"
                value={accountForm.accountStatus}
                onChange={(e) => setAccountForm({...accountForm, accountStatus: e.target.value})}
              >
                {accountStatuses.map((status) => (
                  <MenuItem key={status} value={status}>
                    {status}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <AnimatedButton
            onClick={handleSubmit}
            variant="contained"
            disabled={!accountForm.accountNumber || !accountForm.accountName}
          >
            {editingAccount ? "Update" : "Create"}
          </AnimatedButton>
        </DialogActions>
      </Dialog>

      {/* Transfer Dialog */}
      <Dialog open={openTransferDialog} onClose={() => setOpenTransferDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Transfer Funds</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                select
                label="From Account"
                value={transferForm.fromAccountId}
                onChange={(e) => setTransferForm({...transferForm, fromAccountId: e.target.value})}
              >
                {accounts.map((account) => (
                  <MenuItem key={account.id} value={account.id}>
                    {account.accountName} ({formatCurrency(account.balance)})
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                select
                label="To Account"
                value={transferForm.toAccountId}
                onChange={(e) => setTransferForm({...transferForm, toAccountId: e.target.value})}
              >
                {accounts
                  .filter(account => account.id.toString() !== transferForm.fromAccountId)
                  .map((account) => (
                    <MenuItem key={account.id} value={account.id}>
                      {account.accountName}
                    </MenuItem>
                  ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                type="number"
                label="Amount"
                value={transferForm.amount}
                onChange={(e) => setTransferForm({...transferForm, amount: e.target.value})}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenTransferDialog(false)}>Cancel</Button>
          <AnimatedButton
            onClick={handleTransfer}
            variant="contained"
            disabled={!transferForm.fromAccountId || !transferForm.toAccountId || !transferForm.amount}
          >
            Transfer
          </AnimatedButton>
        </DialogActions>
      </Dialog>

      {/* Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
      >
        <Alert onClose={handleCloseSnackbar} severity={snackbar.severity}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
}