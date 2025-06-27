// components/Breadcrumb.js
import React from "react";
import { Link } from "react-router-dom";
import "../styles/Breadcrumb.css";

const Breadcrumb = ({ paths }) => {
  return (
    <nav className="breadcrumb">
      <ul className="breadcrumb-list">
        {paths.map((path, index) => (
          <li key={index} className="breadcrumb-item">
            {path.url ? (
              <Link to={path.url} className="breadcrumb-link">
                {path.name}
              </Link>
            ) : (
              <span className="breadcrumb-text">{path.name}</span>
            )}
          </li>
        ))}
      </ul>
    </nav>
  );
};

export default Breadcrumb;
