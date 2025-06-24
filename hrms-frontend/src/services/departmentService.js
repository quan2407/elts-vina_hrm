import axiosClient from "./axiosClient";

const departmentService = {
  getAllDepartments: () => {
    return axiosClient.get("/departments");
  },
  getPositionsByDepartment: (departmentId) => {
    return axiosClient.get(`/departments/${departmentId}/positions`);
  },
  getLinesByDepartment: (departmentId) => {
    return axiosClient.get(`/departments/${departmentId}/lines`);
  },
};

export default departmentService;
