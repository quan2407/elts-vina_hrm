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

export const getAllCity = async () => {
  const response = await axios.get("https://esgoo.net/api-tinhthanh/1/0.htm");
  return response.data;
};

export const CreateRecruitment = async (payload) => {
  const response = await axios.post(
    `${API_URL}`, payload,
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};

export const EditRecruitment = async (payload, id) => {
  const response = await axios.put(
    `${API_URL}/${id}`, payload,
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};