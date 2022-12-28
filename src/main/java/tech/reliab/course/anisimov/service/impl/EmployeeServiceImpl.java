package tech.reliab.course.anisimov.service.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.reliab.course.anisimov.entity.Bank;
import tech.reliab.course.anisimov.entity.BankOffice;
import tech.reliab.course.anisimov.entity.Employee;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;
import tech.reliab.course.anisimov.service.BankOfficeService;
import tech.reliab.course.anisimov.service.EmployeeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

final public class EmployeeServiceImpl implements EmployeeService {
    //region ===================== Properties ======================
    private final Map<String, Employee> employeeMap = new HashMap<>();

    //region ===================== DI ======================
    @NotNull public BankOfficeService bankOfficeService;

    //region ===================== EmployeeService implementation ======================
    @Override
    public @Nullable Employee create(@NotNull Employee employee) throws UnuniqeIdException {
        return this.addEmployee(new Employee(employee));
    }

    @Override
    public @Nullable Employee addEmployee(@NotNull Employee employee) throws UnuniqeIdException {
        if (this.employeeMap.containsKey(employee.getId())) { throw new UnuniqeIdException(employee.getId()); }

        BankOffice bankOffice = employee.getOffice();
        try {
            if (bankOffice != null && this.bankOfficeService.addEmployee(bankOffice.getId(), employee)) {
                this.employeeMap.put(employee.getId(), employee);
                return employee;
            }
        } catch (DoesNotExistException e) {
            return null;
        }

        return employee;
    }

    @Override
    public @NotNull Employee getEmployeeById(@NotNull String employeeId) throws DoesNotExistException {
        Employee employee = this.employeeMap.get(employeeId);
        if (employee == null) { throw new DoesNotExistException(employeeId); }

        return employee;
    }

    @Override
    public @NotNull Boolean deleteEmployee(@NotNull String employeeId) throws DoesNotExistException {
        Employee employee = this.getEmployeeById(employeeId);

        this.bankOfficeService.removeEmployee(employee.getOffice().getId(), employeeId);
        return this.employeeMap.remove(employeeId) != null;
    }

    @Override
    public @NotNull List<Employee> getEmployeesByOfficeId(@NotNull String officeId) {
        return this.employeeMap.values()
                .stream()
                .filter(employee -> Objects.equals(employee.getOffice().getId(), officeId))
                .toList();
    }

    @Override
    public @NotNull List<Employee> getEmployeesByBank(@NotNull String bankId) {
        return this.employeeMap.values()
                .stream()
                .filter(employee -> Objects.equals(employee.getBankOfWork().getId(), bankId))
                .toList();
    }

    @Override
    public @NotNull String stringRepresentation(@NotNull String employeeId) throws DoesNotExistException {
        Employee employee = this.getEmployeeById(employeeId);

        return employee.toString();
    }

    @Override
    public @NotNull Boolean isSuitableForLoan(@NotNull String employeeId) throws DoesNotExistException {
        Employee employee = this.getEmployeeById(employeeId);

        return employee.getCanIssueLoans();
    }
}
