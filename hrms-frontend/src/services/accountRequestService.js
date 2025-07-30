import axiosClient from "./axiosClient";

const accountRequestService = {
  getRequestsByStatus: (status, page = 0, size = 10) => {
    return axiosClient.get(`/account-requests`, {
      params: { status, page, size },
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
