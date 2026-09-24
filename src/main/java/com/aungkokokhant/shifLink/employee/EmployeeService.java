package com.aungkokokhant.shifLink.employee;

import com.aungkokokhant.shifLink.employee.dto.EmployeeRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }

    public Employee findById(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(()-> new EmployeeNotFoundException(id));

    }

    @Transactional
    public Employee create(EmployeeRequest request){
        if(employeeRepository.existsByEmployeeCode(request.employeeCode())){
            throw new IllegalArgumentException("Employee with code " + request.employeeCode() + " already exists");

        }

        Employee employee = new Employee(
                null,
                request.employeeCode(),
                request.name(),
                request.type(),
                request.department(),
                EmployeeStatus.ACTIVE
        );
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(EmployeeRequest request, Long id){
        Employee employee = findById(id);

        if(!employee.getEmployeeCode().equals(request.employeeCode())
            && employeeRepository.existsByEmployeeCode(request.employeeCode())){
            throw new IllegalArgumentException("Employee with code " + request.employeeCode() + " already exists");
        }
        employee.setEmployeeCode(request.employeeCode());
        employee.setName(request.name());
        employee.setType(request.type());
        employee.setDepartment(request.department());
        employee.setStatus(request.status());
        return employee;
    }

    @Transactional
    public void delete(Long id){
        Employee employee = findById(id);
        employeeRepository.delete(employee);
    }

}
