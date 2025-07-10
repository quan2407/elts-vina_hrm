import axiosClient from "./axiosClient";

const API_URL = "/lines";

export const getAllLines = async (search) => {
  const response = await axiosClient.get(API_URL,{params: { search }});
  return response.data;
}