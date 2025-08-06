import axiosClient from "./axiosClient";
const API_URL = "/notification";

export const getNotifications = async () => {
  try {
    const response = await axiosClient.get(API_URL);
    return response.data;
  } catch (error) {
    console.error("Error fetching notifications:", error);
    throw error;
  }
}

// PATCH /notifications/{id}/read
export const markNotificationAsRead = async (id) => {
  try {
    const response = await axiosClient.patch(`${API_URL}/${id}/read`);
    return response.data;
  } catch (error) {
    console.error("Error marking notification as read:", error);
    throw error;
  }
};
