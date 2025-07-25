import "../styles/JobDetail.css";
import HeaderRecruitment from "../components/HeaderRecruitment";
import FooterRecruitment from "../components/FooterRecruitment";
import Breadcrumb from "../components/Breadcrumb";
import imgsanxuat from "../assets/imgs/image_sanxuat.jpg";

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
          <h2 style={{ textAlign: "center", textTransform: "uppercase", marginBottom: "50px" }}>Về ELTS VINA</h2>

          <div style={{ display: "flex", flexWrap: "wrap", gap: "20px", alignItems: "flex-start" }}>

            <p style={{ flex: 1, minWidth: "300px", textAlign: "justify", marginBottom: "40px" }}>

              <b>Địa điểm:</b> Lô P, Khu công nghiệp Bình Xuyên, Thị Trấn Hương Canh, Huyện Bình Xuyên, Tỉnh Vĩnh Phúc, Việt Nam
              <br />
              <br />
              <b>Số điện thoại:</b> 02113 866 858
              <br />
              <br />
              <b>Người đại diện:</b> Ông Lee Chunsik
              <br />
              <br />
              <b> Chức vụ:</b> Giám đốc
            </p>
            <div style={{ display: "flex", justifyContent: "center", marginBottom: "60px" }}>
              <iframe
                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d4488.020968208626!2d105.66112781146822!3d21.26232327963281!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3134fbfeea3f464b%3A0xf9ab028c4c66a711!2sC%C3%94NG%20TY%20TNHH%20EL%20TS%20VINA!5e1!3m2!1svi!2s!4v1751245448703!5m2!1svi!2s"
                width="450"
                height="300"
                style={{ border: 0 }}
                allowFullScreen=""
                loading="lazy"
                referrerPolicy="no-referrer-when-downgrade"
                title="ELTS VINA Map"
              ></iframe>
            </div>
          </div>
          <h2 style={{ textAlign: "center", textTransform: "uppercase", marginBottom: "50px" }}>Phạm vi, quy mô, loại hình</h2>
          <div style={{ display: "flex", flexWrap: "wrap", gap: "20px", alignItems: "flex-start" }}>
            <div style={{ flex: 1, minWidth: "300px", marginRight: "20px" }}>

              <img
                src={imgsanxuat}
                alt="Môi trường làm việc Vietcombank"
                style={{ width: "100%", borderRadius: "8px", boxShadow: "0 4px 8px rgba(0,0,0,0.1)" }}
              />

            </div>
            <div style={{ flex: 1, minWidth: "300px" }}>

              <p>
                Hiện tại, <b>ELTS VINA</b> là một trong những công ty hàng đầu trong lĩnh vực:
                <br />
                <br />
                -  Sản xuất, gia công các bộ phận của bảng mạch in điện tử (FPCB PART) dùng cho điện thoại di động và các thiết bị điện tử khác.
                <br />
                <br />
                - Sản xuất, gia công, ép: tấm khả năng chịu lực của bảng mạch FPCB (Stiffening plate of FPCB) dùng cho điện thoại di động và các thiết bị điện tử khác từ thép không gỉ, hợp kim niken - bạc,...
              </p>

            </div>
            <p>                - Sản xuất, gia công: các loại màng dẫn điện, băng dính, màng PE tự dính, tấm liên kết có chất kết dính ở nhiệt độ cao dùng trong sản xuất linh kiện điện tử
            </p>
          </div>

        </section>
      </main>
      <FooterRecruitment />
    </div>
  );
};

export default AboutUs;
