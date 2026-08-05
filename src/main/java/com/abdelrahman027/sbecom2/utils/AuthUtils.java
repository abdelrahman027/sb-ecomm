package com.abdelrahman027.sbecom2.utils;


import com.abdelrahman027.sbecom2.model.User;
import com.abdelrahman027.sbecom2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    private final UserRepository userRepository;


    public String loggedInEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication != null ? authentication.getName() : null).orElseThrow(()-> new UsernameNotFoundException("user not found"));
        return user.getEmail();
    }




    public Long loggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(authentication != null ? authentication.getName() : null).orElseThrow(()-> new UsernameNotFoundException("user not found"));
        return user.getUserId();
    }

    public User loggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(authentication != null ? authentication.getName() : null).orElseThrow(()-> new UsernameNotFoundException("user not found"));
    }
}
