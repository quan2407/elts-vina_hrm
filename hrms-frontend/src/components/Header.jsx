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
                  <rect width="32" height="32" fill="white" />
                </clipPath>
              </defs>
            </svg>
          </div>
        </div>
        <div className="header-actions">
          <div className="action-button notification-button">
            <svg
              className="header-icon-bg"
              width="73"
              height="73"
              viewBox="0 0 73 73"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g filter="url(#filter0_d_2_1989)">
                <circle cx="35.5" cy="32.5" r="26.5" fill="#253D90" />
              </g>
              <defs>
                <filter
                  id="filter0_d_2_1989"
                  x="0"
                  y="0"
                  width="73"
                  height="73"
                  filterUnits="userSpaceOnUse"
                  colorInterpolationFilters="sRGB"
                >
                  <feFlood floodOpacity="0" result="BackgroundImageFix" />
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
                    result="effect1_dropShadow_2_1989"
                  />
                  <feOffset dx="1" dy="4" />
                  <feGaussianBlur stdDeviation="4.5" />
                  <feComposite in2="hardAlpha" operator="out" />
                  <feColorMatrix
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"
                  />
                  <feBlend
                    mode="normal"
                    in2="BackgroundImageFix"
                    result="effect1_dropShadow_2_1989"
                  />
                  <feBlend
                    mode="normal"
                    in="SourceGraphic"
                    in2="effect1_dropShadow_2_1989"
                    result="shape"
                  />
                </filter>
              </defs>
            </svg>
            <svg
              className="header-icon"
              width="28"
              height="28"
              viewBox="0 0 28 28"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M14 28C15.9316 28 17.4984 26.4332 17.4984 24.5H10.5017C10.5017 26.4332 12.0685 28 14 28ZM25.7792 19.8127C24.7226 18.6774 22.7457 16.9695 22.7457 11.375C22.7457 7.12578 19.7663 3.72422 15.7489 2.88969V1.75C15.7489 0.783672 14.9658 0 14 0C13.0343 0 12.2511 0.783672 12.2511 1.75V2.88969C8.23379 3.72422 5.25441 7.12578 5.25441 11.375C5.25441 16.9695 3.27746 18.6774 2.2209 19.8127C1.89277 20.1655 1.7473 20.5871 1.75004 21C1.75605 21.8969 2.45988 22.75 3.50551 22.75H24.4946C25.5402 22.75 26.2446 21.8969 26.25 21C26.2528 20.5871 26.1073 20.1649 25.7792 19.8127Z"
                fill="white"
              />
            </svg>
            <div className="notification-badge">
              <svg
                width="31"
                height="31"
                viewBox="0 0 31 31"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <g filter="url(#filter0_d_2_1993)">
                  <circle cx="16.5" cy="13.5" r="10.5" fill="#FF0000" />
                  <circle cx="16.5" cy="13.5" r="10" stroke="white" />
                </g>
                <defs>
                  <filter
                    id="filter0_d_2_1993"
                    x="0"
                    y="0"
                    width="31"
                    height="31"
                    filterUnits="userSpaceOnUse"
                    colorInterpolationFilters="sRGB"
                  >
                    <feFlood floodOpacity="0" result="BackgroundImageFix" />
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
                      result="effect1_dropShadow_2_1993"
                    />
                    <feOffset dx="-1" dy="2" />
                    <feGaussianBlur stdDeviation="2" />
                    <feComposite in2="hardAlpha" operator="out" />
                    <feColorMatrix
                      type="matrix"
                      values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.25 0"
                    />
                    <feBlend
                      mode="normal"
                      in2="BackgroundImageFix"
                      result="effect1_dropShadow_2_1993"
                    />
                    <feBlend
                      mode="normal"
                      in="SourceGraphic"
                      in2="effect1_dropShadow_2_1993"
                      result="shape"
                    />
                  </filter>
                </defs>
              </svg>
              <div className="notification-count">13</div>
            </div>
          </div>

          <div className="action-button tools-button">
            <svg
              className="header-icon-bg"
              width="73"
              height="73"
              viewBox="0 0 73 73"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g filter="url(#filter0_d_2_1982)">
                <circle cx="35.5" cy="32.5" r="26.5" fill="#FFC20E" />
              </g>
              <defs>
                <filter
                  id="filter0_d_2_1982"
                  x="0"
                  y="0"
                  width="73"
                  height="73"
                  filterUnits="userSpaceOnUse"
                  colorInterpolationFilters="sRGB"
                >
                  <feFlood floodOpacity="0" result="BackgroundImageFix" />
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
                    result="effect1_dropShadow_2_1982"
                  />
                  <feOffset dx="1" dy="4" />
                  <feGaussianBlur stdDeviation="4.5" />
                  <feComposite in2="hardAlpha" operator="out" />
                  <feColorMatrix
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0.1 0"
                  />
                  <feBlend
                    mode="normal"
                    in2="BackgroundImageFix"
                    result="effect1_dropShadow_2_1982"
                  />
                  <feBlend
                    mode="normal"
                    in="SourceGraphic"
                    in2="effect1_dropShadow_2_1982"
                    result="shape"
                  />
                </filter>
              </defs>
            </svg>
            <svg
              className="header-icon"
              width="31"
              height="31"
              viewBox="0 0 31 31"
              fill="none"
              xmlns="http://www.w3.org/2000/svg"
            >
              <g clipPath="url(#clip0_2_1983)">
                <path
                  d="M29.8591 7.12133C29.7313 6.60627 29.0868 6.43173 28.7115 6.80705L24.47 11.0485L20.5982 10.4034L19.9531 6.53155L24.1945 2.29011C24.5721 1.91251 24.3902 1.26911 23.8717 1.1402C21.1692 0.470559 18.1934 1.1921 16.0813 3.3037C13.8202 5.56473 13.1979 8.84221 14.1391 11.6999L1.9675 23.8715C0.542085 25.2969 0.542085 27.6081 1.9675 29.0335C3.3929 30.4589 5.70413 30.4589 7.12954 29.0335L19.2909 16.8722C22.1497 17.8253 25.4209 17.1962 27.6967 14.9203C29.8112 12.8059 30.5316 9.82556 29.8591 7.12133ZM4.54909 27.8209C3.79332 27.8209 3.18015 27.2077 3.18015 26.4519C3.18015 25.6956 3.79332 25.083 4.54909 25.083C5.30486 25.083 5.91803 25.6956 5.91803 26.4519C5.91803 27.2077 5.30486 27.8209 4.54909 27.8209Z"
                  fill="white"
                />
              </g>
              <defs>
                <clipPath id="clip0_2_1983">
                  <rect
                    width="29.2041"
                    height="29.2041"
                    fill="white"
                    transform="translate(0.898438 0.898438)"
                  />
                </clipPath>
              </defs>
            </svg>
          </div>

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
                  <circle cx="35.5" cy="32.5" r="26.5" fill="#3F861E" />
                </g>
                <path
                  d="M51.3937 28.925C51.6375 28.7313 52 28.9125 52 29.2188V42C52 43.6562 50.6562 45 49 45H23C21.3438 45 20 43.6562 20 42V29.225C20 28.9125 20.3562 28.7375 20.6062 28.9312C22.0062 30.0187 23.8625 31.4 30.2375 36.0312C31.5563 36.9937 33.7812 39.0188 36 39.0063C38.2313 39.025 40.5 36.9563 41.7687 36.0312C48.1437 31.4 49.9937 30.0125 51.3937 28.925ZM36 37C37.45 37.025 39.5375 35.175 40.5875 34.4125C48.8812 28.3938 49.5125 27.8687 51.425 26.3687C51.7875 26.0875 52 25.65 52 25.1875V24C52 22.3438 50.6562 21 49 21H23C21.3438 21 20 22.3438 20 24V25.1875C20 25.65 20.2125 26.0812 20.575 26.3687C22.4875 27.8625 23.1188 28.3938 31.4125 34.4125C32.4625 35.175 34.55 37.025 36 37Z"
                  fill="white"
                />
              </g>
              <g filter="url(#filter2_d_2_1995)">
                <circle cx="57.5" cy="18.5" r="10.5" fill="#FF0000" />
                <circle cx="57.5" cy="18.5" r="10" stroke="white" />
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
                <tspan x="52" y="23.3885">
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
                  <feFlood floodOpacity="0" result="BackgroundImageFix" />
                  <feColorMatrix
                    in="SourceAlpha"
                    type="matrix"
                    values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0"
                    result="hardAlpha"
                  />
                  <feOffset dy="4" />
                  <feGaussianBlur stdDeviation="2" />
                  <feComposite in2="hardAlpha" operator="out" />
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
                  <feFlood floodOpacity="0" result="BackgroundImageFix" />
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
                  <feOffset dx="1" dy="4" />
                  <feGaussianBlur stdDeviation="4.5" />
                  <feComposite in2="hardAlpha" operator="out" />
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
                  <feFlood floodOpacity="0" result="BackgroundImageFix" />
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
                  <feOffset dx="-1" dy="2" />
                  <feGaussianBlur stdDeviation="2" />
                  <feComposite in2="hardAlpha" operator="out" />
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
