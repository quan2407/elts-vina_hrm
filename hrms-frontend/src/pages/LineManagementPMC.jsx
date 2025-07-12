import React, { useState } from "react";
import MainLayout from "../components/MainLayout";
import "../styles/ManagementLayout.css";
import LineTable from "../components/LineTable";



function LineManagement() {
    const [searchTerm, setSearchTerm] = useState("");



    return (
        <MainLayout>
            <div className="content-wrapper">
                <div className="page-header">
                    <h1 className="page-title">Danh sách Line</h1>
                    <div className="page-actions">

                        <form className="form-floating" onSubmit={(e) => e.preventDefault()}>
                            <input type="text" className="form-control" style={{ width: "240px" }} id="floatingInputValue" value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)} />
                            <label htmlFor="floatingInputValue">Tìm kiếm theo tên line</label>
                        </form>


                    </div>
                </div>
                <LineTable searchTerm={searchTerm}/>
            </div>
        </MainLayout>
    );
}

export default LineManagement;