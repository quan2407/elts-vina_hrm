package sep490.com.example.hrms_backend;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "HRMS - Human Resource Management System",
				description = "RESTful API documentation for the internal HRMS system.",
				version = "v1.0",
				contact = @Contact(
						name = "Centrix HRMS Team",
						email = "support@centrix.asia",
						url = "https://centrix.asia"
				),
				license = @License(
						name = "Private Internal License",
						url = "https://centrix.asia/license"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "GitHub Repo & Dev Resources",
				url = "https://github.com/centrix/hrms-backend"
		)
)
public class HrmsBackendApplication {


	public static void main(String[] args) {
		SpringApplication.run(HrmsBackendApplication.class, args);
	}

}
