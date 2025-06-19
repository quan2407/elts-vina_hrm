import React from "react";
import {
  LayoutDashboard,
  Users,
  Briefcase,
  UserPlus,
  MessageSquare,
  Target,
} from "lucide-react";

export const systemMenus = [
  {
    text: "Account List",
    path: "/",
    icon: (isActive) => (
      <Users size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  },
  {
    text: "Messages",
    path: "/messages",
    icon: (isActive) => (
      <MessageSquare size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
    badge: 13,
  },
];

export const hrMenus = [
  {
    text: "Dashboard",
    path: "/",
    icon: (isActive) => (
      <LayoutDashboard size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  },
  {
    text: "Employee Management",
    path: "/employee-management",
    icon: (isActive) => (
      <Users size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  },
  {
    text: "Jobs",
    path: "/jobs",
    icon: (isActive) => (
      <Briefcase size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  },
  {
    text: "Performance Management",
    path: "/targets",
    icon: (isActive) => (
      <Target size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  },
];
