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
  Snackbar,
  CardActions
} from "@mui/material";
import {
  Add,
  Edit,
  Delete,
  Security,
  TrendingUp,
  Refresh,
  Visibility
} from "@mui/icons-material";
import { motion } from "framer-motion";
import { collateralService } from "../services/collateral";
import AnimatedButton from "../components/common/AnimatedButton";
import LoaderBars from "../components/common/LoaderBars";

const collateralTypes = ["BONDS", "STOCKS", "REAL_ESTATE", "COMMODITIES"];
const collateralStatuses = ["ELIGIBLE", "INELIGIBLE", "PLEDGED", "MATURED"];
const ratings = ["AAA", "AA", "A", "BBB", "BB", "B", "CCC"];

export default function Collateral() {
  const [collaterals, setCollaterals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [openDialog, setOpenDialog] = useState(false);
  const [openViewDialog, setOpenViewDialog] = useState(false);
  const [editingCollateral, setEditingCollateral] = useState(null);
  const [viewingCollateral, setViewingCollateral] = useState(null);
  const [snackbar, setSnackbar] = useState({ open: false, message: "", severity: "success" });
  
  const [collateralForm, setCollateralForm] = useState({
    description: "",
    collateralType: "BONDS",
    originalValue: "",
    marketValue: "",
    currency: "USD",
    rating: "A",
    maturityDate: "",
    collateralStatus: "ELIGIBLE"
  });

  const loadCollaterals = async () => {
    try {
      setLoading(true);
      const data = await collateralService.getAllCollaterals();
      setCollaterals(data);
    } catch (error) {
      showSnackbar("Failed to load collaterals", "error");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCollaterals();
  }, []);

  const showSnackbar = (message, severity = "success") => {
    setSnackbar({ open: true, message, severity });
  };

  const handleCloseSnackbar = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const handleOpenDialog = (collateral = null) => {
    if (collateral) {
      setEditingCollateral(collateral);
      setCollateralForm({
        description: collateral.description,
        collateralType: collateral.collateralType,
        originalValue: collateral.originalValue.toString(),
        marketValue: collateral.marketValue.toString(),
        currency: collateral.currency,
        rating: collateral.rating,
        maturityDate: collateral.maturityDate ? collateral.maturityDate.split('T')[0] : "",
        collateralStatus: collateral.collateralStatus
      });
    } else {
      setEditingCollateral(null);
      setCollateralForm({
        description: "",
        collateralType: "BONDS",
        originalValue: "",
        marketValue: "",
        currency: "USD",
        rating: "A",
        maturityDate: "",
        collateralStatus: "ELIGIBLE"
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setEditingCollateral(null);
  };

  const handleViewCollateral = (collateral) => {
    setViewingCollateral(collateral);
    setOpenViewDialog(true);
  };

  const handleSubmit = async () => {
    try {
      const formData = {
        ...collateralForm,
        originalValue: parseFloat(collateralForm.originalValue),
        marketValue: parseFloat(collateralForm.marketValue),
        maturityDate: collateralForm.maturityDate ? `${collateralForm.maturityDate}T00:00:00` : null
      };

      if (editingCollateral) {
        await collateralService.updateCollateral(editingCollateral.id, formData);
        showSnackbar("Collateral updated successfully");
      } else {
        await collateralService.createCollateral(formData);
        showSnackbar("Collateral created successfully");
      }
      
      handleCloseDialog();
      loadCollaterals();
    } catch (error) {
      showSnackbar(error.response?.data || "Operation failed", "error");
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this collateral?")) {
      try {
        await collateralService.deleteCollateral(id);
        showSnackbar("Collateral deleted successfully");
        loadCollaterals();
      } catch (error) {
        showSnackbar("Failed to delete collateral", "error");
      }
    }
  };

  const formatCurrency = (amount, currency = "USD") => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency
    }).format(amount);
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString();
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "ELIGIBLE": return "success";
      case "PLEDGED": return "info";
      case "MATURED": return "warning";
      case "INELIGIBLE": return "error";
      default: return "default";
    }
  };

  const getRatingColor = (rating) => {
    if (["AAA", "AA", "A"].includes(rating)) return "success";
    if (["BBB", "BB"].includes(rating)) return "warning";
    return "error";
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
        <LoaderBars />
      </Box>
    );
  }

  const eligibleCollaterals = collaterals.filter(c => c.collateralStatus === "ELIGIBLE");
  const totalMarketValue = collaterals.reduce((sum, c) => sum + c.marketValue, 0);

  return (
    <Box>
      {/* Header */}
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 3 }}>
        <Box>
          <Typography variant="h4" gutterBottom>
            Collateral Management
          </Typography>
          <Typography variant="body1" color="textSecondary">
            Manage eligible collaterals and monitor their status
          </Typography>
        </Box>
        
        <Button
          variant="outlined"
          startIcon={<Refresh />}
          onClick={loadCollaterals}
        >
          Refresh
        </Button>
      </Box>

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={3}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
          >
            <Card>
              <CardContent>
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <Security color="primary" sx={{ fontSize: 40 }} />
                  <Box>
                    <Typography variant="h6">Total Collaterals</Typography>
                    <Typography variant="h4">{collaterals.length}</Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
        
        <Grid item xs={12} md={3}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
          >
            <Card>
              <CardContent>
                <Typography variant="h6">Total Market Value</Typography>
                <Typography variant="h4">
                  {formatCurrency(totalMarketValue)}
                </Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
        
        <Grid item xs={12} md={3}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <Card>
              <CardContent>
                <Typography variant="h6">Eligible Collaterals</Typography>
                <Typography variant="h4">{eligibleCollaterals.length}</Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
        
        <Grid item xs={12} md={3}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
          >
            <Card>
              <CardContent>
                <Typography variant="h6">Eligible Value</Typography>
                <Typography variant="h4">
                  {formatCurrency(eligibleCollaterals.reduce((sum, c) => sum + c.marketValue, 0))}
                </Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
      </Grid>

      {/* Collaterals Grid View */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {collaterals.slice(0, 6).map((collateral, index) => (
          <Grid item xs={12} md={6} lg={4} key={collateral.id}>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 * index }}
            >
              <Card sx={{ height: "100%" }}>
                <CardContent>
                  <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "start", mb: 2 }}>
                    <Typography variant="h6" noWrap>
                      {collateral.description}
                    </Typography>
                    <Chip 
                      label={collateral.collateralStatus} 
                      color={getStatusColor(collateral.collateralStatus)}
                      size="small"
                    />
                  </Box>
                  
                  <Typography variant="body2" color="textSecondary" gutterBottom>
                    {collateral.collateralType}
                  </Typography>
                  
                  <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                    <Typography variant="body2">Market Value:</Typography>
                    <Typography variant="body2" fontWeight="bold">
                      {formatCurrency(collateral.marketValue, collateral.currency)}
                    </Typography>
                  </Box>
                  
                  <Box sx={{ display: "flex", justifyContent: "space-between", mb: 1 }}>
                    <Typography variant="body2">Rating:</Typography>
                    <Chip 
                      label={collateral.rating} 
                      color={getRatingColor(collateral.rating)}
                      size="small"
                    />
                  </Box>
                  
                  <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                    <Typography variant="body2">Maturity:</Typography>
                    <Typography variant="body2">
                      {formatDate(collateral.maturityDate)}
                    </Typography>
                  </Box>
                </CardContent>
                
                <CardActions>
                  <Button 
                    size="small" 
                    startIcon={<Visibility />}
                    onClick={() => handleViewCollateral(collateral)}
                  >
                    View
                  </Button>
                  <Button 
                    size="small" 
                    startIcon={<Edit />}
                    onClick={() => handleOpenDialog(collateral)}
                  >
                    Edit
                  </Button>
                </CardActions>
              </Card>
            </motion.div>
          </Grid>
        ))}
      </Grid>

      {/* Collaterals Table */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.5 }}
      >
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Description</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Market Value</TableCell>
                <TableCell>Rating</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Maturity Date</TableCell>
                <TableCell>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {collaterals.map((collateral) => (
                <TableRow key={collateral.id} hover>
                  <TableCell>{collateral.description}</TableCell>
                  <TableCell>{collateral.collateralType}</TableCell>
                  <TableCell>{formatCurrency(collateral.marketValue, collateral.currency)}</TableCell>
                  <TableCell>
                    <Chip 
                      label={collateral.rating} 
                      color={getRatingColor(collateral.rating)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Chip 
                      label={collateral.collateralStatus} 
                      color={getStatusColor(collateral.collateralStatus)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>{formatDate(collateral.maturityDate)}</TableCell>
                  <TableCell>
                    <IconButton
                      size="small"
                      onClick={() => handleViewCollateral(collateral)}
                      color="info"
                    >
                      <Visibility />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleOpenDialog(collateral)}
                      color="primary"
                    >
                      <Edit />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleDelete(collateral.id)}
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
      <Tooltip title="Add New Collateral">
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

      {/* Create/Edit Dialog */}
      <Dialog open={openDialog} onClose={handleCloseDialog} maxWidth="md" fullWidth>
        <DialogTitle>
          {editingCollateral ? "Edit Collateral" : "Create New Collateral"}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Description"
                value={collateralForm.description}
                onChange={(e) => setCollateralForm({...collateralForm, description: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                select
                label="Collateral Type"
                value={collateralForm.collateralType}
                onChange={(e) => setCollateralForm({...collateralForm, collateralType: e.target.value})}
              >
                {collateralTypes.map((type) => (
                  <MenuItem key={type} value={type}>
                    {type}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                select
                label="Rating"
                value={collateralForm.rating}
                onChange={(e) => setCollateralForm({...collateralForm, rating: e.target.value})}
              >
                {ratings.map((rating) => (
                  <MenuItem key={rating} value={rating}>
                    {rating}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="number"
                label="Original Value"
                value={collateralForm.originalValue}
                onChange={(e) => setCollateralForm({...collateralForm, originalValue: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="number"
                label="Market Value"
                value={collateralForm.marketValue}
                onChange={(e) => setCollateralForm({...collateralForm, marketValue: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Currency"
                value={collateralForm.currency}
                onChange={(e) => setCollateralForm({...collateralForm, currency: e.target.value})}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                select
                label="Status"
                value={collateralForm.collateralStatus}
                onChange={(e) => setCollateralForm({...collateralForm, collateralStatus: e.target.value})}
              >
                {collateralStatuses.map((status) => (
                  <MenuItem key={status} value={status}>
                    {status}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                type="date"
                label="Maturity Date"
                value={collateralForm.maturityDate}
                onChange={(e) => setCollateralForm({...collateralForm, maturityDate: e.target.value})}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <AnimatedButton
            onClick={handleSubmit}
            variant="contained"
            disabled={!collateralForm.description || !collateralForm.originalValue || !collateralForm.marketValue}
          >
            {editingCollateral ? "Update" : "Create"}
          </AnimatedButton>
        </DialogActions>
      </Dialog>

      {/* View Dialog */}
      <Dialog open={openViewDialog} onClose={() => setOpenViewDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Collateral Details</DialogTitle>
        <DialogContent>
          {viewingCollateral && (
            <Box sx={{ mt: 2 }}>
              <Typography variant="h6" gutterBottom>{viewingCollateral.description}</Typography>
              
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">Type:</Typography>
                  <Typography variant="body1">{viewingCollateral.collateralType}</Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">Rating:</Typography>
                  <Chip 
                    label={viewingCollateral.rating} 
                    color={getRatingColor(viewingCollateral.rating)}
                    size="small"
                  />
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">Original Value:</Typography>
                  <Typography variant="body1">
                    {formatCurrency(viewingCollateral.originalValue, viewingCollateral.currency)}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">Market Value:</Typography>
                  <Typography variant="body1">
                    {formatCurrency(viewingCollateral.marketValue, viewingCollateral.currency)}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">Status:</Typography>
                  <Chip 
                    label={viewingCollateral.collateralStatus} 
                    color={getStatusColor(viewingCollateral.collateralStatus)}
                    size="small"
                  />
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">Maturity Date:</Typography>
                  <Typography variant="body1">{formatDate(viewingCollateral.maturityDate)}</Typography>
                </Grid>
              </Grid>
            </Box>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenViewDialog(false)}>Close</Button>
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