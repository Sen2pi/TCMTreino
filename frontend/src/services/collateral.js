import api from "./api";

export const collateralService = {
  getAllCollaterals: async () => {
    const response = await api.get("/collateral");
    return response.data;
  },

  getCollateralById: async (id) => {
    const response = await api.get(`/collateral/${id}`);
    return response.data;
  },

  createCollateral: async (collateralData) => {
    const response = await api.post("/collateral", collateralData);
    return response.data;
  },

  updateCollateral: async (id, collateralData) => {
    const response = await api.put(`/collateral/${id}`, collateralData);
    return response.data;
  },

  deleteCollateral: async (id) => {
    await api.delete(`/collateral/${id}`);
  },

  updateMarketValue: async (id, marketValue) => {
    const response = await api.patch(`/collateral/${id}/market-value`, { marketValue });
    return response.data;
  },

  getEligibleCollaterals: async () => {
    const response = await api.get("/collateral/eligible");
    return response.data;
  },

  getCollateralsSummary: async () => {
    const response = await api.get("/collateral/summary");
    return response.data;
  },

  getCollateralsByType: async (collateralType) => {
    const response = await api.get(`/collateral/type/${collateralType}`);
    return response.data;
  },

  getCollateralsByStatus: async (status) => {
    const response = await api.get(`/collateral/status/${status}`);
    return response.data;
  }
};