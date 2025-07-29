import React from "react";
import "../../styles/JobsDashboardTable.css";
import * as XLSX from "xlsx";

const RecruitmentDashboardTable = ({ data = [] }) => {
  return (
    <div className="jobdash-table-wrapper">
      <div className="jobdash-table">
        <div className="jobdash-table-header">
          <div className="jobdash-header-cell">Id</div>
          <div className="jobdash-header-cell">Nội dung tuyển dụng</div>
          <div className="jobdash-header-cell">Số lượng tuyển dụng</div>
          <div className="jobdash-header-cell">Số lượng ứng tuyển</div>
          <div className="jobdash-header-cell">Số lượng đã tuyển</div>
        </div>

        {data.map((job) => (
          <div
            key={job.recruitmentId}
            className="jobdash-table-row"
          >
            <div className="jobdash-table-cell">{job.recuitmentId}</div>
            <div className="jobdash-table-cell">{job.recruitmentTitle}</div>
            <div className="jobdash-table-cell">{job.canTuyen}</div>
            <div className="jobdash-table-cell">{job.ungTuyen}</div>
            <div className="jobdash-table-cell">{job.daTuyen}</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default RecruitmentDashboardTable;
