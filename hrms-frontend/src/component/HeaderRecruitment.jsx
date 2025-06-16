import "./HeaderRecruitment.css";
import imgCongTy from "../assets/imgs/imageCongTy.jpg";

function HeaderRecruitment() {
  return (
    <>
      <header className="header-recruitment">
        <div className="brand-title-recruitment">ELTS VINA</div>
        <div className="menu-icon-recruitment">
          <svg
            width="50"
            height="50"
            viewBox="0 0 50 50"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M8.33334 37.5C7.74306 37.5 7.24792 37.3 6.84792 36.9C6.44792 36.5 6.24862 36.0056 6.25001 35.4167C6.25001 34.8264 6.45001 34.3313 6.85001 33.9313C7.25001 33.5313 7.74445 33.332 8.33334 33.3333H41.6667C42.257 33.3333 42.7521 33.5333 43.1521 33.9333C43.5521 34.3333 43.7514 34.8278 43.75 35.4167C43.75 36.007 43.55 36.5021 43.15 36.9021C42.75 37.3021 42.2556 37.5014 41.6667 37.5H8.33334ZM8.33334 27.0833C7.74306 27.0833 7.24792 26.8833 6.84792 26.4833C6.44792 26.0833 6.24862 25.5889 6.25001 25C6.25001 24.4097 6.45001 23.9146 6.85001 23.5146C7.25001 23.1146 7.74445 22.9153 8.33334 22.9167H41.6667C42.257 22.9167 42.7521 23.1167 43.1521 23.5167C43.5521 23.9167 43.7514 24.4111 43.75 25C43.75 25.5903 43.55 26.0854 43.15 26.4854C42.75 26.8854 42.2556 27.0847 41.6667 27.0833H8.33334ZM8.33334 16.6667C7.74306 16.6667 7.24792 16.4667 6.84792 16.0667C6.44792 15.6667 6.24862 15.1722 6.25001 14.5833C6.25001 13.9931 6.45001 13.4979 6.85001 13.0979C7.25001 12.6979 7.74445 12.4986 8.33334 12.5H41.6667C42.257 12.5 42.7521 12.7 43.1521 13.1C43.5521 13.5 43.7514 13.9945 43.75 14.5833C43.75 15.1736 43.55 15.6688 43.15 16.0688C42.75 16.4688 42.2556 16.6681 41.6667 16.6667H8.33334Z"
              fill="white"
            />
          </svg>
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
