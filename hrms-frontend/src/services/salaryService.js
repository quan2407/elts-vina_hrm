import axiosClient from "./axiosClient";

const salaryService = {
  getMonthlySalaries: (month, year, page = 0, size = 10) =>
    axios.get(`/api/salaries`, {
      params: { month, year, page, size },
    }),

  regenerateMonthlySalaries: (month, year) => {
    return axiosClient.put("/salaries/regenerate", null, {
      params: { month, year },
    });
  },

  getAvailableSalaryMonths: () => {
    return axiosClient.get("/salaries/available-months");
  },
  getEmpMonthlySalaries: (month, year) => {
    return axiosClient.get("/salaries/employee-months", {
      params: { month, year },
    });
  },
  lockSalaryMonth: (month, year, locked) => {
    return axiosClient.put("/salaries/lock", null, {
      params: { month, year, locked },
    });
  },
  exportMonthlySalaries: (month, year) => {
    return axiosClient.get("/salaries/export", {
      params: { month, year },
      responseType: "blob",
    });
  },
};

export default salaryService;
