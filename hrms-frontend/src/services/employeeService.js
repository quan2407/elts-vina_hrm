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
  getAllEmployees: (
    page = 0,
    size = 10,
    search = "",
    departmentId = null,
    positionId = null,
    lineId = null
  ) => {
    const params = { page, size, search };
    if (departmentId) params.departmentId = departmentId;
    if (positionId) params.positionId = positionId;
    if (lineId) params.lineId = lineId;

    return axiosClient.get("/employees", { params });
  },

  getEmployeesByDepartmentId: (id) => {
    return axiosClient.get(`/employees/department/${id}`);
  },

  createEmployee: (formData) => {
    return axiosClient.post("/employees", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  getEmployeeById: (id) => {
    return axiosClient.get(`/employees/${id}`);
  },

  updateEmployee: (id, formData) => {
    return axiosClient.put(`/employees/${id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
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
  getSimpleEmployees: () => {
    return axiosClient.get("/employees/simple");
  },
  getCurrentEmployeeName: () => {
    return axiosClient.get("/username/me/name");
  },
};

export default employeeService;
