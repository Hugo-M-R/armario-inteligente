package br.com.unit.tokseg.armario_inteligente.util;

import br.com.unit.tokseg.armario_inteligente.model.TipoUsuarioEnum;
import br.com.unit.tokseg.armario_inteligente.model.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Usuario getCurrentUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        return usuario;
    }

    public static boolean isAdmin(Usuario usuario) {
        return usuario.getTipo() == TipoUsuarioEnum.ADMIN;
    }

    public static boolean isPorteiro(Usuario usuario) {
        return usuario.getTipo() == TipoUsuarioEnum.PORTEIRO;
    }

    public static boolean isMorador(Usuario usuario) {
        return usuario.getTipo() == TipoUsuarioEnum.MORADOR;
    }

    public static boolean canManageAllEncomendas(Usuario usuario) {
        return isAdmin(usuario) || isPorteiro(usuario);
    }
}
