package com.abdelrahman027.sbecom2.controller;


import com.abdelrahman027.sbecom2.model.AppRole;
import com.abdelrahman027.sbecom2.model.Role;
import com.abdelrahman027.sbecom2.model.User;
import com.abdelrahman027.sbecom2.repository.RoleRepository;
import com.abdelrahman027.sbecom2.repository.UserRepository;
import com.abdelrahman027.sbecom2.security.jwt.*;
import com.abdelrahman027.sbecom2.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.BAD_REQUEST);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        assert userDetails != null;
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        //modified for jwt cookie old was passing the jwt with
        LoginResponse response = LoginResponse.builder().roles(roles).jwtToken(jwtCookie.toString()).username(userDetails.getUsername()).id(userDetails.getId()).build();
        //modified as for cookie jwt
        //old was ResponseEntity.ok(response)
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,jwtCookie.toString()).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody RegisterRequest registerRequest) {
        boolean existsByUserName = userRepository.existsByUserName(registerRequest.getUsername());
        boolean existByEmail = userRepository.existsByEmail(registerRequest.getEmail());

        if (existsByUserName)
            return new ResponseEntity<>(new MessageResponse("username already exists"), HttpStatus.BAD_REQUEST);
        if (existByEmail)
            return new ResponseEntity<>(new MessageResponse("email already exists"), HttpStatus.BAD_REQUEST);

        User user = new User();

        user.setUserName(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("role not found"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(() -> new RuntimeException("role not found"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("user registered successfully"));
    }


    @GetMapping("/username")
    public String currentUsername(Authentication authentication) {
        if (authentication !=null) return authentication.getName();
        else return "NULL";
    }


    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        assert userDetails != null;

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        //modified for jwt cookie old was passing the jwt with
        LoginResponse response = LoginResponse.builder().roles(roles).username(userDetails.getUsername()).id(userDetails.getId()).build();
        //modified as for cookie jwt
        //old was ResponseEntity.ok(response)
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser() {
        ResponseCookie cookie = jwtUtils.cleanJwtCookie();
        return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(new MessageResponse("user has signed out"));
    }
}



