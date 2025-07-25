import axiosClient from "./axiosClient";

const accountService = {
  getAllAccounts: () => {
    return axiosClient.get("/accounts");
  },

  toggleAccountStatus: (id) => {
    return axiosClient.put(`/accounts/${id}/toggle-status`);
  },
};

export default accountService;
