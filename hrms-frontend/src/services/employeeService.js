import axiosClient from "./axiosClient";

const employeeService = {
  getEmployeeNotInLine: (lineId, searchTerm = "") => {
    return axiosClient.get(`/employees/not-in-line/${lineId}`, {
      params: { search: searchTerm },
    });
  },

  getEmployeeByLineId: (id) => {
    return axiosClient.get(`/employees/line/${id}`);
  },
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
  deleteEmployee: (id) => {
    return axiosClient.delete(`/employees/${id}`);
  },
  exportFile: () => {
    return axiosClient.get("/employees/export", {
      responseType: "blob",
    });
  },
  addEmployeesToLine: (lineId, employeeIds) => {
    return axiosClient.put(`employees/add-to-line/${lineId}`, employeeIds);
  },
  getNextEmployeeCode: () => axiosClient.get("/employees/next-code"),
  getNextEmployeeCodeByPosition: (positionId) => {
    return axiosClient.get(`/employees/next-code/${positionId}`);
  },
};

export default employeeService;
