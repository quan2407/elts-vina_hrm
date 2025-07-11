import axiosClient from "./axiosClient";

const API_URL = "/lines";

export const getAllLines = async (search) => {
  const response = await axiosClient.get(API_URL, { params: { search } });
  return response.data;
}

export const getLineById = async (id) => {
  try {
    const response = await axiosClient.get(`${API_URL}/${id}`);
    return response.data;
  }
  catch (error) {
    console.error("Error fetching line:", error);
    throw error;
  }

}

export const updateLineLeader = async (lineId, leaderId) => {
  try {
    const response = await axiosClient.put(`${API_URL}/${lineId}/leader`, { leaderId });
    return response.data;
  } catch (error) {
    console.error("Error updating line leader:", error);
    throw error;
  }
}