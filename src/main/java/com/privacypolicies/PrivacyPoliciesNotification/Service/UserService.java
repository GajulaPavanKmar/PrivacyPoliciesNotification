package com.privacypolicies.PrivacyPoliciesNotification.Service;

import com.privacypolicies.PrivacyPoliciesNotification.Model.User;
import com.privacypolicies.PrivacyPoliciesNotification.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        System.out.println("Received username: " + userName);
        User user = findByUserName(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User not found for username: " + userName);
        }
        System.out.println("Stored hashed password for " + userName + ": " + user.getUserPassword());
        return new org.springframework.security.core.userdetails.User(
                user.getUserEmail(),
                user.getUserPassword(),
                new ArrayList<>());
    }

    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + userName));
    }

    public User createNewAccount(User user) {
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        User savedUser = userRepository.save(user);
        return savedUser;
    }



    public User updateUserDetails(User user) {
        // Assume we have a UserRepository to handle database operations
        // Validate user details here if necessary
        User existingUser = userRepository.findById(user.getUserId()).orElse(null);
        if (existingUser != null) {
            existingUser.setUserEmail(user.getUserEmail());
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            // Update other fields as necessary

            return userRepository.save(existingUser);
        }
        return null;
    }

}
