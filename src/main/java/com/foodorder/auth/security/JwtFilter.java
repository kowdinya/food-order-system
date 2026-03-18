package com.foodorder.auth.security;

import com.foodorder.auth.model.User;
import com.foodorder.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //step1 : get Authentication header

        String authHeader = request.getHeader("Authorization");

        //step2 : if header is null or doesn't start with "Bearer " -> skip filter

        if(authHeader==null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return ;
        }
        //step 3 : extract token (remove "Bearer "  prefix -> 7 characters
String token = authHeader.substring(7);

        //step 4 : extract email

        String email = jwtUtil.extractEmail(token);//does this work i thought it will directly fetch email i.e return jwts.builder() from jwtutil class

        //step 5 : validate token

if(jwtUtil.isTokenValid(token)){

    //step6 : find user in db

    Optional<User> user = userRepository.findByEmail(email);

    if(user.isPresent()){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user.get(),//actual user object
                null,//no credentials needed
                user.get().getAuthorities() //roles//not working // to work this changed   Optional user  into Optional<User> user
        );
        //attach request details to the token
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        //tell spring security "this user is authenticated "
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    filterChain.doFilter(request, response);


    //this line must be **outside** the `if(isTokenValid)` block — even if token is invalid, the request must continue (SecurityContext just won't be set, and Spring Security will block it automatically).
//basic structure
//    So your complete structure should be:
//```
//    if(header null or no Bearer) → doFilter + return
//            extract token
//    extract email
//    if(token valid) {
//        find user
//        if(user present) → set SecurityContext
//    }
//    filterChain.doFilter(request, response)  ← always runs
}
    }
}
