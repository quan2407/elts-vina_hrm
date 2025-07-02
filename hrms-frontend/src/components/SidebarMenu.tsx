"use client";
import * as React from "react";

interface MenuItemProps {
  icon: string;
  label: string;
  isActive?: boolean;
  hasNotification?: boolean;
  notificationCount?: number;
}

const MenuItem: React.FC<MenuItemProps> = ({
  icon,
  label,
  isActive = false,
  hasNotification = false,
  notificationCount,
}) => {
  const baseClasses = isActive
    ? "flex gap-3.5 px-6 py-5 text-lg font-bold text-black bg-yellow-400 rounded-2xl max-md:px-5"
    : "flex gap-3.5 text-lg";

  return (
    <div className={baseClasses}>
      <img
        src={icon}
        className="object-contain shrink-0 aspect-[1.22] w-[33px]"
        alt=""
      />
      <span className="my-auto basis-auto">{label}</span>
      {hasNotification && notificationCount && (
        <div className="z-10 px-1.5 my-auto bg-red-600 rounded-full border border-white border-solid shadow-sm h-[23px] w-[23px] text-xs font-bold text-white">
          {notificationCount}
        </div>
      )}
    </div>
  );
};

export const SidebarMenu: React.FC = () => {
  return (
    <nav className="flex flex-col w-full">
      <h3 className="mt-11 ml-6 text-base max-md:mt-10 max-md:ml-2.5">
        Features
      </h3>

      <div className="flex gap-3.5 mt-9 ml-7 text-lg whitespace-nowrap max-md:ml-2.5">
        <img
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/8509e1b79978ac84de861ec085921a0ba01bbbc3?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
          className="object-contain shrink-0 aspect-[1.14] w-[33px]"
          alt=""
        />
        <span className="self-start">Dashboard</span>
      </div>

      <div className="flex gap-5 justify-between self-end mt-8 max-w-full whitespace-nowrap w-[324px]">
        <div className="flex flex-col items-start self-start mt-3 text-lg">
          <div className="flex gap-3.5">
            <img
              src="https://cdn.builder.io/api/v1/image/assets/TEMP/69ba6a5fed9e9d645d9b791bcc7d05435e6a62c5?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
              className="object-contain shrink-0 w-9 aspect-[1.33]"
              alt=""
            />
            <span>Messages</span>
          </div>
          <h3 className="mt-9 text-base">Recruitment</h3>
          <div className="flex gap-3.5 mt-8">
            <img
              src="https://cdn.builder.io/api/v1/image/assets/TEMP/0003cd8d6e583003ee49d28ad00b38030e90792d?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
              className="object-contain shrink-0 aspect-[1.13] w-[35px]"
              alt=""
            />
            <span className="my-auto">Jobs</span>
          </div>
          <div className="flex flex-col self-stretch px-px mt-9 w-full">
            <div className="flex gap-3">
              <img
                src="https://cdn.builder.io/api/v1/image/assets/TEMP/ee4a23bc2877ab49c6506905ab132e1624a2c1d1?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
                className="object-contain shrink-0 w-10 aspect-[1.43]"
                alt=""
              />
              <span className="self-start">Candidates</span>
            </div>
            <div className="flex gap-5 self-start mt-8">
              <img
                src="https://cdn.builder.io/api/v1/image/assets/TEMP/80b135a432c54be0a913200d2593aa79a1d8b5a6?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
                className="object-contain shrink-0 aspect-[0.76] w-[25px]"
                alt=""
              />
              <span className="self-start">Resumes</span>
            </div>
          </div>
          <h3 className="mt-9 text-base">Organization</h3>
        </div>
        <div className="flex flex-col text-xs font-bold">
          <div className="flex gap-4 items-start">
            <div className="flex shrink-0 self-end mt-8 bg-black bg-opacity-0 h-[25px] w-[25px]" />
            <div className="z-10 px-1.5 my-auto bg-red-600 rounded-full border border-white border-solid shadow-sm h-[23px] w-[23px]">
              13
            </div>
            <div className="flex shrink-0 self-start bg-black bg-opacity-0 h-[25px] w-[25px]" />
          </div>
          <div className="flex shrink-0 self-center mt-60 bg-black bg-opacity-0 h-[25px] w-[25px] max-md:mt-10" />
        </div>
      </div>

      <div className="flex gap-4 mt-5 ml-6 text-lg max-md:ml-2.5">
        <img
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/5efaf636bec5dbefd1a4298187df3e1441f4d5ef?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
          className="object-contain shrink-0 w-7 aspect-square"
          alt=""
        />
        <span className="my-auto basis-auto">Employee Management</span>
      </div>

      <div className="flex gap-3.5 mt-6 ml-6 text-lg max-md:ml-2.5">
        <img
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/193f6b21faecbbc5dedd3cadf67c3aeac5a3fae1?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
          className="object-contain shrink-0 aspect-square w-[34px]"
          alt=""
        />
        <span className="my-auto basis-auto">Leave Management</span>
      </div>

      <MenuItem
        icon="https://cdn.builder.io/api/v1/image/assets/TEMP/f5111edb8f2fd900033ce7c1539414d9ca523373?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
        label="Performance Management"
        isActive={true}
      />

      <div className="flex z-10 shrink-0 self-end -mt-2 mr-24 bg-black bg-opacity-0 h-[25px] w-[25px] max-md:mr-2.5" />

      <div className="flex gap-4 self-start ml-4 mt-3 text-lg max-md:ml-2.5">
        <img
          src="https://cdn.builder.io/api/v1/image/assets/TEMP/a2bae744a375d9906f07afec79b2b0d7139402c5?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
          className="object-contain shrink-0 self-start aspect-[1.48] w-[34px]"
          alt=""
        />
        <span>Payroll Management</span>
      </div>

      <div className="flex flex-col justify-center items-center px-16 py-3.5 mt-12 w-full font-bold bg-red-600 rounded-2xl shadow-[13px_4px_14px_rgba(0,0,0,0.12)] max-md:px-5 max-md:mt-10">
        <button className="flex gap-3 max-w-full w-[102px] text-white">
          <img
            src="https://cdn.builder.io/api/v1/image/assets/TEMP/82f2ec22028f258ca2ba2c5d5a3fb3bb49360f2d?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
            className="object-contain shrink-0 w-6 aspect-[0.96]"
            alt=""
          />
          <span>Log Out</span>
        </button>
      </div>
    </nav>
  );
};
