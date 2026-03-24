package io.luzonni.mindbudget.rest.resources.auth;

import io.luzonni.mindbudget.domain.model.user.User;
import io.luzonni.mindbudget.repository.user.UserRepository;
import io.luzonni.mindbudget.rest.dto.auth.AuthRequest;
import io.luzonni.mindbudget.util.JwtUtil;
import io.luzonni.mindbudget.util.PasswordUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.Map;
import java.util.Optional;

@Path("/auth")
public class AuthResource {
    
    private final UserRepository userRepository;

    @Inject
    public AuthResource(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @POST
    public Response login(AuthRequest loginRequest) {
        System.out.println(
                ConfigProvider.getConfig().getValue("smallrye.jwt.sign.key", String.class)
        );
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if(userOpt.isEmpty()) {
            return Response.serverError().status(Response.Status.UNAUTHORIZED).build();
        }

        User user = userOpt.get();

        boolean valid = PasswordUtil.verify(
                loginRequest.getPassword(),
                user.getPasswordHash()
        );

        if(!valid) {
            return Response.serverError().status(Response.Status.UNAUTHORIZED).build();
        }

        String token = JwtUtil.generateToken(user.getId(), user.getEmail());

        return Response.ok(Map.of(
                "access_token", token,
                "type", "Bearer"
        )).build();
    }

}
