import React from "react";
import "../styles/HeaderRecruitment.css";
import imgCongTy from "../assets/imgs/imageCongTy.jpg";
import { Link } from "react-router-dom";

function HeaderRecruitment() {
  return (
    <>
      <header className="header-recruitment">
        <div className="brand-title-recruitment">ELTS VINA</div>
        <div className="menu-icon-recruitment">
          <div >
            <Link className="brand-title-recruitment" to="/jobs">Cơ hội nghề nghiệp</Link></div>
          <div>
            <Link className="brand-title-recruitment" to="/about-us">Giới thiệu</Link>
          </div>

        </div>
      </header>
      <section className="hero-section-recruitment">
        <img
          src={imgCongTy}
          alt="Công ty"
          className="hero-image-recruitment"
        />
        <div className="hero-overlay-recruitment"></div>
        <div className="hero-title-recruitment">Tuyển dụng</div>
      </section>
    </>
  );
}

export default HeaderRecruitment;
