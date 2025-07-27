import axiosClient from "./axiosClient";

const roleService = {
  getAllRoles: () => axiosClient.get("/roles"),
  getPermissionsByRole: (roleId) =>
    axiosClient.get(`/roles/${roleId}/permissions`),
};

export default roleService;
