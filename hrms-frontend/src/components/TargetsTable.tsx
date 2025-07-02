"use client";
import * as React from "react";

interface TargetRowProps {
  name: string;
  title: string;
  kpiWeight: number;
  targetDate: string;
  actionIcon: string;
  isHighlighted?: boolean;
}

const TargetRow: React.FC<TargetRowProps> = ({
  name,
  title,
  kpiWeight,
  targetDate,
  actionIcon,
  isHighlighted = false,
}) => {
  const rowClasses = isHighlighted
    ? "flex flex-wrap gap-10 items-start pt-3.5 pr-16 pl-8 mt-6 max-w-full text-xl text-black bg-sky-100 rounded-md w-[1220px] max-md:px-5"
    : "flex flex-wrap gap-10 items-center mt-5 ml-8 max-w-full text-xl text-black w-[1120px]";

  return (
    <div className={rowClasses}>
      <div className="grow shrink self-stretch my-auto w-[135px]">{name}</div>
      <div
        className={`${isHighlighted ? "text-center" : "my-auto text-center"}`}
      >
        {title}
      </div>
      <div
        className={`text-3xl text-center ${isHighlighted ? "" : "self-stretch my-auto"}`}
      >
        {kpiWeight}
      </div>
      <div className={`text-center ${isHighlighted ? "" : "my-auto"}`}>
        {targetDate.split(" / ").map((date, index) => (
          <React.Fragment key={index}>
            {date}
            {index === 0 && <br />}
          </React.Fragment>
        ))}
      </div>
      <button className="flex gap-2 self-stretch px-8 py-3.5 text-lg text-center text-white whitespace-nowrap bg-blue-900 rounded-md shadow-[11px_4px_14px_rgba(0,0,0,0.12)] max-md:px-5">
        <span className="grow">Actions</span>
        <img
          src={actionIcon}
          className="object-contain shrink-0 self-start mt-1.5 w-3.5 aspect-square"
          alt=""
        />
      </button>
    </div>
  );
};

export const TargetsTable: React.FC = () => {
  const targets = [
    {
      name: "Abebe Kebede",
      title: "Sign new customers",
      kpiWeight: 5,
      targetDate: "01 -Jan-2021 / 01 -Jan-2022",
      actionIcon:
        "https://cdn.builder.io/api/v1/image/assets/TEMP/2fabc0add8911cc11be6dff450b5d3c21e4250c7?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba",
      isHighlighted: false,
    },
    {
      name: "Abebe Kebede",
      title: "Develop marketing Campaign",
      kpiWeight: 4,
      targetDate: "01 -Jan-2021 / 01 -Jan-2022",
      actionIcon:
        "https://cdn.builder.io/api/v1/image/assets/TEMP/254a47b580fb8389a9557bfd3f5c092c85367901?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba",
      isHighlighted: true,
    },
    {
      name: "Abebe Kebede",
      title: "Sign 3 new customers",
      kpiWeight: 5,
      targetDate: "01 -Jan-2021 / 01 -Jan-2022",
      actionIcon:
        "https://cdn.builder.io/api/v1/image/assets/TEMP/653e7b278cb81c6188d53a744bda9e7d178914a6?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba",
      isHighlighted: false,
    },
    {
      name: "Abebe Kebede",
      title: "Sign new customers",
      kpiWeight: 5,
      targetDate: "01 -Jan-2021 / 01 -Jan-2022",
      actionIcon:
        "https://cdn.builder.io/api/v1/image/assets/TEMP/653226fc758b01463d5653d1c71dcaafb5e5d79a?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba",
      isHighlighted: true,
    },
    {
      name: "Abebe Kebede",
      title: "Sign new customers",
      kpiWeight: 5,
      targetDate: "01 -Jan-2021 / 01 -Jan-2022",
      actionIcon:
        "https://cdn.builder.io/api/v1/image/assets/TEMP/c9af2f4d38fcbf35ede2c1f216dbaaa7d71541c0?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba",
      isHighlighted: false,
    },
    {
      name: "Abebe Kebede",
      title: "Sign new customers",
      kpiWeight: 5,
      targetDate: "01 -Jan-2021 / 01 -Jan-2022",
      actionIcon:
        "https://cdn.builder.io/api/v1/image/assets/TEMP/bbc73dac1e235a12ad88817f9c72650481b4df88?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba",
      isHighlighted: true,
    },
    {
      name: "Abebe Kebede",
      title: "Sign new customers",
      kpiWeight: 5,
      targetDate: "01 -Jan-2021 / 01 -Jan-2022",
      actionIcon:
        "https://cdn.builder.io/api/v1/image/assets/TEMP/69150e9883cbd8ce9565bd39776b7497de943376?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba",
      isHighlighted: false,
    },
  ];

  return (
    <section className="px-9 pt-10 pb-6 mt-9 w-full font-bold bg-white rounded-2xl max-md:px-5 max-md:max-w-full">
      <header className="flex flex-wrap gap-5 justify-between mr-3.5 ml-5 w-full max-w-[1189px] max-md:mr-2.5 max-md:max-w-full">
        <h2 className="self-start text-2xl text-black">Manage Targets</h2>
        <div className="flex gap-10 text-lg text-center text-white whitespace-nowrap">
          <img
            src="https://cdn.builder.io/api/v1/image/assets/TEMP/a09f4d23b4b310a0412eed15185762585443a093?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
            className="object-contain shrink-0 my-auto aspect-square w-[25px]"
            alt=""
          />
          <button className="flex gap-5 px-8 py-5 bg-lime-700 rounded-2xl shadow-[11px_4px_14px_rgba(0,0,0,0.12)] max-md:px-5">
            <span>Export</span>
            <img
              src="https://cdn.builder.io/api/v1/image/assets/TEMP/bdf363b31e69f76f9884b4a0789f32f084466c26?placeholderIfAbsent=true&apiKey=b77093444f03412d8ddf09ea95690fba"
              className="object-contain shrink-0 self-start mt-1 w-3.5 aspect-square"
              alt=""
            />
          </button>
        </div>
      </header>

      <div className="flex gap-10 items-start px-12 pt-7 pb-2.5 mt-7 text-xl text-black bg-sky-100 rounded-md max-md:px-5 max-md:max-w-full">
        <div>Name(s)</div>
        <div>Title(s)</div>
        <div className="grow shrink w-[93px]">KPI Weight</div>
        <div>Target Date</div>
        <div className="grow shrink w-[126px]">Actions</div>
      </div>

      {targets.map((target, index) => (
        <TargetRow
          key={index}
          name={target.name}
          title={target.title}
          kpiWeight={target.kpiWeight}
          targetDate={target.targetDate}
          actionIcon={target.actionIcon}
          isHighlighted={target.isHighlighted}
        />
      ))}
    </section>
  );
};
