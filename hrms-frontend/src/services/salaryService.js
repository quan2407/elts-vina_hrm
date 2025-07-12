import axiosClient from "./axiosClient";

const salaryService = {
  getMonthlySalaries: (month, year) => {
    return axiosClient.get("/salaries", {
      params: { month, year },
    });
  },
  regenerateMonthlySalaries: (month, year) => {
    return axiosClient.put(`/salaries/regenerate?month=${month}&year=${year}`);
  },
};

export default salaryService;
