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
  IconButton
} from "@mui/material";
import {
  AccountCircle,
  Lock,
  Visibility,
  VisibilityOff
} from "@mui/icons-material";
import logo from "../assets/logo.png";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import AnimatedButton from "../components/common/AnimatedButton";
import LoaderBars from "../components/common/LoaderBars";

export default function Login() {
  const [credentials, setCredentials] = useState({
    username: "",
    password: ""
  });
  const [showPassword, setShowPassword] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const { login, error, clearError, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/dashboard");
    }
  }, [isAuthenticated, navigate]);

  useEffect(() => {
    if (error) {
      const timer = setTimeout(() => {
        clearError();
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [error, clearError]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setCredentials(prev => ({
      ...prev,
      [name]: value
    }));
    if (error) clearError();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    try {
      await login(credentials);
      navigate("/dashboard");
    } catch (err) {
      console.error("Login failed:", err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
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
              maxWidth: 400
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
                KPS Treasury
              </Typography>
              
              <Typography variant="body2" color="text.secondary" textAlign="center">
                Treasury & Collateral Management System
              </Typography>
            </Box>

            {error && (
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.3 }}
              >
                <Alert severity="error" sx={{ mb: 3 }}>
                  {typeof error === "string" ? error : "Login failed. Please try again."}
                </Alert>
              </motion.div>
            )}

            <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="username"
                label="Username"
                name="username"
                autoComplete="username"
                autoFocus
                value={credentials.username}
                onChange={handleInputChange}
                disabled={isSubmitting}
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
                name="password"
                label="Password"
                type={showPassword ? "text" : "password"}
                id="password"
                autoComplete="current-password"
                value={credentials.password}
                onChange={handleInputChange}
                disabled={isSubmitting}
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
                    disabled={!credentials.username || !credentials.password}
                    sx={{ py: 1.5 }}
                  >
                    Sign In
                  </AnimatedButton>
                )}
              </Box>
            </Box>
          </Paper>
        </motion.div>
      </Box>
    </Container>
  );
}