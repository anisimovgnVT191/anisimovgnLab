package tech.reliab.course.anisimov.service;

import tech.reliab.course.anisimov.entity.BankOffice;
import tech.reliab.course.anisimov.entity.Employee;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;

import java.util.List;

public interface EmployeeService {
    // Создание работника
    Employee create(Employee employee) throws UnuniqeIdException;

    // Добавить работника
    Employee addEmployee(Employee employee) throws UnuniqeIdException;

    // Получить работника по айди
    Employee getEmployeeById(String employeeId) throws DoesNotExistException;

    // Удалить работника
    Boolean deleteEmployee(String employeeId) throws DoesNotExistException;

    // Получить работников офиса
    List<Employee> getEmployeesByOfficeId(String officeId);

    // Получить работников банка
    List<Employee> getEmployeesByBank(String bankId);

    String stringRepresentation(String employeeId) throws DoesNotExistException;

    Boolean isSuitableForLoan(String employeeId) throws DoesNotExistException;
}
