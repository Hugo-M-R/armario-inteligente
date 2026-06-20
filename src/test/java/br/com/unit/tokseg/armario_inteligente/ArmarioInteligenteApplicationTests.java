package br.com.unit.tokseg.armario_inteligente;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import br.com.unit.tokseg.armario_inteligente.config.TestSecurityConfig;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(TestSecurityConfig.class)
class ArmarioInteligenteApplicationTests {

	@Test
	void contextLoads() {
	}

}
