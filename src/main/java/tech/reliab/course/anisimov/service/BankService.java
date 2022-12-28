package tech.reliab.course.anisimov.service;

import tech.reliab.course.anisimov.entity.*;
import tech.reliab.course.anisimov.exception.*;

import java.util.List;

public interface BankService {
    // Создание банка
    Bank create(Bank bank) throws UnuniqeIdException;

    // Добавление банка
    Bank addBank(Bank bank) throws UnuniqeIdException;

    // Получение банка по id
    Bank getBankById(String bankId) throws DoesNotExistException;

    // Удалить банк по айди
    Boolean deleteBank(String bankId) throws DoesNotExistException;

    // Получить все банки
    List<Bank> getAllBanks();

    // Вычисление процентной ставки
    void calculateInterestRate(Bank bank);

    // Внести деньги
    void depositMoney(String bankId, double sum) throws DoesNotExistException;

    // Снять деньги
    void withdrawMoney(String bankId, double sum) throws DoesNotExistException, NotEnoughMoneyException;

    // Добавить офис
    void addOffice(String bankId, BankOffice office) throws DoesNotExistException;

    // Удалить офис
    Boolean removeOffice(String bankId, String officeId) throws DoesNotExistException;

    // Добавить новго клиента
    Boolean addClient(String bankId, User user) throws DoesNotExistException;

    // Удаление клиента
    Boolean removeClient(String bankId, String userId) throws DoesNotExistException;

    // Добавить работника
    Boolean addEmployee(String bankId, Employee employee) throws DoesNotExistException;

    // Удалить работника
    Boolean removeEmployee(String bankId, String employeeId) throws DoesNotExistException;

    // Одобрение кредита
    Boolean isCreditAllowed(String bankId, CreditAccount creditAccount, Employee employee) throws DoesNotExistException, IssuingCreditException;

    String stringRepresentation(String bankId) throws DoesNotExistException;

    List<Bank> getSuitableForLoanBanks(double sum, int monthCount) throws IssuingCreditException;

    Boolean isSuitableForLoan(String bankId, double money) throws DoesNotExistException;

    List<BankOffice> getSuitableForLoanOffices(String bankId, double money) throws DoesNotExistException;
}
