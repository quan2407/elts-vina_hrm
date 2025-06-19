"use client";
import * as React from "react";
import { UserProfile } from "./UserProfile";
import { SidebarMenu } from "./SidebarMenu";

export const Sidebar: React.FC = () => {
  return (
    <aside className="w-[22%] max-md:ml-0 max-md:w-full">
      <div className="pt-8 w-full text-white rounded-none bg-indigo-950 max-md:mt-6">
        <UserProfile
          logoUrl="https://cdn.builder.io/api/v1/image/assets/TEMP/50f521771d2e737152e9210995e7a79982140d14?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
          avatarUrl="https://cdn.builder.io/api/v1/image/assets/TEMP/560b14f88b086323235bf91c1f166b43c0ce7427?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
          userName="Aman Admin"
          userRole="Admin"
        />

        <SidebarMenu />

        <img
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/e334bc584ecac7729d6457ecfad24b16096e4102?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
          className="object-contain z-10 mt-3.5 bg-blend-lighten aspect-[6.54] w-[359px]"
          alt=""
        />
      </div>
    </aside>
  );
};
