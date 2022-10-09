package org.comppress.customnewsapi.controller;


import lombok.RequiredArgsConstructor;
import org.comppress.customnewsapi.dto.AuthRequestDto;
import org.comppress.customnewsapi.dto.AuthResponseDto;
import org.comppress.customnewsapi.dto.UserDto;
import org.comppress.customnewsapi.security.JwtTokenUtil;
import org.comppress.customnewsapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthenticationsController {

    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "/register")
    public ResponseEntity<UserDto> saveUser(@RequestBody @Valid UserDto userDto){
        return userService.saveUser(userDto);
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequestDto authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponseDto(token, userDetails.getUsername()));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser() {

        return userService.deleteUser();
    }

//    @RequestMapping(value = "/delete/{email}", method = RequestMethod.GET)
//    public ResponseEntity<?> getDeletedUser(
//            @PathVariable(name = "email") String email
//    )  {
//
//        return userService.getDeletedUser(email);
//    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}
