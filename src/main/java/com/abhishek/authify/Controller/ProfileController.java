package com.abhishek.authify.Controller;

import com.abhishek.authify.io.ProfileRequest;
import com.abhishek.authify.io.ProfileResponse;
import com.abhishek.authify.io.ResetPasswordRequest;
import com.abhishek.authify.service.MailService;
import com.abhishek.authify.service.ProfileSeriviceimpl;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileSeriviceimpl service;

    private final MailService mailService;
    private final ProfileSeriviceimpl profileSeriviceimpl;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse Register(@Valid  @RequestBody ProfileRequest request){
        ProfileResponse response = service.createProfile(request);

        //welcome email
        mailService.welcomemail(response.getEmail(),response.getName());
        return response;
    }

    @GetMapping("/profile")
    public ProfileResponse getprofile(@CurrentSecurityContext(expression = "Authentication?.name") String email){
       return service.getProfile(email);
    }


    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isauthenticated(@CurrentSecurityContext(expression = "Authentication?.name")String email){
        return ResponseEntity.ok(email!=null);
    }

    @PostMapping("/send-reset-otp")
    public void sendresetotp(@RequestParam String email){
        try{
        profileSeriviceimpl.sendResetOtp(email);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public void resetpassword(@Valid @RequestBody ResetPasswordRequest request){
        try {
            profileSeriviceimpl.resetPassword(request.getEmail(), request.getOtp(),request.getNewpassword());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public void sendverifyotp(@CurrentSecurityContext(expression = "Authentication?.name") String email){
        try{
            profileSeriviceimpl.sendOtp(email);

        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());

        }
    }

    @PostMapping("/verify-otp")
    public void verifyotp(@RequestBody Map<String,Object> request,@CurrentSecurityContext(expression = "Authentication?.name") String email){
        if(request.get("otp").toString()==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"missing value");

        }


        try{
            profileSeriviceimpl.verifyOtp(email,request.get("otp").toString());

        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?>logout(HttpServletResponse response){
        ResponseCookie cookie = ResponseCookie.from("jwt","")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("logged out successfully");
    }




}
