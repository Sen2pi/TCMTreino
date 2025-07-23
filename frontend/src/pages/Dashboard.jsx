import React, { useState, useEffect } from "react";
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Avatar,
  Chip,
  Paper,
  IconButton,
  Tooltip
} from "@mui/material";
import {
  AccountBalance,
  Security,
  TrendingUp,
  Assessment,
  Refresh
} from "@mui/icons-material";
import { motion } from "framer-motion";
import { useAuth } from "../contexts/AuthContext";
import { treasuryService } from "../services/treasury";
import { collateralService } from "../services/collateral";
import { TreasuryBarChart, TreasuryPieChart } from "../components/charts/TreasuryChart";
import LoaderBars from "../components/common/LoaderBars";

const StatCard = ({ title, value, icon, color, subtitle, delay = 0 }) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ delay, duration: 0.5 }}
  >
    <Card sx={{ height: "100%" }}>
      <CardContent>
        <Box sx={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
          <Box>
            <Typography color="textSecondary" gutterBottom variant="overline">
              {title}
            </Typography>
            <Typography variant="h4" component="div" sx={{ fontWeight: 600 }}>
              {value}
            </Typography>
            {subtitle && (
              <Typography variant="body2" color="textSecondary">
                {subtitle}
              </Typography>
            )}
          </Box>
          <Avatar sx={{ backgroundColor: color, width: 56, height: 56 }}>
            {icon}
          </Avatar>
        </Box>
      </CardContent>
    </Card>
  </motion.div>
);

export default function Dashboard() {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState({
    totalBalance: 0,
    accountsCount: 0,
    collateralsCount: 0,
    eligibleCollaterals: 0,
    accountsByType: [],
    collateralsByType: []
  });

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      // Parallel API calls for better performance
      const [
        treasurySummary,
        collateralSummary,
        accountsByType,
        collateralsByType
      ] = await Promise.all([
        treasuryService.getAccountsSummary().catch(() => ({ totalBalance: 0, accountsCount: 0 })),
        collateralService.getCollateralsSummary().catch(() => ({ collateralsCount: 0, eligibleCollaterals: 0 })),
        treasuryService.getAllAccounts().catch(() => []),
        collateralService.getAllCollaterals().catch(() => [])
      ]);

      // Process data for charts
      const accountTypeData = accountsByType.reduce((acc, account) => {
        const type = acc.find(item => item.name === account.accountType);
        if (type) {
          type.value += account.balance;
        } else {
          acc.push({ name: account.accountType, value: account.balance });
        }
        return acc;
      }, []);

      const collateralTypeData = collateralsByType.reduce((acc, collateral) => {
        const type = acc.find(item => item.name === collateral.collateralType);
        if (type) {
          type.value += collateral.marketValue;
        } else {
          acc.push({ name: collateral.collateralType, value: collateral.marketValue });
        }
        return acc;
      }, []);

      setDashboardData({
        totalBalance: treasurySummary.totalBalance || 0,
        accountsCount: treasurySummary.accountsCount || 0,
        collateralsCount: collateralSummary.collateralsCount || 0,
        eligibleCollaterals: collateralSummary.eligibleCollaterals || 0,
        accountsByType: accountTypeData,
        collateralsByType: collateralTypeData
      });
    } catch (error) {
      console.error("Failed to load dashboard data:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
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
            Welcome back, {user?.username}!
          </Typography>
          <Typography variant="body1" color="textSecondary">
            Treasury & Collateral Management Dashboard
          </Typography>
        </Box>
        
        <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
          <Chip 
            label={user?.role} 
            color="primary" 
            variant="outlined"
          />
          <Tooltip title="Refresh Data">
            <IconButton onClick={loadDashboardData}>
              <Refresh />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Stats Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Balance"
            value={formatCurrency(dashboardData.totalBalance)}
            subtitle="Across all accounts"
            icon={<AccountBalance />}
            color="#1976d2"
            delay={0.1}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Treasury Accounts"
            value={dashboardData.accountsCount}
            subtitle="Active accounts"
            icon={<TrendingUp />}
            color="#2e7d32"
            delay={0.2}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Total Collaterals"
            value={dashboardData.collateralsCount}
            subtitle="All collaterals"
            icon={<Security />}
            color="#ed6c02"
            delay={0.3}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <StatCard
            title="Eligible Collaterals"
            value={dashboardData.eligibleCollaterals}
            subtitle="Available for use"
            icon={<Assessment />}
            color="#9c27b0"
            delay={0.4}
          />
        </Grid>
      </Grid>

      {/* Charts */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <motion.div
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.5, duration: 0.5 }}
          >
            <Paper sx={{ p: 3, height: "100%" }}>
              <TreasuryBarChart
                data={dashboardData.accountsByType}
                title="Balance by Account Type"
              />
            </Paper>
          </motion.div>
        </Grid>
        
        <Grid item xs={12} md={6}>
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.6, duration: 0.5 }}
          >
            <Paper sx={{ p: 3, height: "100%" }}>
              <TreasuryPieChart
                data={dashboardData.collateralsByType}
                title="Collateral Value by Type"
              />
            </Paper>
          </motion.div>
        </Grid>
      </Grid>
    </Box>
  );
}