import axiosClient from "./axiosClient";

const accountService = {
  getAllAccounts: () => {
    return axiosClient.get("/accounts");
  },
};

export default accountService;
