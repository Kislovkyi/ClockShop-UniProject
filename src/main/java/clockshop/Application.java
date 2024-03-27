package clockshop;

import org.salespointframework.EnableSalespoint;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.web.SecurityFilterChain;

@EnableScheduling
@EnableSalespoint
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Configuration
	static class WebSecurityConfiguration {

		@Bean
		SecurityFilterChain clockShopSecurity(HttpSecurity http) throws Exception {

			return http
				.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(login -> login.loginProcessingUrl("/login").defaultSuccessUrl("/catalog"))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/"))
				.build();
		}
	}
}