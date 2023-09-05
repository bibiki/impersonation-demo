package com.gagi.configuration;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class ConfigureKeycloakSpringSecurity {

	@Autowired
	UserDetailsService detailsService;
	
	
	
	
	@Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		log.info("configuring oauth spring security");
		http.addFilterAfter(new Filter() {
			
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if(authentication instanceof OAuth2AuthenticationToken) {
					//reason d'etre of this filter is this:
					//controller methods take an Authentication parameter. One can use specific types like OAuth2User but when users of other type
					//login, then that parameter goes as null. for example if that parameters is actually a UserDetails type
					//therefore, I have this to unify the two types to make sure my Keycloak authenticated users come as userDetails types in my controller
					OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
					UserDetails user = detailsService.loadUserByUsername(token.getPrincipal().getAttribute("email"));
					SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, http));
				}
				chain.doFilter(request, response);
			}
		}, OAuth2AuthorizationCodeGrantFilter.class);
        http
                .oauth2Client()
                    .and()
                .oauth2Login()
                .tokenEndpoint()
                    .and()
                .userInfoEndpoint();
        http
                .authorizeHttpRequests()
                            .requestMatchers(antMatcher("/oauth2/**"), antMatcher("/login/**")).permitAll()
                            .requestMatchers(antMatcher("/admin")).hasAnyRole("ADMIN", "SUPER_USER")
                            .requestMatchers(antMatcher("/user")).hasAnyRole("USER", "SUPER_USER")
                            .anyRequest().fullyAuthenticated()
                .and()
                    .logout()
                    .logoutSuccessUrl("http://localhost:8080/realms/basic-realm/protocol/openid-connect/logout");

        return http.build();
    }
	
	@Bean
	public SwitchUserFilter switchUserFilter() {
		log.info("switch user filter being created");
		SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(detailsService);
        filter.setSwitchUserMatcher(antMatcher("/impersonate"));
        filter.setSwitchFailureUrl("/switchUser");
        filter.setTargetUrl("/");
        return filter;
	}
}
