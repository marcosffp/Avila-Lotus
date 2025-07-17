package avila.lotus.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableJpaRepositories(basePackages = "avila.lotus.back.repositorio")
@SpringBootApplication
@EnableAsync
public class BackApplication {

	public static void main(String[] args) {
		// Configurações do banco de dados
		System.setProperty("DATABASE_HOST", System.getenv("DATABASE_HOST"));
		System.setProperty("DATABASE_PORT", System.getenv("DATABASE_PORT"));
		System.setProperty("DATABASE_NAME", System.getenv("DATABASE_NAME"));
		System.setProperty("DATABASE_USERNAME", System.getenv("DATABASE_USERNAME"));
		System.setProperty("DATABASE_PASSWORD", System.getenv("DATABASE_PASSWORD"));

		// Configurações de e-mail
		System.setProperty("MAIL_NO_REPLY", System.getenv("MAIL_NO_REPLY"));
		System.setProperty("PASSWORD_NO_REPLY", System.getenv("PASSWORD_NO_REPLY"));
		System.setProperty("MAIL_AGENDAMENTOS", System.getenv("MAIL_AGENDAMENTOS"));
		System.setProperty("PASSWORD_AGENDAMENTOS", System.getenv("PASSWORD_AGENDAMENTOS"));
		System.setProperty("MAIL_CONTATO", System.getenv("MAIL_CONTATO"));
		System.setProperty("PASSWORD_CONTATO", System.getenv("PASSWORD_CONTATO"));

		// Outras configurações
		System.setProperty("JWT_SECRET", System.getenv("JWT_SECRET"));
		System.setProperty("COD_AUTENTICACAO", System.getenv("COD_AUTENTICACAO"));

		SpringApplication.run(BackApplication.class, args);
	}
}