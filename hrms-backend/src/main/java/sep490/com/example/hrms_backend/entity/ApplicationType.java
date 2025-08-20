package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "application_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationType {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_type_id")
    private Long id;

    @NotBlank
    @Column(name = "application_type_name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;



    @OneToMany(mappedBy = "applicationType")
    private List<Application> applications;
}
