package tech.reliab.course.anisimov.service;

import tech.reliab.course.anisimov.entity.BankAtm;
import tech.reliab.course.anisimov.exception.CannotDepositMoneyException;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;

import java.util.List;

public interface AtmService {
    // Создать банкомат
    BankAtm create(BankAtm bankAtm) throws UnuniqeIdException;

    // Добавить банкомат
    BankAtm addAtm(BankAtm bankAtm) throws UnuniqeIdException;

    // Получить банкомат по айди
    BankAtm getAtmById(String atmId) throws DoesNotExistException;

    // Получить все бакнкоматы банка
    List<BankAtm> getAllAtms();

    // Получить все банкоматы офисы
    List<BankAtm> getAtmsByOfficeId(String officeId);

    // Получить вс банкоматы банка
    List<BankAtm> getAtmsByBankId(String bankId);

    // Удалить банкомат
    Boolean deleteAtm(String atmId) throws DoesNotExistException, NotEnoughMoneyException;

    // Внести деньги
    void depositMoney(String atmId, double sum) throws DoesNotExistException, CannotDepositMoneyException;

    void withdrawMoney(String atmId, double sum) throws DoesNotExistException, NotEnoughMoneyException;

    String stringRepresentation(String atmId) throws DoesNotExistException;

    Boolean isSuitableForLoan(String atmId, double sum) throws DoesNotExistException;
}
