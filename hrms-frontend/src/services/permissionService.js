import axiosClient from "./axiosClient";

const permissionService = {
  getGroupedPermissions: () => axiosClient.get("/permissions/grouped"),
  getMyPermissions: () => axiosClient.get("/me/permissions"),
};

export default permissionService;
