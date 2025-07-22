import axiosClient from "./axiosClient";

const applicationApprovalService = {
  getStep1Applications: (page = 0, size = 10, status = null) => {
    const url = status
      ? `/applications/step-1?page=${page}&size=${size}&status=${status}`
      : `/applications/step-1?page=${page}&size=${size}`;
    return axiosClient.get(url);
  },

  approveStep1: (id, data) => {
    return axiosClient.put(`/applications/${id}/approve-step-1`, data);
  },
};

export default applicationApprovalService;
