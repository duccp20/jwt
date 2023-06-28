package com.example.jwt.controller;

import com.example.jwt.dto.request.SignInForm;
import com.example.jwt.dto.request.SignUpForm;
import com.example.jwt.dto.response.JwtResponse;
import com.example.jwt.dto.response.ResponseMessage;
import com.example.jwt.model.Role;
import com.example.jwt.model.RoleName;
import com.example.jwt.model.User;
import com.example.jwt.security.jwt.JwtProvider;
import com.example.jwt.security.userPrincipal.UserPrincipal;
import com.example.jwt.service.RoleService;
import com.example.jwt.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signUp")
    public ResponseEntity<ResponseMessage> doSignUp(@RequestBody SignUpForm signUpForm) {
        //kiểm tra xem user đã tồn tại trong db chưa
        boolean isExistUsername = userService.existsByUsername(signUpForm.getUsername());
        //Nếu tồn tại trả về lỗi
        if (isExistUsername) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("This username is already existed!")
                            .data("")
                            .build()
            );
        }
    //tương tự
        boolean isExistPhoneNumber = userService.existsByPhone(signUpForm.getPhoneNumber());
        if (isExistPhoneNumber) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    ResponseMessage.builder()
                            .status("FAILED")
                            .message("This phone number is already existed!")
                            .data("")
                            .build()
            );
        }

        //sau khi đã check thì set role cho user đó
        Set<Role> roles = new HashSet<>();
        //Nếu form k có role hoặc là k nhập gì thì mặc định là USER
        if (signUpForm.getRoles() == null || signUpForm.getRoles().isEmpty()) {
            //check xem trong db table Role có role tên là user chưa, nếu chưa thì báo lỗi
            Role role = roleService.findByName(RoleName.USER).orElseThrow(
                    () -> new RuntimeException("Failed -> NOT FOUND ROLE"));
            roles.add(role); // nếu trong db có thì add cho user này = role có trong db vừa query
            System.out.println(roles);
        } else {
            //Nếu form có role thì set role cho user này theo role.
            signUpForm.getRoles().forEach(role -> {
                        switch (role) {
                            case "admin":
                                Role adminRole = roleService.findByName(RoleName.ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Failed -> NOT FOUND ROLE"));
                                roles.add(adminRole);
                            case "user":
                                Role userRole = roleService.findByName(RoleName.USER)
                                        .orElseThrow(() -> new RuntimeException("Failed -> NOT FOUND ROLE"));
                                roles.add(userRole);
                        }

                    });
        }


        //Đã check thông tin và add role. Tiếp theo là tạo constructor cho user đó
        User user = User.builder()
                .fullName(signUpForm.getFullName())
                .username(signUpForm.getUsername())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .phone(signUpForm.getPhoneNumber())
                .roles(roles)
                .build();
        // thông báo cho client
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseMessage.builder()
                        .status("ok")
                        .message("Account was created successfully!")
                        .data(userService.save(user)) //bước lưu vào db
                        .build()
        );
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> doSignIn(@RequestBody SignInForm signInForm) {

        //khi đăng nhập, bắt đầu authen.
        try {
            Authentication authentication = authenticationManager // này là do ta config bên WebSercurityConfig và có đặt bean cho nó, nên ta inject vào và dùng
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            signInForm.getUsername(), //authen = username và password
                            signInForm.getPassword())
                    );

            //sau khi đã authen thì inhetc jwtprovider và generate token
            String token = jwtProvider.generateToken(authentication);
            //kết quả trả về cuối cùng là UserPrincipal như đã nói từ trước
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return new ResponseEntity<>(
                    JwtResponse.builder()
                            .status("OK")
                            .type("Bearer")
                            .fullName(userPrincipal.getFullName())
                            .token(token)
                            .roles(userPrincipal.getAuthorities())
                            .build(), HttpStatus.OK);

        } catch (AuthenticationException e) {
            //trong quá trình authen nếu có lỗi thì sẽ bắn ra như này
            return new ResponseEntity<>(
                    ResponseMessage.builder()
                            .status("Failed")
                            .message("Invalid username or password!")
                            .data("")
                            .build(), HttpStatus.UNAUTHORIZED);
        }
    }
}