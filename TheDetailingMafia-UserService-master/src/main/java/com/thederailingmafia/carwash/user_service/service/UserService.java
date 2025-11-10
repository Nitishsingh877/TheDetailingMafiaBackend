package com.thederailingmafia.carwash.user_service.service;

import com.thederailingmafia.carwash.user_service.dto.*;
import com.thederailingmafia.carwash.user_service.exception.EmailExistException;
import com.thederailingmafia.carwash.user_service.exception.PassswordNotFoundException;
import com.thederailingmafia.carwash.user_service.exception.RoleNotFoundException;
import com.thederailingmafia.carwash.user_service.exception.UserNotFoundException;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.model.UserRole;
import com.thederailingmafia.carwash.user_service.repository.UserRepository;
import com.thederailingmafia.carwash.user_service.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public  class UserService {
    @Autowired
//    Automatically injects dependencies
    private UserRepository userRepository;


    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    @Autowired
    private CustomerService customerService;
    @Autowired
    private WasherService washerService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    public UserModel saveUser(UserDto userDto, String authToken) {
        // Validate role
        if (userDto.getUserRole() == null) {
            throw new RoleNotFoundException("Role is required");
        }
        // Check if email exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailExistException("Email already exists. Please log in.");
        }
        UserModel userModel = new UserModel(userDto.getName(), userDto.getPassword(), userDto.getEmail(), userDto.getUserRole(),null, null,authToken);

        if(authToken.equals("Email")) {
            userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        }
         userRepository.save(userModel);

        if(userModel.getUserRole() == UserRole.CUSTOMER) {
            CustomerDto customerDto = new CustomerDto(userDto);
            customerService.CreateCustomer(userModel,customerDto);
        }

        if(userModel.getUserRole() == UserRole.WASHER){
            WasherDto washerDto = new WasherDto(userDto);
            washerService.SaveWasher(userModel,washerDto);

        }

        if(userModel.getUserRole() == UserRole.ADMIN) {
            AdminDto adminDto = new AdminDto(userDto);
            adminService.createAdmin(userModel,adminDto);


        }
        return userModel;
       // return jwtUtil.generateToken(userModel.getEmail(), userModel.getUserRole().name());
    }

    @Transactional
    //used because if user logged in changes will commit in db else it will rollback to intial state.
    public LoginResponseDto loginUser(String email, String password) throws Exception {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        if(!user.getAuth().equals("Email")) {
           throw new UserNotFoundException(email +" -> This is Wrong email. please try again");
        }

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new PassswordNotFoundException("This is Wrong email or password. Please try again");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getUserRole().name());
        UserDto userDto = new UserDto(user.getName(), user.getEmail(), null,user.getUserRole());

        return new LoginResponseDto(token,userDto);
    }

    public UserModel getUserProfile(String email) {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user;
    }

    public UserProfileResponse updateUserProfile(String email, UserProfileResponse userProfileResponse) {

        UserModel user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        if(userProfileResponse.getName() != null) {
            user.setName(userProfileResponse.getName());
        }
        if(userProfileResponse.getEmail() != null) {
            user.setEmail(userProfileResponse.getEmail());
        }

        userRepository.save(user);
        return new UserProfileResponse(user.getName(),user.getEmail(),user.getUserRole(),user.getAddress(),user.getPhoneNumber());
    }

    public UserModel findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }


}
