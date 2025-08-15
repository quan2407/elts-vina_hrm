import axiosClient from "./axiosClient";

const positionService = {
  getAll: () => axiosClient.get("/positions"),
};

export default positionService;
