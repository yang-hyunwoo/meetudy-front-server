package front.meetudy.security.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvironmentProvider {

    private final Environment env;
    private static Environment staticEnv;

    @PostConstruct
    public void init() {
        staticEnv = env;
    }

    public static boolean isProd() {
        return staticEnv != null && staticEnv.acceptsProfiles(Profiles.of("prod"));
    }
}