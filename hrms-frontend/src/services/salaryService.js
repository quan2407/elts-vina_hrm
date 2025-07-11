import axiosClient from "./axiosClient";

const salaryService = {
  getMonthlySalaries: (month, year) => {
    return axiosClient.get("/salaries", {
      params: { month, year },
    });
  },
};

export default salaryService;
