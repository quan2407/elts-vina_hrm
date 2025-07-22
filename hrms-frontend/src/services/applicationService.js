import axiosClient from "./axiosClient";

const applicationService = {
  createApplication: (formData) => {
    return axiosClient.post("/applications", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
  getMyApplications: (page = 0, size = 10, status = null) => {
    const url = status
      ? `/applications/me?page=${page}&size=${size}&status=${status}`
      : `/applications/me?page=${page}&size=${size}`;
    return axiosClient.get(url);
  },
  getApplicationDetail: (id) => {
    return axiosClient.get(`/applications/${id}`);
  },
  cancelApplication: (id) => {
    return axiosClient.post(`/applications/${id}/cancel`);
  },
  updateApplication: (id, formData) => {
    return axiosClient.put(`/applications/${id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
};

export default applicationService;
