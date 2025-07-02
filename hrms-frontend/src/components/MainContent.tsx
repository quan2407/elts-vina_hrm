"use client";
import * as React from "react";
import { NavigationTabs } from "./NavigationTabs";
import { TargetsTable } from "./TargetsTable";

export const MainContent: React.FC = () => {
  return (
    <main className="ml-5 w-[78%] max-md:ml-0 max-md:w-full">
      <div className="flex flex-col mt-14 w-full max-md:mt-10 max-md:max-w-full">
        <header className="flex flex-wrap gap-5 justify-between self-end mr-8 max-w-full w-[1036px] max-md:mr-2.5">
          <div className="flex flex-wrap gap-10 pr-10 text-xl font-bold bg-white rounded-2xl max-md:max-w-full">
            <div className="flex gap-9">
              <div className="flex gap-8 items-start px-7 pt-6 pb-1.5 text-white bg-blue-900 rounded-2xl max-md:px-5">
                <span>All Candidates</span>
                <img
                  src="https://cdn.builder.io/api/v1/image/assets/TEMP/372dbbbce1279777159fe12cc807b3fa357305f1?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
                  className="object-contain shrink-0 mt-1 aspect-square w-[18px]"
                  alt=""
                />
              </div>
              <div className="my-auto text-neutral-400">Search...</div>
            </div>
            <img
              src="https://cdn.builder.io/api/v1/image/assets/TEMP/be3f8ad0779258323991e48c687296db49159e5e?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
              className="object-contain shrink-0 my-auto w-8 aspect-square"
              alt=""
            />
          </div>
          <div className="flex gap-7 self-start mt-1">
            <img
              src="https://cdn.builder.io/api/v1/image/assets/TEMP/7e63237b-e8ca-406c-9109-cca0c2c33e19?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
              className="object-contain shrink-0 self-start aspect-[1.21] bg-black bg-opacity-0 w-[46px]"
              alt=""
            />
            <div className="flex relative flex-col aspect-square w-[53px]">
              <img
                src="https://cdn.builder.io/api/v1/image/assets/TEMP/118406604e0b19f3902bf1eefe1102cec7cb1528?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
                className="object-cover absolute inset-0 size-full"
                alt=""
              />
              <img
                src="https://cdn.builder.io/api/v1/image/assets/TEMP/2f64d060aade4ba14ae4c3d8380150ed151dfd90?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
                className="object-contain w-full aspect-square"
                alt=""
              />
            </div>
            <img
              src="https://cdn.builder.io/api/v1/image/assets/TEMP/bb4240150deef15e697e20059f02a512115d72e9?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
              className="object-contain shrink-0 aspect-[1.11] w-[59px]"
              alt=""
            />
          </div>
        </header>

        <h1 className="self-start mt-14 ml-5 text-2xl font-bold text-black max-md:mt-10 max-md:ml-2.5">
          Performance Management
        </h1>

        <NavigationTabs />
        <TargetsTable />
      </div>
    </main>
  );
};
