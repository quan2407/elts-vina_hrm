export const getRoleDisplayName = (roleCode) => {
  switch (roleCode) {
    case "ROLE_ADMIN":
      return "Quản trị viên";
    case "ROLE_HR":
      return "Nhân sự";
    case "ROLE_HR_MANAGER":
      return "Trưởng phòng nhân sự";
    case "ROLE_EMPLOYEE":
      return "Nhân viên";
    case "ROLE_PMC":
      return "Kế hoạch sản xuất";
    case "ROLE_PRODUCTION_MANAGER":
      return "Quản lý sản xuất";
    case "ROLE_LINE_LEADER":
      return "Tổ trưởng";
    default:
      return roleCode;
  }
};
