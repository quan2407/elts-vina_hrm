import React, { useEffect, useState } from "react";
import workScheduleService from "../services/workScheduleService";
import WorkScheduleModal from "./WorkScheduleModal";
import holidayService from "../services/holidayService ";
import "../styles/WorkScheduleTable.css";
import { useSearchParams } from "react-router-dom";

const weekdays = ["CN", "Th 2", "Th 3", "Th 4", "Th 5", "Th 6", "Th 7"];

const toHourLabel = (h) => {
  if (h == null) return "";
  const v = Math.round(Number(h) * 10) / 10;
  if (!Number.isFinite(v)) return "";
  return `${v % 1 === 0 ? v.toFixed(0) : v.toFixed(1)}h`;
};

function WorkScheduleTable({
  month,
  year,
  setMonth,
  setYear,
  onStatusChange,
  onMonthYearChange,
  canEdit = true,
  reloadTrigger,
  onRejectReasonChange = () => {},
  onNeedRevisionChange = () => {},
}) {
  const [searchParams] = useSearchParams();
  const focusDateParam = searchParams.get("focusDate"); // yyyy-MM-dd
  const departmentNameParam = searchParams.get("departmentName");
  const lineNameParam = searchParams.get("lineName");

  const [dates, setDates] = useState([]);
  const [data, setData] = useState([]);
  const [selectedDeptId, setSelectedDeptId] = useState(null);
  const [selectedLineId, setSelectedLineId] = useState(null);
  const [modalError, setModalError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const pickErrMsg = (err) =>
    err?.response?.data?.message ||
    err?.response?.data ||
    err?.message ||
    "Có lỗi xảy ra. Vui lòng thử lại.";

  const [workType, setWorkType] = useState("normal");
  const [availableMonths, setAvailableMonths] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedDept, setSelectedDept] = useState("");
  const [selectedLine, setSelectedLine] = useState("");
  const [selectedDate, setSelectedDate] = useState("");
  const [selectedWorkScheduleId, setSelectedWorkScheduleId] = useState(null);
  const [startTime, setStartTime] = useState("08:00");
  const [endTime, setEndTime] = useState("17:00");
  const [holidays, setHolidays] = useState([]);
  const [selectedDetailId, setSelectedDetailId] = useState(null);
  const [canEditModal, setCanEditModal] = useState(true);

  const pad = (n) => n.toString().padStart(2, "0");
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const todayIso = `${today.getFullYear()}-${pad(today.getMonth() + 1)}-${pad(
    today.getDate()
  )}`;
  console.log("📆 FIXED todayIso:", todayIso);

  const getTotalMonthValue = (m, y) => y * 12 + m;

  const filteredData = React.useMemo(() => {
    const isHr = (name) => (name || "").toLowerCase().includes("nhân sự");
    return data.filter((dept) => !isHr(dept.departmentName));
  }, [data]);

  const handleDelete = (id) => {
    workScheduleService
      .deleteWorkScheduleDetail(id)
      .then(() => {
        setModalOpen(false);
        fetchDataAndStatus(month, year);
      })
      .catch((err) => console.error("Lỗi xóa lịch làm việc:", err));
  };

  const getLastAvailableMonth = () => {
    if (availableMonths.length === 0) return null;
    return availableMonths.reduce((a, b) =>
      getTotalMonthValue(b.month, b.year) > getTotalMonthValue(a.month, a.year)
        ? b
        : a
    );
  };

  const getMinAvailableMonth = () => {
    if (availableMonths.length === 0) return null;
    return availableMonths.reduce((a, b) =>
      getTotalMonthValue(b.month, b.year) < getTotalMonthValue(a.month, a.year)
        ? b
        : a
    );
  };

  const getNextAvailableMonth = () => {
    if (availableMonths.length === 0) return false;
    const sorted = [...availableMonths].sort(
      (a, b) =>
        getTotalMonthValue(a.month, a.year) -
        getTotalMonthValue(b.month, b.year)
    );
    const last = sorted.at(-1);
    if (!last) return false;
    return (
      getTotalMonthValue(month, year) <
      getTotalMonthValue(last.month, last.year) + 1
    );
  };

  const getPrevAvailableMonth = () => {
    if (availableMonths.length === 0) return false;
    const sorted = [...availableMonths].sort(
      (a, b) =>
        getTotalMonthValue(a.month, a.year) -
        getTotalMonthValue(b.month, b.year)
    );
    const first = sorted[0];
    if (!first) return false;
    return (
      getTotalMonthValue(month, year) >
      getTotalMonthValue(first.month, first.year) - 1
    );
  };

  const isMonthSelectable = (m, y) => {
    if (availableMonths.length === 0) return false;
    const totalMonth = getTotalMonthValue(m, y);
    const todayMonth = getTotalMonthValue(
      today.getMonth() + 1,
      today.getFullYear()
    );
    return (
      totalMonth >= todayMonth && // ✅ chặn tháng/năm quá khứ
      totalMonth <=
        Math.max(
          ...availableMonths.map((d) => getTotalMonthValue(d.month, d.year))
        ) +
          1
    );
  };

  const fetchDataAndStatus = (m, y) => {
    workScheduleService
      .getWorkScheduleByMonth(m, y)
      .then((res) => {
        setData(res.data);
        const allLines = res.data.flatMap((dept) => dept.lines);
        const anyNeedRevision = allLines.some(
          (line) => line.needRevision === true
        );
        onNeedRevisionChange(anyNeedRevision);

        if (onStatusChange) {
          const allAccepted =
            allLines.length > 0 &&
            allLines.every((line) => String(line.accepted) === "true");
          const allSubmitted =
            allLines.length > 0 &&
            allLines.every((line) => String(line.submitted) === "true");
          const allRejected =
            allLines.length > 0 &&
            allLines.every(
              (line) =>
                !line.accepted &&
                !line.submitted &&
                line.rejectReason?.trim()?.length > 0
            );

          console.log("🔥 Gọi onStatusChange từ WorkScheduleTable với:", {
            allAccepted,
            allSubmitted,
            allRejected,
          });

          if (allAccepted) onStatusChange("approved");
          else if (allSubmitted) onStatusChange("submitted");
          else onStatusChange("not-submitted");
        }

        if (onRejectReasonChange) {
          const firstRejected = allLines.find(
            (line) => !line.accepted && !line.submitted && line.rejectReason
          );
          onRejectReasonChange(firstRejected?.rejectReason || "");
        }
      })
      .catch(() => {
        setData([]);
        if (onStatusChange) onStatusChange("not-submitted");
      });
  };

  const handleCreateSchedule = (month, year) => {
    workScheduleService
      .createWorkSchedulesForMonth(month, year)
      .then(() => fetchDataAndStatus(month, year))
      .catch((err) => console.error("Lỗi tạo lịch làm việc:", err));
  };

  // Auto-scroll đến line & ngày theo query params
  useEffect(() => {
    if (
      !departmentNameParam ||
      !lineNameParam ||
      data.length === 0 ||
      dates.length === 0 ||
      !focusDateParam
    )
      return;

    const timeout = setTimeout(() => {
      const decodedDept = decodeURIComponent(departmentNameParam);
      const decodedLine = decodeURIComponent(lineNameParam);
      const lineElements = document.querySelectorAll(
        ".work-schedule-line-name"
      );

      for (let el of lineElements) {
        const lineText = el.getAttribute("data-linename")?.trim();
        const deptText = el.getAttribute("data-deptname")?.trim();
        console.log("🔍 So sánh:", deptText, "-", lineText);

        if (lineText === decodedLine && deptText === decodedDept) {
          console.log("✅ Match:", deptText, lineText);

          // Scroll dọc tới line
          el.scrollIntoView({ behavior: "smooth", block: "center" });

          // Scroll ngang tới ngày
          const index = dates.findIndex((d) => d.iso === focusDateParam);
          if (index !== -1) {
            const scrollContainer = document.querySelector(
              ".work-schedule-scroll"
            );
            if (scrollContainer) {
              const cellWidth = 100;
              scrollContainer.scrollTo({
                left: index * cellWidth,
                behavior: "smooth",
              });
            }
          }
          break;
        }
      }
    }, 200);

    return () => clearTimeout(timeout);
  }, [departmentNameParam, lineNameParam, data, dates, focusDateParam]);

  // Lấy ngày & holiday
  useEffect(() => {
    holidayService.getAllHolidays().then((res) => {
      setHolidays(res.data || []);
    });

    const numDays = new Date(year, month, 0).getDate();
    const tempDates = Array.from({ length: numDays }, (_, i) => {
      const dateObj = new Date(year, month - 1, i + 1);
      return {
        full: dateObj.toLocaleDateString("vi-VN"),
        weekday: weekdays[dateObj.getDay()],
        isSunday: dateObj.getDay() === 0,
        isSaturday: dateObj.getDay() === 6,
        iso: `${dateObj.getFullYear()}-${(dateObj.getMonth() + 1)
          .toString()
          .padStart(2, "0")}-${dateObj.getDate().toString().padStart(2, "0")}`,
      };
    });
    setDates(tempDates);

    fetchDataAndStatus(month, year);

    workScheduleService
      .getAvailableMonths()
      .then((res) => setAvailableMonths(res.data || []))
      .catch((err) => console.error("Lỗi lấy danh sách tháng:", err));
  }, [month, year, reloadTrigger]);

  // Recalc dates with holidays
  useEffect(() => {
    const numDays = new Date(year, month, 0).getDate();
    const tempDates = Array.from({ length: numDays }, (_, i) => {
      const dateObj = new Date(year, month - 1, i + 1);
      const isoDate = `${dateObj.getFullYear()}-${(dateObj.getMonth() + 1)
        .toString()
        .padStart(2, "0")}-${dateObj.getDate().toString().padStart(2, "0")}`;
      const isHoliday = holidays.some(
        (holiday) => isoDate >= holiday.startDate && isoDate <= holiday.endDate
      );
      return {
        full: dateObj.toLocaleDateString("vi-VN"),
        weekday: weekdays[dateObj.getDay()],
        isSunday: dateObj.getDay() === 0,
        isHoliday,
        iso: isoDate,
      };
    });
    setDates(tempDates);
  }, [month, year, holidays]);

  const handlePrevMonth = () => {
    const newMonth = month === 1 ? 12 : month - 1;
    const newYear = month === 1 ? year - 1 : year;
    if (isMonthSelectable(newMonth, newYear)) {
      setMonth(newMonth);
      setYear(newYear);
      if (onMonthYearChange) onMonthYearChange(newMonth, newYear);
    }
  };

  const handleNextMonth = () => {
    const newMonth = month === 12 ? 1 : month + 1;
    const newYear = month === 12 ? year + 1 : year;
    if (isMonthSelectable(newMonth, newYear)) {
      setMonth(newMonth);
      setYear(newYear);
      if (onMonthYearChange) onMonthYearChange(newMonth, newYear);
    }
  };

  const nextDisabled = !getNextAvailableMonth();
  const prevDisabled = !getPrevAvailableMonth();

  const handleOpenModal = (
    deptId,
    deptName,
    lineId,
    lineName,
    dateIso,
    workScheduleId,
    detail = null,
    editable = true
  ) => {
    setSelectedDeptId(deptId);
    setSelectedLineId(lineId);
    setSelectedDept(deptName);
    setSelectedLine(lineName);
    setSelectedDate(dateIso);
    setSelectedWorkScheduleId(workScheduleId);

    if (detail?.startTime && detail?.endTime) {
      console.log("Chi tiết lịch:", detail);
      const start = detail.startTime.slice(0, 5);
      const end = detail.endTime.slice(0, 5);

      if (start === "08:00" && end === "17:00") {
        setWorkType("normal");
      } else if (start === "08:00" && end === "20:00") {
        setWorkType("overtime");
      } else {
        setWorkType("custom");
      }
      setStartTime(start);
      setEndTime(end);
      setSelectedDetailId(detail.id);
      setCanEditModal(editable);
      console.log("Giờ làm việc:", start, "-", end);
    } else {
      setWorkType("normal");
      setStartTime("08:00");
      setEndTime("17:00");
      setSelectedDetailId(null);
      setCanEditModal(true);
    }
    setModalError(null);
    setModalOpen(true);
  };

  const handleSave = async () => {
    if (endTime <= startTime) {
      alert("Giờ kết thúc phải sau giờ bắt đầu.");
      return;
    }

    const payload = {
      dateWork: selectedDate,
      startTime,
      endTime,
      workScheduleId: selectedWorkScheduleId,
    };

    const savePromise = selectedDetailId
      ? workScheduleService.updateWorkScheduleDetail({
          workScheduleDetailId: selectedDetailId,
          startTime,
          endTime,
        })
      : workScheduleService
          .resolveWorkScheduleId(selectedDeptId, selectedLineId, selectedDate)
          .then((res) =>
            workScheduleService.createWorkScheduleDetail({
              ...payload,
              workScheduleId: res.data,
            })
          );

    setSubmitting(true);
    setModalError(null);

    try {
      await savePromise;
      setModalOpen(false);
      fetchDataAndStatus(month, year);
    } catch (err) {
      console.error("Lỗi thêm/cập nhật lịch làm việc:", err);
      setModalError(pickErrMsg(err)); // có thể là string (VD: vượt 40h) hoặc object (VD: { endTime: [...] })
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="work-schedule-container">
      {/* Toolbar */}
      <div className="work-schedule-toolbar-wrapper">
        <div className="work-schedule-toolbar">
          <button
            onClick={handlePrevMonth}
            className="work-schedule-nav-btn"
            style={{
              backgroundColor: prevDisabled ? "#ccc" : "#000",
              color: "#fff",
            }}
            disabled={prevDisabled}
          >
            ◀
          </button>

          <select
            value={month}
            onChange={(e) => {
              const m = parseInt(e.target.value, 10);
              if (isMonthSelectable(m, year)) {
                setMonth(m);
                if (onMonthYearChange) onMonthYearChange(m, year);
              }
            }}
          >
            {Array.from({ length: 12 }, (_, i) => {
              const m = i + 1;
              return (
                <option
                  key={m}
                  value={m}
                  disabled={!isMonthSelectable(m, year)}
                >
                  Tháng {m}
                </option>
              );
            })}
          </select>

          <select
            value={year}
            onChange={(e) => {
              const y = parseInt(e.target.value, 10);
              if (isMonthSelectable(month, y)) {
                setYear(y);
                if (onMonthYearChange) onMonthYearChange(month, y);
              }
            }}
          >
            {Array.from({ length: 5 }, (_, i) => {
              const y = 2023 + i;
              return (
                <option
                  key={y}
                  value={y}
                  disabled={!availableMonths.some((m) => m.year === y)}
                >
                  Năm {y}
                </option>
              );
            })}
          </select>

          <button
            onClick={handleNextMonth}
            className="work-schedule-nav-btn"
            style={{
              backgroundColor: nextDisabled ? "#ccc" : "#000",
              color: "#fff",
            }}
            disabled={nextDisabled}
          >
            ▶
          </button>
        </div>
      </div>

      {/* Body */}
      {data.length === 0 ? (
        <div className="work-schedule-empty">
          <p className="work-schedule-empty-text">
            Chưa có lịch làm việc nào được tạo cho tháng {month}/{year}
          </p>
          {canEdit && (
            <button
              className="work-schedule-create-btn"
              onClick={() => handleCreateSchedule(month, year)}
            >
              Tạo lịch cho tháng này
            </button>
          )}
        </div>
      ) : (
        <div className="work-schedule-layout">
          {/* Cột trái: phòng ban / chuyền */}
          <div className="work-schedule-left">
            <div className="work-schedule-header work-schedule-header-dept work-schedule-span-2">
              Phòng ban
            </div>
            <div className="work-schedule-header work-schedule-header-line work-schedule-span-2">
              Chuyền
            </div>

            {filteredData.map((dept) =>
              dept.lines.map((line, idx) => (
                <React.Fragment key={`${dept.departmentId}-${idx}`}>
                  {idx === 0 && (
                    <div
                      className="work-schedule-cell work-schedule-dept-name"
                      data-deptname={dept.departmentName}
                      style={{ gridRow: `span ${dept.lines.length}` }}
                    >
                      {dept.departmentName}
                    </div>
                  )}

                  <div
                    className="work-schedule-cell work-schedule-line-name"
                    data-linename={line.lineName}
                    data-deptname={dept.departmentName}
                  >
                    <div className="line-title">{line.lineName || <i></i>}</div>
                    {line?.monthlyOtRemainingHours != null && (
                      <div
                        className="work-schedule-ot-remaining"
                        title={
                          line?.monthlyOtUsedMinutes != null &&
                          line?.monthlyOtCapMinutes != null
                            ? `Đã dùng: ${(
                                line.monthlyOtUsedMinutes / 60
                              ).toFixed(1)}h / Trần: ${(
                                line.monthlyOtCapMinutes / 60
                              ).toFixed(0)}h`
                            : undefined
                        }
                      >
                        OT còn: {toHourLabel(line.monthlyOtRemainingHours)}
                      </div>
                    )}
                  </div>
                </React.Fragment>
              ))
            )}
          </div>

          {/* Phần phải: lưới ngày */}
          <div className="work-schedule-scroll">
            <div
              className="work-schedule-grid"
              style={{ gridTemplateColumns: `repeat(${dates.length}, 100px)` }}
            >
              {dates.map((d, idx) => (
                <div
                  key={`date-${idx}`}
                  className={`work-schedule-header work-schedule-header-date ${
                    d.isSunday || d.isHoliday ? "work-schedule-weekend" : ""
                  }`}
                >
                  {d.full}
                </div>
              ))}

              {dates.map((d, idx) => (
                <div
                  key={`weekday-${idx}`}
                  className={`work-schedule-header work-schedule-header-date ${
                    d.isSunday || d.isHoliday ? "work-schedule-weekend" : ""
                  }`}
                >
                  {d.weekday}
                </div>
              ))}

              {filteredData.map((dept) =>
                dept.lines.map((line, idx) =>
                  line.workDetails.map((detail, i) => {
                    const isOvertime =
                      detail.overtime === true || detail.overtime === "true";
                    const isFocus =
                      dates[i]?.iso === focusDateParam &&
                      line.lineName === lineNameParam &&
                      dept.departmentName === departmentNameParam;

                    return (
                      <div
                        key={`${dept.departmentId}-${idx}-${i}`}
                        className={`work-schedule-cell work-schedule-day-cell ${
                          isFocus ? "work-schedule-focus-cell" : ""
                        }`}
                      >
                        {detail.startTime && detail.endTime ? (
                          <span
                            className={`work-schedule-time-text ${
                              isOvertime ? "work-schedule-overtime" : ""
                            }`}
                            style={{ cursor: "pointer" }}
                            onClick={() =>
                              handleOpenModal(
                                dept.departmentId,
                                dept.departmentName,
                                line?.lineId,
                                line?.lineName ?? "",
                                dates[i].iso,
                                detail.workScheduleId,
                                detail,
                                canEdit
                              )
                            }
                          >
                            {detail.startTime?.slice(0, 5)} -{" "}
                            {detail.endTime?.slice(0, 5)}
                          </span>
                        ) : (
                          canEdit &&
                          dates[i]?.iso >= todayIso && (
                            <button
                              className="work-schedule-add-btn"
                              onClick={() => {
                                handleOpenModal(
                                  dept.departmentId,
                                  dept.departmentName,
                                  line?.lineId,
                                  line?.lineName ?? "",
                                  dates[i].iso,
                                  detail.workScheduleId,
                                  null,
                                  canEdit
                                );
                              }}
                            >
                              +
                            </button>
                          )
                        )}
                      </div>
                    );
                  })
                )
              )}
            </div>
          </div>
        </div>
      )}

      <WorkScheduleModal
        isOpen={modalOpen}
        onClose={() => setModalOpen(false)}
        onSave={handleSave}
        data={{
          departmentName: selectedDept,
          lineName: selectedLine,
          date: selectedDate,
          startTime,
          endTime,
          workType,
          setWorkType,
          onChange: (field, value) => {
            if (field === "startTime") setStartTime(value);
            if (field === "endTime") setEndTime(value);
            if (field === "workType") {
              setWorkType(value);
              if (value === "normal") {
                setStartTime("08:00");
                setEndTime("17:00");
              } else if (value === "overtime") {
                setStartTime("08:00");
                setEndTime("20:00");
              }
            }
          },
          workScheduleId: selectedWorkScheduleId,
          id: selectedDetailId,
          onDelete: handleDelete,
          canEdit: canEditModal,
        }}
        errorMessages={modalError}
        submitting={submitting}
      />
    </div>
  );
}

export default WorkScheduleTable;
