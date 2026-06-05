import axiosClient from "./axiosClient";

const API_URL = "/recruitment";

export const getAllRecruitments = async (
  page,
  size,
  search,
  sortField,
  sortOrder
) => {
  const response = await axiosClient.get(API_URL, {
    params: { page, size, search, sortField, sortOrder },
  });
  return response.data;
};

export const getRecruitmentById = async (id) => {
  try {
    const response = await axiosClient.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching recruitment:", error);
    throw error;
  }
};

export const CreateRecruitment = async (payload) => {
  const response = await axiosClient.post(API_URL, payload);
  return response.data;
};

export const EditRecruitment = async (payload, id) => {
  const response = await axiosClient.put(`${API_URL}/${id}`, payload);
  return response.data;
};
