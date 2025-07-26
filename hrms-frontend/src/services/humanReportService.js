import axiosClient from "./axiosClient";

const API_URL = "/human-report";

export const getFullEmp = async (selectedDate) => {
  try {
    const response = await axiosClient.get(`${API_URL}/full-emp`, { params: { date: selectedDate } });
    return response.data;
  } catch (error) {
    console.error("Error fetching human report:", error);
    throw error;
  }
}

export const getAbsentEmp = async (selectedDate) => {
  try {
    const response = await axiosClient.get(`${API_URL}/absent`, { params: { date: selectedDate } });
    return response.data;
  } catch (error) {
    console.error("Error fetching human report:", error);
    throw error;
  }
}

export const getAbsentEmpKL = async (selectedDate) => {
  try {
    const response = await axiosClient.get(`${API_URL}/absentkl`, { params: { date: selectedDate } });
    return response.data;
  } catch (error) {
    console.error("Error fetching absent employees by line:", error);
    throw error;
  }
}

export const exportFile = async (selectedDate) => {
  return axiosClient.get(`${API_URL}/export`, {
    responseType: "blob",
    params: { date: selectedDate } 
  });
}