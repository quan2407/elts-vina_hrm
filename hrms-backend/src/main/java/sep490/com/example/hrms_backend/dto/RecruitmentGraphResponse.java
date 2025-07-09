package sep490.com.example.hrms_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentGraphResponse {
    private Long RecuitementId;
    private String RecruitmentTitle;
    private int canTuyen;
    private Long daTuyen;
    private Long ungTuyen;


}
