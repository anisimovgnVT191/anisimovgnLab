package tech.reliab.course.anisimov.service.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.reliab.course.anisimov.entity.Bank;
import tech.reliab.course.anisimov.entity.BankAtm;
import tech.reliab.course.anisimov.entity.BankOffice;
import tech.reliab.course.anisimov.entity.Employee;
import tech.reliab.course.anisimov.exception.CannotDepositMoneyException;
import tech.reliab.course.anisimov.exception.DoesNotExistException;
import tech.reliab.course.anisimov.exception.NotEnoughMoneyException;
import tech.reliab.course.anisimov.exception.UnuniqeIdException;
import tech.reliab.course.anisimov.service.AtmService;
import tech.reliab.course.anisimov.service.BankOfficeService;
import tech.reliab.course.anisimov.service.BankService;
import tech.reliab.course.anisimov.service.EmployeeService;

import java.util.*;

final public class BankOfficeServiceImpl implements BankOfficeService {
    //region ===================== Properties ======================
    private final Map<String, BankOffice> bankOfficeMap= new HashMap<>();

    //region ===================== DI ======================
    @NotNull public BankService bankService;
    @NotNull public AtmService atmService;
    @NotNull public EmployeeService employeeService;

    //region ===================== Constructor ======================
    public BankOfficeServiceImpl() { }

    //region ===================== BankOfficeService implementation ======================
    @Override
    public @Nullable BankOffice create(@NotNull BankOffice bankOffice) throws UnuniqeIdException {
        return this.addOffice(bankOffice);
    }

    @Override
    public @Nullable BankOffice addOffice(BankOffice office) throws UnuniqeIdException {
        if (this.bankOfficeMap.containsKey(office.getId())) {
            throw new UnuniqeIdException(office.getId());
        }

        Bank bank = office.getBank();
        if (bank != null) {
            this.bankOfficeMap.put(office.getId(), office);
            try {
                this.bankService.addOffice(bank.getId(), office);
            } catch (DoesNotExistException e) {
                return null;
            }
            return office;
        } else {
            System.out.println("У офиса не указан банк");
            return null;
        }
    }

    @Override
    public @NotNull List<BankOffice> getAllOffices() {
        return new ArrayList<>(bankOfficeMap.values());
    }

    @Override
    public @NotNull BankOffice getOfficeById(@NotNull String officeId) throws DoesNotExistException {
        BankOffice bankOffice = this.bankOfficeMap.get(officeId);

        if (bankOffice == null) {
            throw new DoesNotExistException(officeId);
        } else {
            return bankOffice;
        }
    }

    @Override
    public @NotNull List<BankOffice> getOfficesByBankId(@NotNull String bankId) {
        return this.bankOfficeMap.values()
                .stream()
                .filter(bankOffice -> Objects.equals(bankOffice.getBank().getId(), bankId))
                .toList();
    }

    @Override
    public @NotNull Boolean deleteOffice(@NotNull String officeId) throws DoesNotExistException {
        BankOffice office = this.getOfficeById(officeId);

        for (BankAtm bankAtm : this.atmService.getAtmsByOfficeId(officeId)) {
            this.removeAtm(officeId, bankAtm.getId());
        }
        this.bankService.removeOffice(office.getBank().getId(), officeId);

        return true;
    }

    @Override
    public @NotNull Boolean addAtm(@NotNull String officeId, @NotNull BankAtm bankAtm) throws DoesNotExistException {
        BankOffice office = this.getOfficeById(officeId);
        if (office.getBank() == null) { return false; }

        if (office.getAtmPlacementAvailable()) {
            office.setAtmsCount(office.getAtmsCount() + 1);
            office.getBank().setAtmsCount(office.getBank().getAtmsCount() + 1);

            bankAtm.setBankOffice(office);
            bankAtm.setParentBank(office.getBank());

            try {
                this.depositMoney(officeId, bankAtm.getTotalCash());
            } catch (CannotDepositMoneyException e) {
                return false;
            }

            return true;
        } else {
            System.out.println("Нет возможности уставновить банкомат");
            return false;
        }
    }

