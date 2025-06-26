import "../styles/JobDetail.css";
import HeaderRecruitment from "../components/HeaderRecruitment";
import FooterRecruitment from "../components/FooterRecruitment";
import Breadcrumb from "../components/Breadcrumb";

const AboutUs = () => {

  return (
    <div className="jobDetail-page">
      <HeaderRecruitment />

      <Breadcrumb
        paths={[
          { name: "Danh sách công việc", url: "/jobs" },
          { name: "Giới thiệu" },
        ]}
      />

      <main className="jobDetail-container">
        

        <section className="jobDetail-content">
          <h3>Thông tin ứng tuyển</h3>


        </section>
      </main>
      <FooterRecruitment />
    </div>
  );
};

export default AboutUs;
