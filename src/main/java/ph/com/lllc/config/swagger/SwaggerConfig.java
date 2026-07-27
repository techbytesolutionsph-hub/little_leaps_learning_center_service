package ph.com.lllc.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Little Leap Learning Center Service API")
                        .version("1.0")
                        .description("Backend for Little Leap Learning Center Service System")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("TechByte Solutions")
                                .email("dev.ecommerce.seo@techbytesolutions.com")));
    }
}