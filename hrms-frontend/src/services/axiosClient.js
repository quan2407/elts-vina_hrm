import axios from "axios";

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");

  // Chỉ gắn token nếu gọi API nội bộ (VITE_API_URL hoặc localhost:8080)
  const isInternalAPI =
    config.baseURL?.includes("localhost:8080") ||
    (import.meta.env.VITE_API_URL && config.baseURL?.includes(import.meta.env.VITE_API_URL.replace(/^https?:\/\//, "")));

  if (token && isInternalAPI && !config.url.includes("/auth/login")) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default axiosClient;
