import axiosClient from "./axiosClient";

const employeeService = {
  getAllEmployees: () => {
    return axiosClient.get("/employees");
  },

  createEmployee: (payload) => {
    return axiosClient.post("/employees", payload);
  },

  getEmployeeById: (id) => {
    return axiosClient.get(`/employees/${id}`);
  },

  updateEmployee: (id, payload) => {
    return axiosClient.put(`/employees/${id}`, payload);
  },
};

export default employeeService;
