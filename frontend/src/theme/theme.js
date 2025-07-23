import { createTheme } from "@mui/material/styles";

export const getDesignTokens = (mode) => ({
  palette: {
    mode,
    ...(mode === "light"
      ? { 
          primary: { main: "#1976d2" },
          secondary: { main: "#dc004e" },
          background: { default: "#fafafa", paper: "#ffffff" },
          text: { primary: "#1a1a1a", secondary: "#666666" }
        }
      : { 
          primary: { main: "#90caf9" },
          secondary: { main: "#f48fb1" },
          background: { default: "#121212", paper: "#1e1e1e" },
          text: { primary: "#ffffff", secondary: "#b3b3b3" }
        }),
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 500,
    },
    h6: {
      fontWeight: 500,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: { 
        root: { 
          borderRadius: 8, 
          textTransform: "none",
          fontWeight: 500,
        } 
      }
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: mode === "light" 
            ? "0 2px 8px rgba(0,0,0,0.1)" 
            : "0 2px 8px rgba(0,0,0,0.3)"
        }
      }
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          boxShadow: "none",
          borderBottom: mode === "light" 
            ? "1px solid rgba(0, 0, 0, 0.12)" 
            : "1px solid rgba(255, 255, 255, 0.12)"
        }
      }
    }
  }
});