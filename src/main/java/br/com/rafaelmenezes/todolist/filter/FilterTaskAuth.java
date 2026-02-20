package br.com.rafaelmenezes.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.rafaelmenezes.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        // Usando .startsWith para garantir que sub-rotas de /tasks também sejam protegidas
        if (servletPath.startsWith("/tasks")) {

            var authorization = request.getHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Basic")) {
                response.sendError(401, "Autorização ausente ou inválida");
                return;
            }

            try {
                var authEncoded = authorization.substring("Basic".length()).trim();
                byte[] authDecode = Base64.getDecoder().decode(authEncoded);
                var authString = new String(authDecode);

                String[] credentials = authString.split(":");
                String username = credentials[0];
                String password = credentials[1];

                var user = this.userRepository.findByUsername(username);

                if (user == null) {
                    response.sendError(401, "Usuário não encontrado");
                    return;
                }

                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                if (passwordVerify.verified) {
                    // Se estiver tudo OK, passa o usuário para o Request para usar no Controller
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401, "Senha incorreta");
                    return; // INTERROMPE AQUI
                }

            } catch (Exception e) {
                response.sendError(400, "Erro ao processar autenticação");
                return;
            }

        } else {
            // Se não for rota de /tasks, segue o fluxo normal
            filterChain.doFilter(request, response);
        }
    }
}
