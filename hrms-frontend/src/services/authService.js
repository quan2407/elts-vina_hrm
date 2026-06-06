import axiosClient from "./axiosClient";

const authService = {
  login: async (credentials) => {
    const res = await axiosClient.post("/auth/login", credentials);
    const token = res.data.accessToken;

    if (token) {
      localStorage.setItem("accessToken", token);

      const payload = JSON.parse(atob(token.split(".")[1]));
      console.log("TOKEN PAYLOAD", payload);

      const roles = payload.roles || [];
      const userId = payload.sub;

      localStorage.setItem("role", JSON.stringify(roles));
      localStorage.setItem("userId", userId); // 👈 thêm dòng này
    }

    return res;
  },

  logout: () => localStorage.removeItem("accessToken"),
  resetPassword: (data) =>
    axiosClient.post("/auth/request-reset-password", data),
  changePassword: (data) => axiosClient.put("/auth/change-password", data),

  getResetRequests: (page = 0, size = 10, filters = {}) => {
    const {
      status = "all", // all | pending | approved | rejected
      search, // tìm theo mã NV / tên NV
      departmentName,
      positionName,
      lineName,
    } = filters;

    return axiosClient.get("/auth/admin/pending-reset-requests", {
      params: {
        status,
        page,
        size,
        ...(search ? { search } : {}),
        ...(departmentName ? { departmentName } : {}),
        ...(positionName ? { positionName } : {}),
        ...(lineName ? { lineName } : {}),
      },
    });
  },
  approveResetPassword: (data) =>
    axiosClient.post("/auth/admin/approve-reset-password", data),
};

export default authService;
