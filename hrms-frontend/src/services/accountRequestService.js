import axiosClient from "./axiosClient";

const accountRequestService = {
  getRequestsByStatus: (status) => {
    return axiosClient.get(`/account-requests?status=${status}`);
  },

  approveRequest: (id) => {
    return axiosClient.post(`/account-requests/${id}/approve`);
  },

  rejectRequest: (id) => {
    return axiosClient.post(`/account-requests/${id}/reject`);
  },
};

export default accountRequestService;
