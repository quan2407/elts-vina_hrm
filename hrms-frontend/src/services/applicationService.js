import axiosClient from "./axiosClient";

const applicationService = {
  createApplication: (formData) => {
    return axiosClient.post("/applications", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
  getMyApplications: (page = 0, size = 10) => {
    return axiosClient.get(`/applications/me?page=${page}&size=${size}`);
  },
};

export default applicationService;
