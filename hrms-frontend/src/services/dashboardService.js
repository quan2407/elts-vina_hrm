import axiosClient from "./axiosClient";

const API_URL = "/dashboard";

export const getRecruitmentGraphChart = async (selectedFromDate, ToDate) => {
  try {
    const response = await axiosClient.get(`${API_URL}/recruitment-graph`, {
      params: {
        fromDate: selectedFromDate,
        toDate: ToDate,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching recruitment graph chart:", error);
    throw error;
  }
};