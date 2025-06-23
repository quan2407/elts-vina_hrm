import axiosClient from "./axiosClient";

const authService = {
  login: (credentials) => axiosClient.post("/auth/login", credentials),
  logout: () => localStorage.removeItem("accessToken"),
  resetPassword: (data) => axiosClient.post("/auth/reset-password", data),
};

export default authService;
