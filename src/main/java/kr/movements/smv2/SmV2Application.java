package kr.movements.smv2;

import kr.movements.smv2.entity.FileEntity;
import kr.movements.smv2.repository.FileRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaAuditing
@SpringBootApplication
public class SmV2Application {

	public static void main(String[] args) {
		SpringApplication.run(SmV2Application.class, args);
	}

}
