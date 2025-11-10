package com.thederailingmafia.carwash.user_service.controller;

import com.thederailingmafia.carwash.user_service.dto.*;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.model.UserRole;
import com.thederailingmafia.carwash.user_service.model.Washer;
import com.thederailingmafia.carwash.user_service.repository.UserRepository;
import com.thederailingmafia.carwash.user_service.repository.WasherRepository;
import com.thederailingmafia.carwash.user_service.service.UserService;
import com.thederailingmafia.carwash.user_service.service.WasherService;
import com.thederailingmafia.carwash.user_service.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Slf4j
@Tag(name = "User Management", description = "User authentication and profile management APIs") // Swagger Tag
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WasherService washerService;
    @Autowired
    private WasherRepository washerRepository;

    public UserController(UserService userService, UserRepository userRepository, WasherService washerService, WasherRepository washerRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.washerService = washerService;
        this.washerRepository = washerRepository;
    }

    @GetMapping("/health")
    @Operation(summary = "Check API Health", description = "Returns 'OK' if API is running")
    public String health() {
        log.info(userService.toString());
        return "OK";
    }

    @PostMapping("/signUp")
    @Operation(summary = "User Sign Up", description = "Registers a new user and returns JWT token")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserDto userDto) {
        try {
//            System.out.println("hello mai abhi aaya");
            UserModel user = userService.saveUser(userDto,"Email");
            String token = jwtUtil.generateToken(user.getEmail(), user.getUserRole().name());
            log.info("signup for the user {}", user.getEmail());
            return  ResponseEntity.ok(new LoginResponseDto(token,userDto));
        }catch (Exception e){
          //  e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Signup failed: " + e.getMessage());
        }
    }



    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user and returns JWT token")
    public LoginResponseDto login(@RequestBody UserDto userDto) throws Exception {
        log.info("login for the user " + userDto.getEmail());
       return userService.loginUser(userDto.getEmail(),userDto.getPassword());
    }

    @GetMapping("/profile")
    @Operation(summary = "Get User Profile", description = "Fetches the profile of the authenticated user")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }
            
            String email = authentication.getName();
            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            log.info("get profile for the user {}", user.getEmail());
            UserProfileResponse response = new UserProfileResponse(
                user.getName(), 
                user.getEmail(), 
                user.getUserRole(),
                user.getAddress(), 
                user.getPhoneNumber()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting profile: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error getting profile: " + e.getMessage());
        }
    }


    @PutMapping("/update/profile")
    @Operation(summary = "Update Profile", description = "Updates the profile of the authenticated user")
    public UserProfileResponse updateProfile(@RequestBody UserProfileResponse userProfileResponse, Authentication authentication)  {
        String email = authentication.getName();

        return userService.updateUserProfile(email,userProfileResponse);

    }

    @GetMapping("/washers")
    public WasherResponse getActiveWashers() {
        List<String> washer = washerRepository.findByIsActiveTrue()
                .stream()
                .map(Washer::getWasherEmail)
                .collect(Collectors.toList());

        return new WasherResponse(washer,false);
    }



    @PutMapping("/{id}/status")
    public ResponseEntity<WasherDto> updateStatus(Authentication authentication, @RequestParam boolean isActive) {
        String mail = authentication.getName();
        //System.out.println(mail);
        Washer updatedWasher = washerService.updateWasherStatus(mail, isActive);
        //System.out.println("chlla ji");
        WasherDto dto = new WasherDto();
        dto.setWasherName(updatedWasher.getWasherName());
        dto.setWasherEmail(updatedWasher.getWasherEmail());
        dto.setIsActive(updatedWasher.isActive());
        return ResponseEntity.ok(dto);

    }

    @GetMapping("/validate")
    @Operation(summary = "Validate User", description = "Validates user token and returns user details if valid")
    public ResponseEntity<?> validateUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            UserModel user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Return user details without sensitive information
            UserProfileResponse response = new UserProfileResponse(
                    user.getName(),
                    user.getEmail(),
                    user.getUserRole(),
                    user.getAddress(),
                    user.getPhoneNumber()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token or user: " + e.getMessage());
        }
    }



}
