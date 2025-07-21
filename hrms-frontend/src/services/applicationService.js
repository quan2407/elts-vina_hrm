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
};

export default applicationService;
