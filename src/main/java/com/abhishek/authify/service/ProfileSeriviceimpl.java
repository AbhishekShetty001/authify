package com.abhishek.authify.service;

import com.abhishek.authify.Entity.UserEntity;
import com.abhishek.authify.Reposistory.UserReposistory;
import com.abhishek.authify.io.ProfileRequest;
import com.abhishek.authify.io.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileSeriviceimpl implements ProfileService {
    private  final UserReposistory userReposistory;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if(!userReposistory.existsByEmail(request.getEmail())) {
            newProfile = userReposistory.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,"email already exists");
    }

    @Override
    public ProfileResponse getProfile(String email) {

        UserEntity existinguser = userReposistory.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found" + email));
        return convertToProfileResponse(existinguser);
    }

    @Override
    public void sendResetOtp(String mail) {
        UserEntity existinguser = userReposistory.findByEmail(mail).orElseThrow(()->new UsernameNotFoundException("user not found"+mail));
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(10000,100000));
        long expiretime = System.currentTimeMillis()+(60*1000*15);
        existinguser.setResetOtp(otp);
        existinguser.setResetOtpExpireAt(expiretime);


        userReposistory.save(existinguser);

        try{
            mailService.sendresetotp( existinguser.getEmail(),otp);

        }catch (RuntimeException e){
            throw new RuntimeException("unable to send email");
        }
    }

    @Override
    public void resetPassword(String mail, String otp, String newPassword) {
        UserEntity existinguser = userReposistory.findByEmail(mail).orElseThrow(()-> new UsernameNotFoundException("email not found "+ mail));
        if(existinguser.getResetOtp()==null || !existinguser.getResetOtp().equals(otp)){
            throw new RuntimeException("invalid otp ");
        }
        if(existinguser.getResetOtpExpireAt()<System.currentTimeMillis()){
            throw new RuntimeException("OTP expired");
        }

        existinguser.setPassword(passwordEncoder.encode(newPassword));
        existinguser.setResetOtp(null);
        existinguser.setResetOtpExpireAt(0L);
        userReposistory.save(existinguser);
    }

    @Override
    public void sendOtp(String mail) {
        UserEntity existinguser = userReposistory.findByEmail(mail).orElseThrow(()-> new UsernameNotFoundException("mail not found"+ mail));
        if(existinguser.getIsAccountVerified()!=null && existinguser.getIsAccountVerified()){
            return;
        }
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(10000,100000));
        long expiretime = System.currentTimeMillis()+(24*60*60*1000);

        existinguser.setVerifyOtpExpireAt(expiretime);
        existinguser.setVerifyOtp(otp);
        userReposistory.save(existinguser);
        try {
            mailService.sendverifyotp(existinguser.getEmail(), otp);
            System.out.println("Mail sent successfully");
        } catch (Exception e) {
            System.out.println("Mail sending failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("unable to send mail", e);
        }


    }

    @Override
    public void verifyOtp(String mail, String otp) {
        UserEntity existinguser = userReposistory.findByEmail(mail).orElseThrow(()-> new UsernameNotFoundException("mail not found"+ mail));
        if(existinguser.getVerifyOtp()==null ||  !existinguser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("invalid otp");
        }

        if(existinguser.getVerifyOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("otp expired");
        }

        existinguser.setIsAccountVerified(true);
        existinguser.setVerifyOtp(null);
        existinguser.setVerifyOtpExpireAt(0L);
        userReposistory.save(existinguser);
    }



    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .name(newProfile.getName())
                .isVerified(newProfile.isAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
       return UserEntity.builder()
               .email(request.getEmail())
               .userId(UUID.randomUUID().toString())
               .name(request.getName())
               .password(passwordEncoder.encode(request.getPassword()))
               .isAccountVerified(false)
               .resetOtp(null)
               .resetOtpExpireAt(0L)
               .verifyOtp(null)
               .verifyOtpExpireAt(0L)
               .build();
    }
}
