import axiosClient from "./axiosClient";

const employeeService = {
  getAllEmployees: () => {
    return axiosClient.get("/employees");
  },
};

export default employeeService;
