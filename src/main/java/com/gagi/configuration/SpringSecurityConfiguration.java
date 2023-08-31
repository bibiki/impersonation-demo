package com.gagi.configuration;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

	@Bean
	public SwitchUserFilter switchUserFilter() {
		log.info("switch user filter being created");
		SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(userDetailsService());
        filter.setSwitchUserMatcher(antMatcher("/impersonate"));
//        filter.setSwitchUserUrl("/impersonate");//this does not work, the line above does. with this line, /impersonate endpoint will return a 403 http status
        filter.setSwitchFailureUrl("/switchUser");
        filter.setTargetUrl("/hello");
        return filter;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers(antMatcher("/"), antMatcher("/hello")).permitAll()
				.requestMatchers(antMatcher("/switching")).hasAnyRole("ADMIN", "PREVIOUS_ADMINISTRATOR")
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout((logout) -> logout.permitAll());

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails oneUser =
			 User.withDefaultPasswordEncoder()
				.username("gagi")
				.password("password")
				.roles("ADMIN")
				.build();
		UserDetails otherUser =
				 User.withDefaultPasswordEncoder()
					.username("ngadhnjim")
					.password("password")
					.roles("USER")
					.build();

		return new InMemoryUserDetailsManager(oneUser, otherUser);
	}
}
