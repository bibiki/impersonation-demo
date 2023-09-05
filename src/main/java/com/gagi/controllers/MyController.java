package com.gagi.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MyController {

	@GetMapping(value = {"/"})
	@ResponseBody
	public String controller(@AuthenticationPrincipal UserDetails user) {
		log.info("hello");
		return "hello " + user.getUsername();
	}
	
	@GetMapping("/admin")
	@ResponseBody
	@PreAuthorize("hasRole('ADMIN')")
	public String requiresAdmin(@AuthenticationPrincipal UserDetails user) {
		log.info("admin");
		return "admin";
	}
	
	@GetMapping("/user")
	@ResponseBody
	@PreAuthorize("hasRole('USER')")
	public String requiresUser(@AuthenticationPrincipal UserDetails user) {
		log.info("user");
		return "user";
	}
	
}
