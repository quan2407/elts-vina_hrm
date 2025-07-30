import React from "react";
import MainLayout from "../components/MainLayout";
import AccountTable from "../components/AccountTable";
import { Plus } from "lucide-react";
import "../styles/ManagementLayout.css";

function AccountManagement() {
  return (
    <MainLayout>
      <div className="content-wrapper">
        <div className="page-header">
          <h1 className="page-title">Quản lý tài khoản</h1>
        </div>
        <AccountTable />
      </div>
    </MainLayout>
  );
}

export default AccountManagement;
