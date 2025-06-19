"use client";
import * as React from "react";
import { Sidebar } from "./Sidebar";
import { MainContent } from "./MainContent";

function Targets() {
  return (
    <div className="overflow-hidden pr-10 bg-sky-100 max-md:pr-5">
      <div className="flex gap-5 max-md:flex-col max-md:">
        <Sidebar />
        <MainContent />
      </div>
    </div>
  );
}

export default Targets;
