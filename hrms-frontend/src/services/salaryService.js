import axiosClient from "./axiosClient";

const salaryService = {
  getMonthlySalaries: (month, year) => {
    return axiosClient.get("/salaries", {
      params: { month, year },
    });
  },
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
};

export default salaryService;
