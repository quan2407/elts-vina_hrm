import axiosClient from "./axiosClient";

const accountRequestService = {
  getRequests: (page = 0, size = 10, filters = {}) => {
    const {
      status = "all",
      search,
      departmentId,
      positionId,
      lineId,
    } = filters;

    return axiosClient.get(`/account-requests`, {
      params: {
        status,
        page,
        size,
        // chỉ gửi khi có giá trị
        ...(search ? { search } : {}),
        ...(departmentId ? { departmentId } : {}),
        ...(positionId ? { positionId } : {}),
        ...(lineId ? { lineId } : {}),
      },
    });
  },

  approveRequest: (id) => {
    return axiosClient.post(`/account-requests/${id}/approve`);
  },

  rejectRequest: (id) => {
    return axiosClient.post(`/account-requests/${id}/reject`);
  },
};

export default accountRequestService;
