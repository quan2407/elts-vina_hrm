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
  getHolidayById: (id) => {
    return axiosClient.get(`/holidays/${id}`);
  },

  updateHoliday: (id, holidayPayload) => {
    return axiosClient.put(`/holidays/${id}`, holidayPayload);
  },
  deleteHoliday: (id) => {
    return axiosClient.delete(`/holidays/${id}`);
  },
};

export default holidayService;