    @Override
    public @NotNull Boolean removeAtm(@NotNull String officeId, @NotNull String atmId) throws DoesNotExistException {
        BankOffice office = this.getOfficeById(officeId);
        BankAtm atm = this.atmService.getAtmById(atmId);
        if (office.getBank() == null) { return false; }

        double sum = atm.getTotalCash();
        try {
            if (this.atmService.deleteAtm(atmId) != null) {
                office.setAtmsCount(office.getAtmsCount() - 1);
                office.getBank().setAtmsCount(office.getBank().getAtmsCount() - 1);

                this.withdrawMoney(officeId, sum);

                return true;
            } else {
                return false;
            }
        } catch (NotEnoughMoneyException e) {
            return false;
        }
    }

    @Override
    public @NotNull Boolean addEmployee(@NotNull String officeId, @NotNull Employee employee) throws DoesNotExistException {
        BankOffice office = this.getOfficeById(officeId);

        employee.setOffice(office);
        employee.setBankOfWork(office.getBank());

        if (this.bankService.addEmployee(employee.getBankOfWork().getId(), employee)) {
            office.setEmployeesCount(office.getEmployeesCount() + 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public @NotNull Boolean removeEmployee(@NotNull String officeId, @NotNull String employeeId) throws DoesNotExistException {
        BankOffice office = this.getOfficeById(officeId);
        if (office.getBank() == null) { return false; }

        return this.bankService.removeEmployee(office.getBank().getId(), employeeId);
    }

    @Override
    public void depositMoney(@NotNull String bankOfficeId, double sum) throws DoesNotExistException, CannotDepositMoneyException {
        BankOffice office = this.getOfficeById(bankOfficeId);
        if (office.getBank() == null) { return; }

        if (office.getCashDepositAvailable()) {
            office.setTotalCash(office.getTotalCash() + sum);
            this.bankService.depositMoney(office.getBank().getId(), sum);
        } else {
            throw new CannotDepositMoneyException();
        }
    }

    @Override
    public void withdrawMoney(String bankOfficeId, double sum) throws DoesNotExistException, NotEnoughMoneyException {
        BankOffice office = this.getOfficeById(bankOfficeId);
        if (office.getBank() == null) { return; }

        if (office.getCashWithdrawalAvailable()) {
            if (office.getTotalCash() >= sum) {
                office.setTotalCash(office.getTotalCash() - sum);
                this.bankService.withdrawMoney(office.getBank().getId(), sum);
            } else {
                throw new NotEnoughMoneyException();
            }
        } else {
            System.out.println("В данном офисе нельзя снимать наличные");
        }
    }

    @Override
    public @NotNull String stringRepresentation(@NotNull String officeId) throws DoesNotExistException {
        BankOffice bankOffice = this.getOfficeById(officeId);

        StringBuilder builder = new StringBuilder(bankOffice.toString());


        builder.append("Информация обанкоматах\n");
        for (BankAtm atm : this.atmService.getAtmsByOfficeId(officeId)) {
            builder.append(this.atmService.stringRepresentation(atm.getId())).append("\n");
        }

        builder.append("Информация о работниках\n");
        for (Employee employee : this.employeeService.getEmployeesByOfficeId(officeId)) {
            builder.append(this.employeeService.stringRepresentation(employee.getId())).append("\n");
        }

        builder.append("\n");
        return builder.toString();
    }

    @Override
    public @NotNull Boolean isSuitableForLoan(@NotNull String officeId, double money) throws DoesNotExistException {
        BankOffice bankOffice = this.getOfficeById(officeId);

        if (bankOffice.getOpen() && bankOffice.getApplyingForLoansAvailable() && bankOffice.getTotalCash() > money) {
            return !this.getSuitableForLoanAtms(officeId, money).isEmpty() && !this.getSuitableForLoanEmployees(officeId).isEmpty();
        }

        return false;
    }

    @Override
    public @NotNull List<BankAtm> getSuitableForLoanAtms(@NotNull String officeId, double money) throws DoesNotExistException {
        List<BankAtm> atms = new ArrayList<>();

        for (BankAtm bankAtm : this.atmService.getAtmsByOfficeId(officeId)) {
            if (this.atmService.isSuitableForLoan(bankAtm.getId(), money)) {
                atms.add(bankAtm);
            }
        }

        return atms;
    }

    @Override
    public @NotNull List<Employee> getSuitableForLoanEmployees(@NotNull String officeId) throws DoesNotExistException {
        List<Employee> employees = new ArrayList<>();

        for (Employee employee : this.employeeService.getEmployeesByOfficeId(officeId)) {
            if (this.employeeService.isSuitableForLoan(employee.getId())) {
                employees.add(employee);
            }
        }

        return employees;
    }
}
