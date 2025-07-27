import axiosClient from "./axiosClient";

const authService = {
  login: async (credentials) => {
    const res = await axiosClient.post("/auth/login", credentials);
    const token = res.data.accessToken;
    if (token) {
      localStorage.setItem("accessToken", token);
    }
    return res;
  },
  logout: () => localStorage.removeItem("accessToken"),
  resetPassword: (data) =>
    axiosClient.post("/auth/request-reset-password", data),
  changePassword: (data) => axiosClient.put("/auth/change-password", data),

  getPendingResetRequests: () =>
    axiosClient.get("/auth/admin/pending-reset-requests"),

  approveResetPassword: (data) =>
    axiosClient.post("/auth/admin/approve-reset-password", data),
};

export default authService;
