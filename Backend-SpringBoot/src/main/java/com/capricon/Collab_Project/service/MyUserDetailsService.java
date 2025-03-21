package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.model.UserPrincipal;
import com.capricon.Collab_Project.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserException("User does not exist"));

        return new UserPrincipal(user);

    }
}
