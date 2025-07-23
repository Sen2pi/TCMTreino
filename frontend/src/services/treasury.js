import api from "./api";

export const treasuryService = {
  getAllAccounts: async () => {
    const response = await api.get("/treasury");
    return response.data;
  },

  getAccountById: async (id) => {
    const response = await api.get(`/treasury/${id}`);
    return response.data;
  },

  createAccount: async (accountData) => {
    const response = await api.post("/treasury", accountData);
    return response.data;
  },

  updateAccount: async (id, accountData) => {
    const response = await api.put(`/treasury/${id}`, accountData);
    return response.data;
  },

  deleteAccount: async (id) => {
    await api.delete(`/treasury/${id}`);
  },

  transferFunds: async (transferData) => {
    const response = await api.post("/treasury/transfer", transferData);
    return response.data;
  },

  getTotalBalance: async () => {
    const response = await api.get("/treasury/total-balance");
    return response.data;
  },

  getAccountsSummary: async () => {
    const response = await api.get("/treasury/summary");
    return response.data;
  },

  getAccountsByType: async (accountType) => {
    const response = await api.get(`/treasury/type/${accountType}`);
    return response.data;
  }
};