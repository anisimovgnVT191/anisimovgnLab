package tech.reliab.course.anisimov.service;

import tech.reliab.course.anisimov.entity.BankAtm;
import tech.reliab.course.anisimov.entity.BankOffice;
import tech.reliab.course.anisimov.entity.Employee;
import tech.reliab.course.anisimov.exception.CannotDepositMoneyException;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;

import java.util.List;

public interface BankOfficeService {
    // Создать офис
    BankOffice create(BankOffice bankOffice) throws UnuniqeIdException;

    // Добавить офис
    BankOffice addOffice(BankOffice office) throws UnuniqeIdException;

    // Получить все офисы
    List<BankOffice> getAllOffices();

    // Получить офис по айди
    BankOffice getOfficeById(String officeId) throws DoesNotExistException;

    // Получить все офисы банка
    List<BankOffice> getOfficesByBankId(String bankId);

    // Удалить офис банка
    Boolean deleteOffice(String officeId) throws DoesNotExistException;

    // Добавить банкомат в офис
    Boolean addAtm(String officeId, BankAtm bankAtm) throws DoesNotExistException;

    // Удалить банкомат из офиса
    Boolean removeAtm(String officeId, String atmId) throws DoesNotExistException;

    // Добавить сотрудника
    Boolean addEmployee(String officeId, Employee employee) throws DoesNotExistException;

    // Удалить сотрудника
    Boolean removeEmployee(String officeId, String employeeId) throws DoesNotExistException;

    // Внести деньги в офис
    void depositMoney(String bankOfficeId, double sum) throws DoesNotExistException, CannotDepositMoneyException;

    // Снять деньги из офис
    void withdrawMoney(String bankOfficeId, double sum) throws DoesNotExistException, NotEnoughMoneyException;

    String stringRepresentation(String officeId) throws DoesNotExistException;

    Boolean isSuitableForLoan(String officeId, double money) throws DoesNotExistException;

    List<BankAtm> getSuitableForLoanAtms(String officeId, double money) throws DoesNotExistException;

    List<Employee> getSuitableForLoanEmployees(String officeId) throws DoesNotExistException;
}
