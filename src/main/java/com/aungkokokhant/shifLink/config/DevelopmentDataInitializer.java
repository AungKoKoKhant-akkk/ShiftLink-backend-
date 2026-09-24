package com.aungkokokhant.shifLink.config;

import com.aungkokokhant.shifLink.employee.Employee;
import com.aungkokokhant.shifLink.employee.EmployeeRepository;
import com.aungkokokhant.shifLink.employee.EmployeeStatus;
import com.aungkokokhant.shifLink.employee.EmployeeType;
import com.aungkokokhant.shifLink.employee.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevelopmentDataInitializer {

    private static final String DEFAULT_PASSWORD = "password";

    @Bean
    CommandLineRunner seedDefaultUsers(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            ensureDefaultUser(employeeRepository, passwordEncoder, "ADM001", "System Administrator", UserRole.ADMIN);
            ensureDefaultUser(employeeRepository, passwordEncoder, "MGR001", "Shift Manager", UserRole.MANAGER);
        };
    }

    private void ensureDefaultUser(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            String employeeCode,
            String name,
            UserRole role
    ) {
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseGet(() -> Employee.builder()
                        .employeeCode(employeeCode)
                        .name(name)
                        .type(EmployeeType.REGULAR)
                        .department("Operations")
                        .status(EmployeeStatus.ACTIVE)
                        .role(role)
                        .build());

        if (!passwordEncoder.matches(DEFAULT_PASSWORD, employee.getPassword())) {
            employee.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        }

        employeeRepository.save(employee);
    }
}