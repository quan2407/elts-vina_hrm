import axiosClient from "./axiosClient";

const API_URL = "/interview";

export const getAllInterviews = async () => {
  const response = await axiosClient.get(API_URL);
  return response.data;
};

export const getInterviewById = async (id) => {
  try {
    const response = await axiosClient.get(`${API_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching interview by ID:", error);
    throw error;
  }
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
};

export const editInterview = async (data, id) => {
  try {
    const response = await axiosClient.put(`${API_URL}/${id}`, data);
    return response.data;
  } catch (error) {
    console.error("Error editing interview:", error);
    throw error;
  }
};

export const updateInterviewStatus = async (id, status) => {
  try {
    const response = await axiosClient.put(`/interview/${id}/status`, { status });
    return response.data;
  } catch (error) {
    console.error("Lỗi cập nhật trạng thái phỏng vấn:", error);
    throw error;
  }
};

export const updateInterviewResult = async (interviewId, result) => {
  try {
    const response = await axiosClient.put(`/interview/${interviewId}/result`, { result });
    return response.data;
  } catch (error) {
    console.error("Lỗi cập nhật kết quả phỏng vấn:", error);
    throw error;
  }
};