package com.aungkokokhant.shifLink.config;

import com.aungkokokhant.shifLink.employee.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ProductionAdminInitializer {
    @Bean
    CommandLineRunner createInitialAdmin(
            EmployeeRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${APP_BOOTSTRAP_ADMIN_PASSWORD:}") String initialPassword
    ) {
        return args -> {
            if (initialPassword.isBlank()) return;

            if (initialPassword.length() < 16) {
                throw new IllegalStateException(
                        "Bootstrap Admin password must have at least 16 characters."
                );
            }

            // ရှိပြီးသား account ရဲ့ password ကို restart တိုင်း မပြောင်းပါ
            if (repository.findByEmployeeCode("ADM001").isPresent()) return;

            Employee admin = Employee.builder()
                    .employeeCode("ADM001")
                    .name("System Administrator")
                    .type(EmployeeType.REGULAR)
                    .department("Operations")
                    .status(EmployeeStatus.ACTIVE)
                    .role(UserRole.ADMIN)
                    .password(passwordEncoder.encode(initialPassword))
                    .build();

            repository.save(admin);
        };
    }
}
