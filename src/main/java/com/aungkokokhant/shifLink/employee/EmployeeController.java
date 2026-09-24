package com.aungkokokhant.shifLink.employee;

import com.aungkokokhant.shifLink.employee.dto.EmployeeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public List<Employee> findAll(){
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Employee findById(@PathVariable("id") Long id){
        return employeeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody EmployeeRequest request){
        Employee employee = employeeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable("id") Long id, @Valid @RequestBody EmployeeRequest request){
        return  employeeService.update(request, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
