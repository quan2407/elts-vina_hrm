import axios from "axios";

/**
 * Trên Vercel bạn sẽ đặt:
 * - Preview/Develop: VITE_API_ORIGIN = https://your-api-develop.onrender.com
 * - Production:      VITE_API_ORIGIN = https://your-api-prod.onrender.com
 *
 * Dev local: file frontend/.env.local
 *   VITE_API_ORIGIN=http://localhost:8080
 */
const baseURL = `${import.meta.env.VITE_API_ORIGIN}/api`;

export const api = axios.create({
    baseURL,
    withCredentials: true,
});

export const getHealth = () => api.get("/health");
