import React, { useEffect, useState } from "react";
import dayjs from "dayjs";
import workScheduleService from "../services/workScheduleService";
import MainLayout from "../components/MainLayout";
import "../styles/WorkCalendarView.css";

const EmployeeWorkScheduleView = () => {
  const today = dayjs();
  const [month, setMonth] = useState(today.month() + 1);
  const [year, setYear] = useState(today.year());
  const [workSchedule, setWorkSchedule] = useState([]);

  useEffect(() => {
    const fetchSchedule = async () => {
      try {
        const res = await workScheduleService.getWorkScheduleForCurrentEmployee(
          month,
          year
        );
        setWorkSchedule(res.data || []);
      } catch (err) {
        console.error("Lỗi khi tải lịch làm việc:", err);
      }
    };
    fetchSchedule();
  }, [month, year]);

  const daysInMonth = dayjs(`${year}-${month}-01`).daysInMonth();
  const startDay = dayjs(`${year}-${month}-01`).day();
  const blanks = Array(startDay === 0 ? 6 : startDay - 1).fill(null);

  const scheduleMap = workSchedule.reduce((acc, cur) => {
    acc[cur.date] = cur;
    return acc;
  }, {});

  const calendarDays = [
    ...blanks,
    ...Array(daysInMonth)
      .fill(0)
      .map((_, i) => i + 1),
  ];

  return (
    <MainLayout>
      <div className="workcal-container">
        <h1 className="workcal-title">Lịch làm việc</h1>
        <div className="workcal-controls">
          <select
            value={month}
            onChange={(e) => setMonth(Number(e.target.value))}
            style={{ fontSize: "16px" }}
          >
            {[...Array(12)].map((_, i) => (
              <option
                key={i + 1}
                value={i + 1}
              >
                Tháng {i + 1}
              </option>
            ))}
          </select>
          <select
            value={year}
            style={{ fontSize: "16px" }}
            onChange={(e) => setYear(Number(e.target.value))}
          >
            {[2024, 2025, 2026].map((y) => (
              <option
                key={y}
                value={y}
              >
                Năm {y}
              </option>
            ))}
          </select>
        </div>

        <div className="workcal-grid">
          {["T2", "T3", "T4", "T5", "T6", "T7", "CN"].map((d) => (
            <div
              key={d}
              className="workcal-header"
            >
              {d}
            </div>
          ))}

          {calendarDays.map((day, idx) => {
            const dateStr = day
              ? dayjs(`${year}-${month}-${day}`).format("YYYY-MM-DD")
              : null;
            const schedule = scheduleMap[dateStr];
            const isToday = dateStr === dayjs().format("YYYY-MM-DD");
            const isOvertime = schedule?.overtime;

            return (
              <div
                key={idx}
                className={`workcal-cell ${schedule ? "has-shift" : "empty"} ${
                  isToday ? "is-today" : ""
                } ${isOvertime ? "is-overtime" : ""}`}
              >
                {day && (
                  <>
                    <div className="workcal-date">{day}</div>
                    {schedule ? (
                      <div className="workcal-details">
                        <div>
                          <strong>Thời gian:</strong>{" "}
                          {schedule.startTime?.slice(0, 5)} -{" "}
                          {schedule.endTime?.slice(0, 5)}
                        </div>
                        <div>
                          <strong>Tăng ca:</strong>{" "}
                          {schedule.overtime ? "Có" : "Không"}
                        </div>
                        <div>
                          <strong>Tổ:</strong> {schedule.lineName}
                        </div>

                        {dayjs(dateStr).isAfter(dayjs(), "day") && (
                          <button
                            className="workcal-leave-btn"
                            onClick={() =>
                              (window.location.href = `/create-application?type=leave&date=${dateStr}`)
                            }
                          >
                            Xin nghỉ
                          </button>
                        )}
                      </div>
                    ) : (
                      <div className="workcal-noshift">--</div>
                    )}
                  </>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </MainLayout>
  );
};

export default EmployeeWorkScheduleView;
