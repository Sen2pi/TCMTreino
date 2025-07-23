import React, { createContext, useContext, useReducer, useEffect } from "react";
import { authService } from "../services/auth";

const AuthContext = createContext();

const authReducer = (state, action) => {
  switch (action.type) {
    case "SET_LOADING":
      return { ...state, loading: action.payload };
    case "LOGIN_SUCCESS":
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        isAuthenticated: true,
        loading: false,
        error: null
      };
    case "LOGIN_ERROR":
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        loading: false,
        error: action.payload
      };
    case "REGISTER_SUCCESS":
      return {
        ...state,
        loading: false,
        error: null,
        registerSuccess: action.payload
      };
    case "REGISTER_ERROR":
      return {
        ...state,
        loading: false,
        error: action.payload
      };
    case "LOGOUT":
      return {
        ...state,
        user: null,
        token: null,
        isAuthenticated: false,
        loading: false,
        error: null,
        registerSuccess: null
      };
    case "CLEAR_ERROR":
      return { ...state, error: null };
    case "CLEAR_REGISTER_SUCCESS":
      return { ...state, registerSuccess: null };
    default:
      return state;
  }
};

const initialToken = localStorage.getItem("token");
const initialUser = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
const initialState = {
  user: initialUser,
  token: initialToken,
  isAuthenticated: !!initialToken,
  loading: true,
  error: null,
  registerSuccess: null
};

export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem("token");
      const user = localStorage.getItem("user");
      
      if (token && user) {
        try {
          // Parse stored user data
          const userData = JSON.parse(user);
          dispatch({
            type: "LOGIN_SUCCESS",
            payload: { user: userData, token }
          });
        } catch (error) {
          // Only clear if user data is corrupted
          localStorage.removeItem("token");
          localStorage.removeItem("user");
          dispatch({ type: "LOGOUT" });
        }
      } else {
        dispatch({ type: "SET_LOADING", payload: false });
      }
    };

    checkAuth();
  }, []);

  const login = async (credentials) => {
    dispatch({ type: "SET_LOADING", payload: true });
    try {
      const response = await authService.login(credentials);
      const { token, ...user } = response;
      localStorage.setItem("token", token);
      localStorage.setItem("user", JSON.stringify(user));
      // Adicionando log para debug
      console.log("[AuthContext] Token salvo no localStorage:", token);
      console.log("[AuthContext] Usuário salvo:", user);
      dispatch({ type: "LOGIN_SUCCESS", payload: { user, token } });
      return { success: true };
    } catch (error) {
      dispatch({ type: "LOGIN_ERROR", payload: error?.response?.data || "Erro ao fazer login" });
      return { success: false, error: error?.response?.data || "Erro ao fazer login" };
    }
  };

  const register = async (userData) => {
    try {
      dispatch({ type: "SET_LOADING", payload: true });
      const response = await authService.register(userData);
      
      dispatch({
        type: "REGISTER_SUCCESS",
        payload: response
      });

      return response;
    } catch (error) {
      dispatch({
        type: "REGISTER_ERROR",
        payload: error.response?.data || "Registration failed"
      });
      throw error;
    }
  };

  const logout = () => {
    authService.logout();
    dispatch({ type: "LOGOUT" });
  };

  const clearError = () => {
    dispatch({ type: "CLEAR_ERROR" });
  };

  const clearRegisterSuccess = () => {
    dispatch({ type: "CLEAR_REGISTER_SUCCESS" });
  };

  const validateToken = async () => {
    try {
      const user = await authService.getCurrentUser();
      dispatch({
        type: "LOGIN_SUCCESS",
        payload: { user, token: state.token }
      });
      return true;
    } catch (error) {
      dispatch({ type: "LOGOUT" });
      return false;
    }
  };

  const value = {
    ...state,
    login,
    register,
    logout,
    clearError,
    clearRegisterSuccess,
    validateToken
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};