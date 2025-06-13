import axiosClient from "./axiosClient";

const authService = {
  login: (credentials) => axiosClient.post("/auth/login", credentials),
  logout: () => localStorage.removeItem("accessToken"),
};

export default authService;
