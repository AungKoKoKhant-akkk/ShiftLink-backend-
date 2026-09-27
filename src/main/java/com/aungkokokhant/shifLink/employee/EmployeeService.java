package com.aungkokokhant.shifLink.employee;

import com.aungkokokhant.shifLink.employee.dto.EmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private static final Set<String> PROTECTED_DEMO_CODES =
            Set.of("ADM-DEMO", "MGR-DEMO", "USR-DEMO");

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.demo-mode:false}")
    private boolean demoMode;

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Transactional
    public Employee create(EmployeeRequest request) {
        if (employeeRepository.existsByEmployeeCode(request.employeeCode())) {
            throw new IllegalArgumentException(
                    "Employee with code " + request.employeeCode() + " already exists"
            );
        }

        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException(
                    "An initial password is required for a new employee"
            );
        }

        Employee employee = Employee.builder()
                .employeeCode(request.employeeCode())
                .name(request.name())
                .type(request.type())
                .department(request.department())
                .status(request.status())
                .role(request.role())
                .password(passwordEncoder.encode(request.password()))
                .build();

        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(EmployeeRequest request, Long id) {
        Employee employee = findById(id);
        protectDemoAccount(employee);

        if (!employee.getEmployeeCode().equals(request.employeeCode())
                && employeeRepository.existsByEmployeeCode(request.employeeCode())) {
            throw new IllegalArgumentException(
                    "Employee with code " + request.employeeCode() + " already exists"
            );
        }

        employee.setEmployeeCode(request.employeeCode());
        employee.setName(request.name());
        employee.setType(request.type());
        employee.setDepartment(request.department());
        employee.setStatus(request.status());
        employee.setRole(request.role());

        if (request.password() != null && !request.password().isBlank()) {
            employee.setPassword(passwordEncoder.encode(request.password()));
        }

        return employee;
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = findById(id);
        protectDemoAccount(employee);
        employeeRepository.delete(employee);
    }

    private void protectDemoAccount(Employee employee) {
        if (demoMode
                && PROTECTED_DEMO_CODES.contains(employee.getEmployeeCode())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Demo login accounts cannot be changed"
            );
        }
    }
}