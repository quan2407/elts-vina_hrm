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
  resetPassword: (data) => axiosClient.post("/auth/reset-password", data),
  changePassword: (data) => axiosClient.put("/auth/change-password", data),
};

export default authService;
