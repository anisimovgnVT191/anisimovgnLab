package tech.reliab.course.anisimov.service.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.reliab.course.anisimov.entity.*;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.IssuingCreditException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;
import tech.reliab.course.anisimov.service.BankOfficeService;
import tech.reliab.course.anisimov.service.BankService;
import tech.reliab.course.anisimov.service.UserService;

import java.util.*;
import java.util.random.RandomGenerator;

final public class BankServiceImpl implements BankService {
    //region ===================== Properties ======================
    private Map<String, Bank> bankMap = new HashMap<>();

    //region ===================== DI ======================
    @NotNull public BankOfficeService bankOfficeService;
    @NotNull public UserService userService;

    //region ===================== BankService implementation ======================
    @Override
    public @NotNull Bank create(@NotNull Bank bank) throws UnuniqeIdException {
        bank.setRating(RandomGenerator.getDefault().nextInt(100));
        bank.setTotalMoney(RandomGenerator.getDefault().nextDouble(10_000, 1_000_000));
        this.calculateInterestRate(bank);
        return this.addBank(bank);
    }

    @Override
    public @NotNull Bank addBank(@NotNull Bank bank) throws UnuniqeIdException {
        if (this.bankMap.containsKey(bank.getId())) { throw new UnuniqeIdException(bank.getId()); }

        this.bankMap.put(bank.getId(), bank);
        return this.bankMap.get(bank.getId());
    }

    @Override
    public @NotNull Bank getBankById(@NotNull String bankId) throws DoesNotExistException {
        Bank bank = this.bankMap.get(bankId);
        if (bank == null) { throw new DoesNotExistException(bankId);}

        return bank;
    }

    @Override
    public @NotNull Boolean deleteBank(@NotNull String bankId) throws DoesNotExistException {
        if (this.bankMap.containsKey(bankId)) { throw new DoesNotExistException(bankId); }

        for (BankOffice bankOffice : this.bankOfficeService.getOfficesByBankId(bankId)) {
            this.removeOffice(bankId, bankOffice.getId());
        }

        return this.bankMap.remove(bankId) != null;
    }

    @Override
    public @NotNull List<Bank> getAllBanks() {
        return new ArrayList<>(this.bankMap.values());
    }

    @Override
    public void calculateInterestRate(@NotNull Bank bank) {
        int rating = bank.getRating();
        double offset = RandomGenerator.getDefault().nextDouble() * 4;

        if (rating < 30) {
            bank.setInterestRate(offset + 16);
        } else if (rating < 60) {
            bank.setInterestRate(offset + 11);
        } else if (rating < 90) {
            bank.setInterestRate(offset + 6);
        } else {
            bank.setInterestRate(offset + 1);
        }

    }

    @Override
    public void depositMoney(@NotNull String bankId, double sum) throws DoesNotExistException {
        Bank bank = this.getBankById(bankId);

        bank.setTotalMoney(bank.getTotalMoney() + sum);
    }

    @Override
    public void withdrawMoney(@NotNull String bankId, double sum) throws DoesNotExistException, NotEnoughMoneyException {
        Bank bank = this.getBankById(bankId);

        if (bank.getTotalMoney() >= sum) {
            bank.setTotalMoney(bank.getTotalMoney() - sum);
        } else {
            throw new NotEnoughMoneyException();
        }
    }

    @Override
    public void addOffice(@NotNull String bankId, @NotNull BankOffice office) throws DoesNotExistException {
        Bank bank = this.getBankById(bankId);

        bank.setOfficesCount(bank.getOfficesCount() + 1);
        bank.setAtmsCount(bank.getAtmsCount() + office.getAtmsCount());

        this.depositMoney(bankId, office.getTotalCash());
    }

    @Override
    public @NotNull Boolean removeOffice(@NotNull String bankId, @NotNull String officeId) throws DoesNotExistException {
        Bank bank = this.getBankById(bankId);
        BankOffice bankOffice = this.bankOfficeService.getOfficeById(officeId);

        this.bankOfficeService.deleteOffice(officeId);
        bank.setOfficesCount(bank.getOfficesCount() - 1);
        try {
            this.withdrawMoney(bankId, bankOffice.getTotalCash());
        } catch (NotEnoughMoneyException e) {
            return false;
        }

        return true;
    }

