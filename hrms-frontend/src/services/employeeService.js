import axiosClient from "./axiosClient";

const employeeService = {
  getAllEmployees: () => {
    return axiosClient.get("/employees");
  },
  getEmployeesByDepartmentId: (id) => {
    return axiosClient.get(`/employees/department/${id}`);
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
  getOwnProfile: () => axiosClient.get("/employees/profile"),
  updateOwnProfile: (data) => axiosClient.put("/employees/profile", data),
};

export default employeeService;
