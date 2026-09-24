package com.aungkokokhant.shifLink.auth;

import com.aungkokokhant.shifLink.employee.Employee;
import com.aungkokokhant.shifLink.employee.EmployeeRepository;
import com.aungkokokhant.shifLink.employee.dto.LoginRequest;
import com.aungkokokhant.shifLink.employee.dto.LoginResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        try {
            request.login(loginRequest.employeeCode(), loginRequest.password());

            Employee employee = employeeRepository
                    .findByEmployeeCode(loginRequest.employeeCode())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid employee code or password"
                    ));

            return new LoginResponse(
                    employee.getId(),
                    employee.getEmployeeCode(),
                    employee.getName(),
                    employee.getRole()
            );
        } catch (ServletException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid employee code or password"
            );
        }
    }

    @GetMapping("/me")
    public LoginResponse currentUser(HttpServletRequest request) {
        String employeeCode = request.getUserPrincipal().getName();

        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));

        return new LoginResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getName(),
                employee.getRole()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request)
            throws ServletException {

        request.logout();
        request.getSession().invalidate();

        return ResponseEntity.noContent().build();
    }
}