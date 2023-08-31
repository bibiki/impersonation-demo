package com.gagi.controllers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

	@GetMapping("/hello")
	@ResponseBody
	public String controller(@AuthenticationPrincipal User user) {
		for(GrantedAuthority ga : user.getAuthorities()) {
			System.out.println(ga.getAuthority());
		}
		return user.getUsername();
	}
	
}
