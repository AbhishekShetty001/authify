package com.abhishek.authify.Controller;

import com.abhishek.authify.io.AuthRequest;
import com.abhishek.authify.io.AuthResponse;
import com.abhishek.authify.service.AppUserDetailService;
import com.abhishek.authify.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailService appUserDetailService;
    private final JwtUtil jwtUtil;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        try{
            System.out.println("[LOGIN] Attempting login for: " + request.getEmail());
            // Authenticate using email and password
            authenticate(request.getEmail(),request.getPassword());
            final UserDetails userDetails = appUserDetailService.loadUserByUsername(request.getEmail());
            System.out.println("[LOGIN] User loaded: " + userDetails.getUsername());
            final String jwttoken= jwtUtil.generatetoken(userDetails);
            System.out.println("[LOGIN] JWT generated: " + jwttoken);


            ResponseCookie cookie = ResponseCookie.from("jwt",jwttoken)
                    .httpOnly(true).path("/").maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(new AuthResponse(request.getEmail(),jwttoken));


        }catch (BadCredentialsException ex){
            System.out.println("[LOGIN] Bad credentials for: " + request.getEmail());
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","wrong email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

        }
        catch (DisabledException de){
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","account disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        }
        catch (Exception e){
            System.out.println("[LOGIN] Bad credentials for: " + request.getEmail());
            Map<String,Object> error = new HashMap<>();
            error.put("error",true);
            error.put("message","Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        }

    }
//That authenticate(...) method is the trigger point for login validation in your controller.
// It activates the whole Spring Security pipeline that checks if the email-password combo is correct.
    private void authenticate(String email,String password){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));

    }

}
