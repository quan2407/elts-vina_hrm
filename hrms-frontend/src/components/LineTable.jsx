import React, {
    useEffect,
    useState,
    forwardRef,
} from "react";
import "../styles/LineTable.css";
import * as XLSX from "xlsx";
import { useNavigate } from "react-router-dom";
import { getAllLines } from "../services/linesService";



const LineTable = forwardRef(({searchTerm}) => {

    const [line, setLine] = useState([]);
    const navigate = useNavigate();


    useEffect(() => {
        const fetchLines = async () => {
            try {
                const data = await getAllLines(searchTerm);
                setLine(data);
            } catch (error) {
                console.error("Lỗi khi load danh sách line:", error);
            }
        };
        fetchLines();
    }, [searchTerm]);

    const handleEmployeeClick = (id) => {
        navigate(`/employee/line/${id}`);
    }

    return (
        <div className="line-table-wrapper">
            <div className="line-table">
                <div className="line-table-header">
                    <div className="line-header-cell">Id</div>
                    <div className="line-header-cell">tên line</div>
                    <div className="line-header-cell">Số lượng nhân viên</div>
                    <div className="line-header-cell">tổ trưởng</div>
                    <div className="line-header-cell">Hành động</div>
                </div>
                {line.length === 0 && (
                    <div className="line-table-empty">Không tìm thấy dòng nào phù hợp.</div>
                )}
                {line.map((a) => (
                    <div
                        key={a.id}
                        className="line-table-row"
                    >
                        <div className="line-table-cell">{a.id}</div>
                        <div className="line-table-cell">{a.name}</div>
                        <div className="line-table-cell">{a.quantity}</div>
                        <div className="line-table-cell">{a.leaderName}</div>
                        <div className="line-table-cell">

                            <button className="viewcandidate-button" onClick={() => handleEmployeeClick(a.id)}>Danh sách nhân viên</button>

                        </div>
                    </div>
                ))}

            </div>
        </div>
    );
});

export default LineTable;