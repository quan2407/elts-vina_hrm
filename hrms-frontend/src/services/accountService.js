import axiosClient from "./axiosClient";

const accountService = {
  getAllAccounts: () => {
    return axiosClient.get("/accounts");
  },

  toggleAccountStatus: (id) => {
    return axiosClient.put(`/accounts/${id}/toggle-status`);
  },
  getAccounts: (page = 0, size = 10) => {
    return axiosClient.get("/accounts", {
      params: { page, size },
    });
  },
  getCurrentUser: () => {
    return axiosClient.get("/me");
  }
};

export default accountService;
