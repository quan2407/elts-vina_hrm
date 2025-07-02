"use client";
import * as React from "react";

interface TabProps {
  label: string;
  isActive?: boolean;
}

const Tab: React.FC<TabProps> = ({ label, isActive = false }) => {
  const baseClasses = "px-16 py-5 shadow-sm rounded-[30px] max-md:px-5";
  const activeClasses = "font-bold bg-yellow-400";
  const inactiveClasses = "bg-white";

  return (
    <button
      className={`${baseClasses} ${isActive ? activeClasses : inactiveClasses}`}
    >
      {label}
    </button>
  );
};

export const NavigationTabs: React.FC = () => {
  return (
    <nav className="flex gap-5 justify-between mt-9 text-2xl text-center text-stone-900 max-md:max-w-full">
      <Tab label="Target Setup" />
      <Tab label="Targets" isActive={true} />
      <Tab label="Appraisals" />
      <Tab label="Settings" />
      <Tab label="Reports" />
    </nav>
  );
};
