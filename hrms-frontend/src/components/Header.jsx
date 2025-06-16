import React from "react";
import "../assets/styles/Header.css";

function Header() {
  return (
    <div className="header">
      <div className="breadcrumb-title">Dashboard / Employee Management</div>
      <div className="header-controls">
        <div className="search-container">
          <div className="search-filter">
            <div className="filter-text">All Candidates</div>
            <svg
              className="dropdown-icon"
              width="18"
              height="18"
              viewBox="0 0 18 18"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M4.81676 10.1248H13.184C13.9363 10.1248 14.3125 11.0354 13.7816 11.5662L9.59801 15.7498C9.26754 16.0803 8.73317 16.0803 8.40622 15.7498L4.21911 11.5662C3.68825 11.0354 4.06442 10.1248 4.81676 10.1248ZM13.7816 6.4334L9.59801 2.2498C9.26754 1.91934 8.73317 1.91934 8.40622 2.2498L4.21911 6.4334C3.68825 6.96426 4.06442 7.87481 4.81676 7.87481H13.184C13.9363 7.87481 14.3125 6.96426 13.7816 6.4334Z"
                fill="white"
              />
            </svg>
          </div>
          <div className="search-input">
            <div className="search-placeholder">Search...</div>
            <svg
              className="search-icon"
              width="32"
              height="32"
              viewBox="0 0 32 32"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g clipPath="url(#clip0_2_2004)">
                <path
                  d="M31.5625 27.6688L25.3312 21.4375C25.05 21.1562 24.6687 21 24.2687 21H23.25C24.975 18.7938 26 16.0187 26 13C26 5.81875 20.1812 0 13 0C5.81875 0 0 5.81875 0 13C0 20.1812 5.81875 26 13 26C16.0187 26 18.7937 24.975 21 23.25V24.2687C21 24.6687 21.1562 25.05 21.4375 25.3312L27.6687 31.5625C28.2562 32.15 29.2062 32.15 29.7875 31.5625L31.5562 29.7938C32.1437 29.2063 32.1437 28.2563 31.5625 27.6688ZM13 21C8.58125 21 5 17.425 5 13C5 8.58125 8.575 5 13 5C17.4187 5 21 8.575 21 13C21 17.4188 17.425 21 13 21Z"
                  fill="#C4C4C4"
                />
              </g>
              <defs>
                <clipPath id="clip0_2_2004">
                  <rect
                    width="32"
                    height="32"
                    fill="white"
                  />
                </clipPath>
              </defs>
            </svg>
          </div>
        </div>
        <div className="header-actions">
          <div className="action-button email-button">
            <svg
              className="header-icon-bg"
              width="73"
              height="73"
              viewBox="0 0 73 73"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g filter="url(#filter0_d_2_1995)">
                <g filter="url(#filter1_d_2_1995)">
                  <circle
                    cx="35.5"
                    cy="32.5"
                    r="26.5"
                    fill="#253D90"
                  />
                </g>
                <g transform="translate(22, 22) scale(1.0)">
                  <path
                    d="M14 28C15.9316 28 17.4984 26.4332 17.4984 24.5H10.5017C10.5017 26.4332 12.0685 28 14 28ZM25.7792 19.8127C24.7226 18.6774 22.7457 16.9695 22.7457 11.375C22.7457 7.12578 19.7663 3.72422 15.7489 2.88969V1.75C15.7489 0.783672 14.9658 0 14 0C13.0343 0 12.2511 0.783672 12.2511 1.75V2.88969C8.23379 3.72422 5.25441 7.12578 5.25441 11.375C5.25441 16.9695 3.27746 18.6774 2.2209 19.8127C1.89277 20.1655 1.7473 20.5871 1.75004 21C1.75605 21.8969 2.45988 22.75 3.50551 22.75H24.4946C25.5402 22.75 26.2446 21.8969 26.25 21C26.2528 20.5871 26.1073 20.1649 25.7792 19.8127Z"
                    fill="white"
                  />
                </g>
              </g>
              <g filter="url(#filter2_d_2_1995)">
                <circle
                  cx="57.5"
                  cy="18.5"
                  r="10.5"
                  fill="#FF0000"
                />
                <circle
                  cx="57.5"
                  cy="18.5"
                  r="10"
                  stroke="white"
                />
              </g>
              <text
                fill="white"
                xmlSpace="preserve"
                style={{ whiteSpace: "pre" }}
                fontFamily="Product Sans"
                fontSize="11"
                fontWeight="bold"
                letterSpacing="0em"
              >
                <tspan
                  x="52"
                  y="23.3885"
                >
                  13
                </tspan>
              </text>
              <defs>
                <filter
                  id="filter0_d_2_1995"
                  x="5"
                  y="6"
                  width="61"
                  height="61"
                  filterUnits="userSpaceOnUse"
                  colorInterpolationFilters="sRGB"
                >
                  <feFlood
                    floodOpacity="0"
                    result="BackgroundImageFix"
                  />
                  <feColorMatrix
                    in="SourceAlpha"
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"
                    result="hardAlpha"
                  />
                  <feOffset dy="4" />
                  <feGaussianBlur stdDeviation="2" />
                  <feComposite
                    in2="hardAlpha"
                    operator="out"
                  />
                  <feColorMatrix
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"
                  />
                  <feBlend
                    mode="normal"
                    in2="BackgroundImageFix"
                    result="effect1_dropShadow_2_1995"
                  />
                  <feBlend
                    mode="normal"
                    in="SourceGraphic"
                    in2="effect1_dropShadow_2_1995"
                    result="shape"
                  />
                </filter>
                <filter
                  id="filter1_d_2_1995"
                  x="0"
                  y="0"
                  width="73"
                  height="73"
                  filterUnits="userSpaceOnUse"
                  colorInterpolationFilters="sRGB"
                >
                  <feFlood
                    floodOpacity="0"
                    result="BackgroundImageFix"
                  />
                  <feColorMatrix
                    in="SourceAlpha"
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"
                    result="hardAlpha"
                  />
                  <feMorphology
                    radius="1"
                    operator="dilate"
                    in="SourceAlpha"
                    result="effect1_dropShadow_2_1995"
                  />
                  <feOffset
                    dx="1"
                    dy="4"
                  />
                  <feGaussianBlur stdDeviation="4.5" />
                  <feComposite
                    in2="hardAlpha"
                    operator="out"
                  />
                  <feColorMatrix
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"
                  />
                  <feBlend
                    mode="normal"
                    in2="BackgroundImageFix"
                    result="effect1_dropShadow_2_1995"
                  />
                  <feBlend
                    mode="normal"
                    in="SourceGraphic"
                    in2="effect1_dropShadow_2_1995"
                    result="shape"
                  />
                </filter>
                <filter
                  id="filter2_d_2_1995"
                  x="41"
                  y="5"
                  width="31"
                  height="31"
                  filterUnits="userSpaceOnUse"
                  colorInterpolationFilters="sRGB"
                >
                  <feFlood
                    floodOpacity="0"
                    result="BackgroundImageFix"
                  />
                  <feColorMatrix
                    in="SourceAlpha"
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"
                    result="hardAlpha"
                  />
                  <feMorphology
                    radius="1"
                    operator="dilate"
                    in="SourceAlpha"
                    result="effect1_dropShadow_2_1995"
                  />
                  <feOffset
                    dx="-1"
                    dy="2"
                  />
                  <feGaussianBlur stdDeviation="2" />
                  <feComposite
                    in2="hardAlpha"
                    operator="out"
                  />
                  <feColorMatrix
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"
                  />
                  <feBlend
                    mode="normal"
                    in2="BackgroundImageFix"
                    result="effect1_dropShadow_2_1995"
                  />
                  <feBlend
                    mode="normal"
                    in="SourceGraphic"
                    in2="effect1_dropShadow_2_1995"
                    result="shape"
                  />
                </filter>
              </defs>
            </svg>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Header;
