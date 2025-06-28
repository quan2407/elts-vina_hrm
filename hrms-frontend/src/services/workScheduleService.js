import axiosClient from "./axiosClient";

const workScheduleService = {
  getWorkScheduleByMonth: (month, year) => {
    return axiosClient.get("/work-schedule-details/view-by-month", {
      params: { month, year },
    });
  },
  createWorkSchedulesForMonth(month, year) {
    return axiosClient.post("/work-schedules", {
      month,
      year,
    });
  },
};

export default workScheduleService;
