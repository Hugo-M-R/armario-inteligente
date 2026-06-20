package br.com.unit.tokseg.armario_inteligente;

import br.com.unit.tokseg.armario_inteligente.config.JwtProperties;
import br.com.unit.tokseg.armario_inteligente.config.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, SecurityProperties.class})
public class ArmarioInteligenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArmarioInteligenteApplication.class, args);
	}

}
