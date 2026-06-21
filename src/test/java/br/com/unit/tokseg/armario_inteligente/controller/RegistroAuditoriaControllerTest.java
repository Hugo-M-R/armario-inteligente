package br.com.unit.tokseg.armario_inteligente.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class RegistroAuditoriaControllerTest {

    @Test
    void auditoriaNaoPossuiEndpointsDeEscrita() {
        boolean possuiPost = Arrays.stream(RegistroAuditoriaController.class.getDeclaredMethods())
                .anyMatch(method -> method.isAnnotationPresent(PostMapping.class));
        boolean possuiDelete = Arrays.stream(RegistroAuditoriaController.class.getDeclaredMethods())
                .anyMatch(method -> method.isAnnotationPresent(DeleteMapping.class));

        assertThat(possuiPost).isFalse();
        assertThat(possuiDelete).isFalse();
    }

    @Test
    void auditoriaPossuiEndpointsDeLeitura() {
        boolean possuiGet = Arrays.stream(RegistroAuditoriaController.class.getDeclaredMethods())
                .anyMatch(method -> Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals("GetMapping")));

        assertThat(possuiGet).isTrue();
    }
}