    @Override
    public @NotNull Boolean addClient(@NotNull String bankId, @NotNull User user) throws DoesNotExistException {
        Bank bank = this.getBankById(bankId);

        user.setBank(bank);
        bank.setClientsCount(bank.getClientsCount() + 1);

        return true;
    }

    @Override
    public @NotNull Boolean removeClient(@NotNull String bankId, @NotNull String userId) throws DoesNotExistException {
        Bank bank = this.getBankById(bankId);

        bank.setClientsCount(bank.getClientsCount() - 1);
        return true;
    }

    @Override
    public @NotNull Boolean addEmployee(@NotNull String bankId, @NotNull Employee employee) throws DoesNotExistException {
        Bank bank = this.getBankById(bankId);

        employee.setBankOfWork(bank);
        bank.setEmployeesCount(bank.getEmployeesCount() + 1);

        return true;
    }

    @Override
    public @NotNull Boolean removeEmployee(@NotNull String bankId, @NotNull String employeeId) throws DoesNotExistException {
        Bank bank = this.getBankById(bankId);

        bank.setEmployeesCount(bank.getEmployeesCount() - 1);
        return true;
    }

    @Override
    public @NotNull Boolean isCreditAllowed(
            @NotNull String bankId,
            @NotNull CreditAccount creditAccount,
            @NotNull Employee employee
    ) throws DoesNotExistException, IssuingCreditException {
        Bank bank = this.getBankById(bankId);

        double sum = creditAccount.getLoanAmount();
        if (bank.getTotalMoney() >= sum) {
            if (employee.getCanIssueLoans()) {
                double sumMonthPay = sum * (bank.getInterestRate() / 100 + 1) / creditAccount.getLoanMonthCount();

                if (creditAccount.getCustomer().getMonthlyIncome() < sumMonthPay) {
                    creditAccount.setEmployee(employee);
                    creditAccount.setMonthlyPayment(sumMonthPay);
                    creditAccount.setBank(bank);
                    creditAccount.setInterestRate((int)bank.getInterestRate());
                    creditAccount.setLoadEndDate(creditAccount.getLoanStartDate().plusMonths(creditAccount.getLoanMonthCount()));

                    return true;
                }
            }
        }

        throw new IssuingCreditException();
    }

    @Override
    public @NotNull String stringRepresentation(@NotNull String bankId) throws DoesNotExistException {
        Bank bank = this.getBankById(bankId);

        StringBuilder builder = new StringBuilder(bank.toString());

        builder.append("Информация о офисах\n");
        for (BankOffice bankOffice : this.bankOfficeService.getOfficesByBankId(bankId)) {
            builder.append(this.bankOfficeService.stringRepresentation(bankOffice.getId())).append("\n");
        }

        builder.append("Информация о клиентах\n");
        for (User user : this.userService.getAllUsersByBankId(bankId)) {
            builder.append(this.userService.stringRepresentation(user.getId())).append("\n");
        }

        builder.append("\n");
        return builder.toString();
    }

    @Override
    public @NotNull List<Bank> getSuitableForLoanBanks(double sum, int monthCount) throws IssuingCreditException {
        List<Bank> banks = new ArrayList<>();

        for (Bank bank : this.getAllBanks()) {
            try {
                if (this.isSuitableForLoan(bank.getId(), sum)) {
                    banks.add(bank);
                }
            } catch (DoesNotExistException e) {
                throw new IssuingCreditException();
            }
        }
        if (banks.isEmpty()) { throw new IssuingCreditException(); }

        return banks;
    }

    @Override
    public @NotNull Boolean isSuitableForLoan(@NotNull String bankId, double money) throws DoesNotExistException {
        return !this.getSuitableForLoanOffices(bankId, money).isEmpty();
    }

    @Override
    public @NotNull List<BankOffice> getSuitableForLoanOffices(@NotNull String bankId, double money) throws DoesNotExistException {
        List<BankOffice> bankOffices = new ArrayList<>();

        for (BankOffice bankOffice : this.bankOfficeService.getOfficesByBankId(bankId)) {
            if (this.bankOfficeService.isSuitableForLoan(bankOffice.getId(), money)) {
                bankOffices.add(bankOffice);
            }
        }

        return bankOffices;
    }
}
