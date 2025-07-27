import axiosClient from "./axiosClient";

const permissionService = {
  getGroupedPermissions: () => axiosClient.get("/permissions/grouped"),
};

export default permissionService;
