import React from "react";
import {
  LayoutDashboard,
  Users,
  Briefcase,
  UserPlus,
  MessageSquare,
  Target,
  Gift
} from "lucide-react";

export const systemMenus = [
  {
    text: "Danh sách tài khoản",
    path: "/",
    icon: (isActive) => (
      <Users size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  },
  {
    text: "Tin nhắn",
    path: "/messages",
    icon: (isActive) => (
      <MessageSquare size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
    badge: 13,
  },
];

export const hrMenus = [
  {
    text: "Tổng quan",
    path: "/",
    icon: (isActive) => (
      <LayoutDashboard size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  },
  {
    text: "Quản lý nhân viên",
    path: "/employee-management",
    icon: (isActive) => (
      <Users size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  },
  {
    text: "Tuyển dụng",
    path: "/jobs-management",

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
  {
    text: "Benefit Management",
    path: "/benefit",
    icon: (isActive) => (
      <Gift size={20} stroke={isActive ? "#4f46e5" : "white"} />
    ),
  }
];
