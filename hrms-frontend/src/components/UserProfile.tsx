"use client";
import * as React from "react";

interface UserProfileProps {
  logoUrl: string;
  avatarUrl: string;
  userName: string;
  userRole: string;
}

export const UserProfile: React.FC<UserProfileProps> = ({
  logoUrl,
  avatarUrl,
  userName,
  userRole,
}) => {
  return (
    <div className="flex flex-col items-start pl-3.5 w-full">
      <img
        src={logoUrl}
        className="object-contain max-w-full aspect-[4.29] w-[296px] max-md:ml-1.5"
        alt="Company Logo"
      />
      <div className="flex gap-5 justify-between self-center mt-5 w-72 max-w-full">
        <img
          src={avatarUrl}
          className="object-contain shrink-0 w-28 max-w-full rounded-none aspect-square"
          alt="User Avatar"
        />
        <div className="flex flex-col my-auto">
          <h2 className="text-2xl font-bold">{userName}</h2>
          <p className="self-start mt-5 text-sm">{userRole}</p>
        </div>
      </div>
    </div>
  );
};
