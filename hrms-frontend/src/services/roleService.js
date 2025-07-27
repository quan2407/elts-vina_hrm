import axiosClient from "./axiosClient";

const roleService = {
  getAllRoles: () => axiosClient.get("/roles"),
  getPermissionsByRole: (roleId) =>
    axiosClient.get(`/roles/${roleId}/permissions`),
  updatePermissions: (roleId, permissionIds) =>
    axiosClient.put(`/roles/${roleId}/permissions`, {
      permissionIds,
    }),
};

export default roleService;
