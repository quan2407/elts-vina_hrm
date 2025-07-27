import axiosClient from "./axiosClient";

const API_URL = "/dashboard";

export const getRecruitmentGraphChart = async () => {
  try {
    const response = await axiosClient.get(`${API_URL}/recruitment-graph`);
    return response.data;
  } catch (error) {
    console.error("Error fetching recruitment graph chart:", error);
    throw error;
  }
};