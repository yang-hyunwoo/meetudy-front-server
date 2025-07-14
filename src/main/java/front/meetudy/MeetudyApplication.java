package front.meetudy;

import front.meetudy.property.FrontJwtProperty;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.TimeZone;

@SpringBootApplication
//@EnableConfigurationProperties(FrontJwtProperty.class)
public class MeetudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetudyApplication.class, args);
	}


	@PostConstruct
	public void setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
