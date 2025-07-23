import React, { useState, useEffect } from "react";
import {
  Box,
  Card,
  CardContent,
  TextField,
  Typography,
  Alert,
  Container,
  Paper,
  InputAdornment,
  IconButton,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid
} from "@mui/material";
import {
  AccountCircle,
  Lock,
  Visibility,
  VisibilityOff,
  Email,
  Person,
  Badge
} from "@mui/icons-material";
import logo from "../assets/logo.png";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import AnimatedButton from "../components/common/AnimatedButton";
import LoaderBars from "../components/common/LoaderBars";

export default function Register() {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    confirmPassword: "",
    email: "",
    firstName: "",
    lastName: "",
    role: "USER"
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState({});
  
  const { register, error, clearError, registerSuccess, clearRegisterSuccess } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (registerSuccess) {
      const timer = setTimeout(() => {
        clearRegisterSuccess();
        navigate("/login");
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [registerSuccess, clearRegisterSuccess, navigate]);

  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => {
        clearError();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [error, clearError]);

  const validateForm = () => {
    const newErrors = {};

    // Username validation
    if (!formData.username) {
      newErrors.username = "Username is required";
    } else if (formData.username.length < 3) {
      newErrors.username = "Username must have at least 3 characters";
    } else if (formData.username.length > 50) {
      newErrors.username = "Username cannot have more than 50 characters";
    }

    // Password validation
    if (!formData.password) {
      newErrors.password = "Password is required";
    } else if (formData.password.length < 8) {
      newErrors.password = "Password must have at least 8 characters";
    } else if (!/(?=.*[A-Z])(?=.*[!@#$%^&*])/.test(formData.password)) {
      newErrors.password = "Password must contain at least 1 uppercase letter and 1 symbol";
    }

    // Confirm password validation
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = "Password confirmation is required";
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    // Email validation
    if (!formData.email) {
      newErrors.email = "Email is required";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Email must be valid";
    }

    // First name validation
    if (!formData.firstName) {
      newErrors.firstName = "First name is required";
    }

    // Last name validation
    if (!formData.lastName) {
      newErrors.lastName = "Last name is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear field error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ""
      }));
    }
    
    if (error) clearError();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);
    
    try {
      const { confirmPassword, ...userData } = formData;
      await register(userData);
    } catch (err) {
      console.error("Registration failed:", err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const toggleConfirmPasswordVisibility = () => {
    setShowConfirmPassword(!showConfirmPassword);
  };

  const handleBackToLogin = () => {
    navigate("/login");
  };

  return (
    <Container component="main" maxWidth="sm">
      <Box
        sx={{
          minHeight: "100vh",
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
          py: 4
        }}
      >
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <Paper
            elevation={8}
            sx={{
              p: 4,
              borderRadius: 3,
              width: "100%",
              maxWidth: 500
            }}
          >
            <Box
              sx={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                mb: 3
              }}
            >
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ delay: 0.2, duration: 0.5 }}
              >
                <Box
                  component="img"
                  src={logo}
                  alt="KPS Treasury Logo"
                  sx={{
                    width: 80,
                    height: 80,
                    mb: 2,
                    objectFit: "contain"
                  }}
                />
              </motion.div>
              
              <Typography component="h1" variant="h4" gutterBottom>
                Create Account
              </Typography>
              
              <Typography variant="body2" color="text.secondary" textAlign="center">
                Register in KPS Treasury & Collateral Management System
              </Typography>
            </Box>

            {error && (
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.3 }}
              >
                <Alert severity="error" sx={{ mb: 3 }}>
                  {typeof error === "string" ? error : "Registration failed. Please try again."}
                </Alert>
              </motion.div>
            )}

            {registerSuccess && (
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.3 }}
              >
                <Alert severity="success" sx={{ mb: 3 }}>
                  User registered successfully! Redirecting to login...
                </Alert>
              </motion.div>
            )}

            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    margin="normal"
                    required
                    fullWidth
                    id="firstName"
                    label="First Name"
                    name="firstName"
                    autoComplete="given-name"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    disabled={isSubmitting}
                    error={!!errors.firstName}
                    helperText={errors.firstName}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Person color="action" />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    margin="normal"
                    required
                    fullWidth
                    id="lastName"
                    label="Last Name"
                    name="lastName"
                    autoComplete="family-name"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    disabled={isSubmitting}
                    error={!!errors.lastName}
                    helperText={errors.lastName}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <Badge color="action" />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>
              </Grid>

              <TextField
                margin="normal"
                required
                fullWidth
                id="username"
                label="Username"
                name="username"
                autoComplete="username"
                value={formData.username}
                onChange={handleInputChange}
                disabled={isSubmitting}
                error={!!errors.username}
                helperText={errors.username}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <AccountCircle color="action" />
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                margin="normal"
                required
                fullWidth
                id="email"
                label="Email"
                name="email"
                type="email"
                autoComplete="email"
                value={formData.email}
                onChange={handleInputChange}
                disabled={isSubmitting}
                error={!!errors.email}
                helperText={errors.email}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Email color="action" />
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type={showPassword ? "text" : "password"}
                id="password"
                autoComplete="new-password"
                value={formData.password}
                onChange={handleInputChange}
                disabled={isSubmitting}
                error={!!errors.password}
                helperText={errors.password}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock color="action" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={togglePasswordVisibility}
                        edge="end"
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <TextField
                margin="normal"
                required
                fullWidth
                name="confirmPassword"
                label="Confirm Password"
                type={showConfirmPassword ? "text" : "password"}
                id="confirmPassword"
                autoComplete="new-password"
                value={formData.confirmPassword}
                onChange={handleInputChange}
                disabled={isSubmitting}
                error={!!errors.confirmPassword}
                helperText={errors.confirmPassword}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock color="action" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle confirm password visibility"
                        onClick={toggleConfirmPasswordVisibility}
                        edge="end"
                      >
                        {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                }}
              />

              <FormControl fullWidth margin="normal" disabled={isSubmitting}>
                <InputLabel id="role-label">Role</InputLabel>
                <Select
                  labelId="role-label"
                  id="role"
                  name="role"
                  value={formData.role}
                  label="Role"
                  onChange={handleInputChange}
                >
                  <MenuItem value="USER">User</MenuItem>
                  <MenuItem value="TREASURY">Treasury</MenuItem>
                  <MenuItem value="COLLATERAL">Collateral</MenuItem>
                </Select>
              </FormControl>

              <Box sx={{ mt: 3, mb: 2 }}>
                {isSubmitting ? (
                  <Box sx={{ display: "flex", justifyContent: "center", py: 2 }}>
                    <LoaderBars />
                  </Box>
                ) : (
                  <AnimatedButton
                    type="submit"
                    fullWidth
                    variant="contained"
                    size="large"
                    disabled={!formData.username || !formData.password || !formData.email || !formData.firstName || !formData.lastName}
                    sx={{ py: 1.5, mb: 2 }}
                  >
                    Create Account
                  </AnimatedButton>
                )}
              </Box>

              <Box sx={{ textAlign: "center" }}>
                <AnimatedButton
                  fullWidth
                  variant="text"
                  onClick={handleBackToLogin}
                  disabled={isSubmitting}
                >
                  Already have an account? Sign in
                </AnimatedButton>
              </Box>
            </Box>
          </Paper>
        </motion.div>
      </Box>
    </Container>
  );
} 