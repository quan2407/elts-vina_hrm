import React from "react";

// Dashboard icon (HR)
const DashboardIcon = (
  <svg
    width="33"
    height="29"
    viewBox="0 0 33 29"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M19.0781 0H31.4531C32.3074 0 33 0.695547 33 1.55357V11.9107C33 12.7687 32.3074 13.4643 31.4531 13.4643H19.0781C18.2238 13.4643 17.5312 12.7687 17.5312 11.9107V1.55357C17.5312 0.695547 18.2238 0 19.0781 0ZM13.9219 0H1.54688C0.692549 0 0 0.695547 0 1.55357V11.9107C0 12.7687 0.692549 13.4643 1.54688 13.4643H13.9219C14.7762 13.4643 15.4688 12.7687 15.4688 11.9107V1.55357C15.4688 0.695547 14.7762 0 13.9219 0ZM0 17.0893V27.4464C0 28.3045 0.692549 29 1.54688 29H13.9219C14.7762 29 15.4688 28.3045 15.4688 27.4464V17.0893C15.4688 16.2313 14.7762 15.5357 13.9219 15.5357H1.54688C0.692549 15.5357 0 16.2313 0 17.0893ZM19.0781 29H31.4531C32.3074 29 33 28.3045 33 27.4464V17.0893C33 16.2313 32.3074 15.5357 31.4531 15.5357H19.0781C18.2238 15.5357 17.5312 16.2313 17.5312 17.0893V27.4464C17.5312 28.3045 18.2238 29 19.0781 29Z"
      fill="white"
    />
  </svg>
);

// Account list icon (Admin)
const AccountListIcon = (
  <svg
    width="28"
    height="28"
    viewBox="0 0 28 28"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
  >
    <path
      d="M14 15.75C18.3477 15.75 21.875 12.2227 21.875 7.875C21.875 3.52734 18.3477 0 14 0C9.65234 0 6.125 3.52734 6.125 7.875C6.125 12.2227 9.65234 15.75 14 15.75ZM21 17.5H17.9867C16.7727 18.0578 15.4219 18.375 14 18.375C12.5781 18.375 11.2328 18.0578 10.0133 17.5H7C3.13359 17.5 0 20.6336 0 24.5V25.375C0 26.8242 1.17578 28 2.625 28H25.375C26.8242 28 28 26.8242 28 25.375V24.5C28 20.6336 24.8664 17.5 21 17.5Z"
      fill="white"
    />
  </svg>
);

// Mock icons
const MessageIcon = (
  <div style={{ width: 20, height: 20, background: "#fff", borderRadius: 4 }} />
);
const EmployeeIcon = (
  <div style={{ width: 20, height: 20, background: "#fff", borderRadius: 4 }} />
);
const JobIcon = (
  <div style={{ width: 20, height: 20, background: "#fff", borderRadius: 4 }} />
);
const CandidateIcon = (
  <div style={{ width: 20, height: 20, background: "#fff", borderRadius: 4 }} />
);

// Menus
export const systemMenus = [
  { text: "Account List", path: "/", icon: AccountListIcon },
  { text: "Messages", path: "/messages", icon: MessageIcon, badge: 13 },
];

export const hrMenus = [
  { text: "Dashboard", path: "/", icon: DashboardIcon },
  {
    text: "Employee Management",
    path: "/employee-management",
    icon: EmployeeIcon,
  },
  { text: "Jobs", path: "/jobs", icon: JobIcon },
  { text: "Candidates", path: "/candidates", icon: CandidateIcon },
];
