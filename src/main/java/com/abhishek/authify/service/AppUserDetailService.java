package com.abhishek.authify.service;

import com.abhishek.authify.Entity.UserEntity;
import com.abhishek.authify.Reposistory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
//Spring Security sees this request and attempts to authenticate
// the user using a UsernamePasswordAuthenticationFilter
// (part of default Spring Security filter chain).
// This filter internally calls your AppUserDetailService.loadUserByUsername(email) to:
//Find the user in the DB (via UserRepository)
public class AppUserDetailService implements UserDetailsService {
    private final UserRepository userReposistory;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("[UserDetailsService] Loading user by email: " + email);
     UserEntity entity= userReposistory.findByEmail(email).orElseThrow(()->

             new UsernameNotFoundException("not email found"+ email));

     return new User(entity.getEmail(),entity.getPassword(),new ArrayList<>());

    }
}
