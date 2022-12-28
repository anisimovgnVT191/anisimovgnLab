package tech.reliab.course.anisimov.service.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.reliab.course.anisimov.entity.Bank;
import tech.reliab.course.anisimov.entity.BankAtm;
import tech.reliab.course.anisimov.entity.BankOffice;
import tech.reliab.course.anisimov.exception.CannotDepositMoneyException;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;
import tech.reliab.course.anisimov.service.AtmService;
import tech.reliab.course.anisimov.service.BankOfficeService;

import java.util.*;

final public class AtmServiceImpl implements AtmService {
    //region ===================== Properties ======================
    private final Map<String, BankAtm> bankAtmMap = new HashMap<>();

    //region ===================== DI ======================
    @NotNull public BankOfficeService bankOfficeService;

    //region ===================== Constructor ======================
    public AtmServiceImpl() {
    }

    //region ===================== AtmService implementation ======================
    @Override
    public @Nullable BankAtm create(@NotNull BankAtm bankAtm) throws UnuniqeIdException {
        return this.addAtm(bankAtm);
    }

    @Override
    public @Nullable BankAtm addAtm(@NotNull BankAtm bankAtm) throws UnuniqeIdException {
        if (this.bankAtmMap.containsKey(bankAtm.getId())) {
            throw new UnuniqeIdException(bankAtm.getId());
        }

        BankOffice bankOffice = bankAtm.getBankOffice();
        if (bankOffice != null) {
            this.bankAtmMap.put(bankAtm.getId(), bankAtm);
            try {
                this.bankOfficeService.addAtm(bankOffice.getId(), bankAtm);
            } catch (DoesNotExistException e) {
                return null;
            }
            return bankAtm;
        } else {
            System.out.println("У банкомата не указан банк");
            return null;
        }
    }

    @Override
    public @NotNull BankAtm getAtmById(@NotNull String atmId) throws DoesNotExistException {
        BankAtm bankAtm = this.bankAtmMap.get(atmId);

        if (bankAtm == null) {
            throw new DoesNotExistException(atmId);
        } else {
            return bankAtm;
        }
    }

    @Override
    public @NotNull List<BankAtm> getAllAtms() {
        return new ArrayList<>(this.bankAtmMap.values());
    }

    @Override
    public @NotNull List<BankAtm> getAtmsByOfficeId(@NotNull String officeId) {
        return this.bankAtmMap.values()
                .stream()
                .filter(atm -> Objects.equals(atm.getBankOffice().getId(), officeId))
                .toList();
    }

    @Override
    public @NotNull List<BankAtm> getAtmsByBankId(@NotNull String bankId) {
        return this.bankAtmMap.values()
                .stream()
                .filter(atm -> Objects.equals(atm.getBank().getId(), bankId))
                .toList();
    }

    @Override
    public @NotNull Boolean deleteAtm(@NotNull String atmId) throws DoesNotExistException {
        BankAtm atm = this.bankAtmMap.get(atmId);
        if (atm == null) { throw new DoesNotExistException(atmId); }


        if (this.bankOfficeService.removeAtm(atm.getBankOffice().getId(), atmId)) {
            return this.bankAtmMap.remove(atmId) != null;
        } else {
            return false;
        }
    }

    @Override
    public void depositMoney(@NotNull String atmId, double sum) throws DoesNotExistException, CannotDepositMoneyException {
        BankAtm atm = this.getAtmById(atmId);
        if (atm.getBankOffice() == null || atm.getBank() == null) { return; }

        switch (atm.getStatus()) {
            case OPEN, RUN_OUT_OF_MONEY -> {
                if (atm.getCashDepositAvailable()) {
                    BankOffice bankOffice = atm.getBankOffice();
                    Bank bank = atm.getBank();
                    double newSum = atm.getTotalCash() + sum;

                    atm.setTotalCash(newSum);
                    bankOffice.setTotalCash(bankOffice.getTotalCash() + newSum);
                    bank.setTotalMoney(bank.getTotalMoney() + newSum);
                } else {
                    throw new CannotDepositMoneyException();
                }
            }
            case CLOSED -> {
                System.out.println("Банкомат закрыт");
            }
        }
    }

    @Override
    public void withdrawMoney(@NotNull String atmId, double sum) throws DoesNotExistException, NotEnoughMoneyException {
        BankAtm atm = this.getAtmById(atmId);
        if (atm.getBank() == null || atm.getBankOffice() == null) { return; }

        switch (atm.getStatus()) {
            case OPEN -> {
                if (atm.getCashWithdrawalAvailable()) {
                    if (atm.getTotalCash() >= sum) {
                        BankOffice office = atm.getBankOffice();
                        Bank bank = atm.getBank();
                        double newSum = atm.getTotalCash() - sum;

                        atm.setTotalCash(newSum);
                        office.setTotalCash(office.getTotalCash() - newSum);
                        bank.setTotalMoney(bank.getTotalMoney() - newSum);
                    } else {
                        throw new NotEnoughMoneyException();
                    }
                } else {
                    System.out.println("Снятие наличных невозможно");
                }
            }
            case CLOSED -> {
                System.out.println("Банкомат закрыт");
            }
            case RUN_OUT_OF_MONEY -> {
                System.out.println("В банкомате закончились деньги");
            }
        }
    }

    @Override
    public @NotNull String stringRepresentation(@NotNull String atmId) throws DoesNotExistException {
        BankAtm bankAtm = this.getAtmById(atmId);

        return bankAtm.toString();
    }

    @Override
    public @NotNull Boolean isSuitableForLoan(@NotNull String atmId, double sum) throws DoesNotExistException {
        BankAtm atm = this.getAtmById(atmId);

        return atm.getTotalCash() > sum;
    }
}
