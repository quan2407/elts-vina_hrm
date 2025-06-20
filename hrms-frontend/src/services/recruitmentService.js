import axios from "axios";

const API_URL = "http://localhost:8080/api/recruitment";

export const getAllRecruitments = async () => {
  const response = await axios.get(API_URL);
  return response.data;
};

export const getRecruitmentById = async (id) => {
  try {
    const response = await axios.get(API_URL + `/` + id);
    return response.data;
  } catch (error) {
    console.error("Error fetching recruitment:", error);
    throw error;
  }
};
