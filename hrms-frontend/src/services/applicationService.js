import axiosClient from "./axiosClient";

const applicationService = {
  createApplication: (formData) => {
    return axiosClient.post("/applications", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
};

export default applicationService;
