import React from "react";
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Menu,
  MenuItem,
  Avatar,
  Box,
  Chip
} from "@mui/material";
import {
  Menu as MenuIcon,
  AccountCircle,
  Logout
} from "@mui/icons-material";
import logo from "../../assets/logo.png";
import { useAuth } from "../../contexts/AuthContext";
import ThemeToggle from "../common/ThemeToggle";

export default function Header({ onMenuClick }) {
  const { user, logout } = useAuth();
  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    handleClose();
  };

  return (
    <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1 }}>
      <Toolbar>
        <IconButton
          color="inherit"
          aria-label="open drawer"
          edge="start"
          onClick={onMenuClick}
          sx={{ mr: 2 }}
        >
          <MenuIcon />
        </IconButton>
        
        <Box
          component="img"
          src={logo}
          alt="KPS Treasury Logo"
          sx={{
            width: 32,
            height: 32,
            mr: 2,
            objectFit: "contain"
          }}
        />
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          KPS Treasury Management
        </Typography>

        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <Chip 
            label={user?.role} 
            size="small" 
            color="secondary" 
            variant="outlined" 
          />
          
          <ThemeToggle />
          
          <IconButton
            size="large"
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleMenu}
            color="inherit"
          >
            <Avatar sx={{ width: 32, height: 32 }}>
              {user?.username?.charAt(0).toUpperCase()}
            </Avatar>
          </IconButton>
          
          <Menu
            id="menu-appbar"
            anchorEl={anchorEl}
            anchorOrigin={{
              vertical: "bottom",
              horizontal: "right",
            }}
            keepMounted
            transformOrigin={{
              vertical: "top",
              horizontal: "right",
            }}
            open={Boolean(anchorEl)}
            onClose={handleClose}
          >
            <MenuItem onClick={handleClose}>
              <AccountCircle sx={{ mr: 1 }} />
              {user?.username}
            </MenuItem>
            <MenuItem onClick={handleLogout}>
              <Logout sx={{ mr: 1 }} />
              Logout
            </MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  );
}