import axiosClient from "./axiosClient";

const API_URL = "/dashboard";

export const getRecruitmentGraphChart = async (selectedFromDate, ToDate) => {
  try {
    const response = await axiosClient.get(`${API_URL}/recruitment-graph`, {
      params: {
        fromDate: selectedFromDate,
        toDate: ToDate,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching recruitment graph chart:", error);
    throw error;
  }
};
export const getEmployeeGenderDistribution = async (
  selectedFromDate,
  ToDate
) => {
  try {
    const response = await axiosClient.get(
      `${API_URL}/employee-gender-distribution`,
      {
        params: {
          fromDate: selectedFromDate,
          toDate: ToDate,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error fetching gender distribution:", error);
    throw error;
  }
};

// API để lấy phân bổ phòng ban
export const getEmployeeDepartmentDistribution = async (
  selectedFromDate,
  ToDate
) => {
  try {
    const response = await axiosClient.get(
      `${API_URL}/employee-department-distribution`,
      {
        params: {
          fromDate: selectedFromDate,
          toDate: ToDate,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error fetching department distribution:", error);
    throw error;
  }
};
