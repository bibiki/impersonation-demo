package com.gagi.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MyUserDetailsService implements UserDetailsService {

	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("loading user details by user name", username);
		if(username.equals("auser")) {
			return User.withDefaultPasswordEncoder()
						.username("auser")
						.password("password")
						.roles("ADMIN")
						.build();
		}
		else if (username.equals("buser")) {
			return User.withDefaultPasswordEncoder()
						.username("buser")
						.password("password")
						.roles("USER")
						.build();
		}
		else if (username.equals("superuser")) {
			return User.withDefaultPasswordEncoder()
						.username("superuser")
						.password("password")
						.roles("SUPER_USER")
						.build();
		}
		else if (username.equals("keycloak@keycloak.com")) {
			return User.withDefaultPasswordEncoder()
						.username("keycloak@keycloak.com")
						.password("password")
						.roles("SUPER_USER")
						.build();
		}
		else if (username.equals("gagi@gagi.com")) {
			return User.withDefaultPasswordEncoder()
						.username("gagi@gagi.com")
						.password("password")
						.roles("SUPER_USER")
						.build();
		}
		throw new UsernameNotFoundException(username);
	}
}
