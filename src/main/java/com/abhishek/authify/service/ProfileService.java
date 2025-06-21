package com.abhishek.authify.service;

import com.abhishek.authify.io.ProfileRequest;
import com.abhishek.authify.io.ProfileResponse;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);
    ProfileResponse getProfile(String email);
    void sendResetOtp(String mail);
    void resetPassword(String mail,String otp,String newPassword);
    void sendOtp(String mail);
    void verifyOtp(String mail,String otp);

}
