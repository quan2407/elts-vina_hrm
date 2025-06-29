import axiosClient from "./axiosClient";

const API_URL = "/interview";

export const getAllInterviews = async () => {
  const response = await axiosClient.get(API_URL);
  return response.data;
};

export const getInterviewByCandidateRecruitmentId = async (id) => {
  try {
    const response = await axiosClient.get(`${API_URL}/candidate-recruitment/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching interview:", error);
    throw error;
  }
};

export const createInterview = async (data) => {
  try {
    const response = await axiosClient.post(API_URL, data);
    return response.data;
  } catch (error) {
    console.error("Error creating interview:", error);
    throw error;
  }
}
