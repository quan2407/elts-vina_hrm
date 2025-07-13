import axiosClient from "./axiosClient";

const holidayService = {
  getAllHolidays: () => {
    return axiosClient.get("/holidays");
  },

  createHoliday: (holidayPayload) => {
    return axiosClient.post("/holidays", holidayPayload);
  },

  checkIfHoliday: (date) => {
    return axiosClient.get(`/holidays/check/${date}`);
  },
};

export default holidayService;
