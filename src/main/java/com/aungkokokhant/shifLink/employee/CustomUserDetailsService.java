package com.aungkokokhant.shifLink.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String employeeCode)
            throws UsernameNotFoundException {

        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Employee not found: " + employeeCode
                ));

        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new UsernameNotFoundException("This employee is not active");
        }

        return User.builder()
                .username(employee.getEmployeeCode())
                .password(employee.getPassword())
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_" + employee.getRole().name()
                        )
                )
                .build();
    }
}