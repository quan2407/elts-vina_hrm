import axios from "axios";
import axiosClient from "./axiosClient";

const API_URL = "http://localhost:8080/api/candidate/apply";

export const applyJob = async (recruitmentId, candidateDto) => {
  const response = await axios.post(
    `${API_URL}/${recruitmentId}`,
    candidateDto,
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};

export const getAllCandidateRecrutment = async (recruitmentId) => {
  const response = await axiosClient.get("http://localhost:8080/api/candidate/" + recruitmentId);
  return response.data;
}
