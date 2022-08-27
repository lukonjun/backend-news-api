package org.comppress.customnewsapi.security;

import org.comppress.customnewsapi.exceptions.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AppAuthenticationService {

    public String getCurrentUsername() throws AuthenticationException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            throw new AuthenticationException("You are not authorized, please login","");
        }
        return  authentication.getName();
    }

}
