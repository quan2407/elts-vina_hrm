import React from "react";
import Sidebar from "../components/Sidebar";
import Header from "../components/Header";
import "../styles/MainLayout.css";


const MainLayout = ({ children }) => {
return (
<div className="layout-container d-flex">
{/* Sidebar renders both desktop fixed and mobile offcanvas */}
<Sidebar />


<main className="main-content flex-grow-1 d-flex flex-column">
<Header />
<div className="content-wrapper flex-grow-1">{children}</div>
</main>
</div>
);
};


export default MainLayout;