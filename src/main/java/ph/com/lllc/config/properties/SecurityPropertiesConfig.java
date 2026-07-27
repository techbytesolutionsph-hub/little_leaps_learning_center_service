package ph.com.lllc.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "security.permit.paths.lllc")
public class SecurityPropertiesConfig {

    private String[] swaggerPath;
    private String[] staticPath;
    private String[] internalPath;
    private String[] defaultPath;

    private String[] clientPath;
    private String clientLogin;
    private String clientLogout;
    private String clientDashboard;

    private String[] staffPath;
    private String staffLogin;
    private String staffLogout;
    private String staffDashboard;

    private String errorPagePath;
}
