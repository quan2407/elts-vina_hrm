import axiosClient from "./axiosClient";
import axios from "axios";

const API_URL = "/recruitment";

export const getAllRecruitments = async () => {
  const response = await axiosClient.get(API_URL);
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

export const getAllCity = async () => {
  const response = await axios.get("https://esgoo.net/api-tinhthanh/1/0.htm");
  return response.data;
};

export const CreateRecruitment = async (payload) => {
  const response = await axiosClient.post(API_URL, payload);
  return response.data;
};

export const EditRecruitment = async (payload, id) => {
  const response = await axiosClient.put(`${API_URL}/${id}`, payload);
  return response.data;
};
