import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import LoaderBars from "./LoaderBars";
import { Box } from "@mui/material";

export default function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) {
    return (
      <Box 
        display="flex" 
        justifyContent="center" 
        alignItems="center" 
        minHeight="100vh"
      >
        <LoaderBars />
      </Box>
    );
  }
  
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}