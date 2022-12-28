package tech.reliab.course.anisimov;

import tech.reliab.course.anisimov.entity.*;
import tech.reliab.course.anisimov.entity.internalComponents.OpenStatus;
import tech.reliab.course.anisimov.service.*;
import tech.reliab.course.anisimov.utils.GlobalServiceLocator;

import javax.swing.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.random.RandomGenerator;

public class Main {
    public static void main(String[] args) {
        BankService bankService = GlobalServiceLocator.getInstance().getBankService();
        BankOfficeService bankOfficeService = GlobalServiceLocator.getInstance().getBankOfficeService();
        AtmService atmService = GlobalServiceLocator.getInstance().getAtmService();
        UserService userService = GlobalServiceLocator.getInstance().getUserService();
        EmployeeService employeeService = GlobalServiceLocator.getInstance().getEmployeeService();
        PaymentAccountService paymentAccountService = GlobalServiceLocator.getInstance().getPaymentAccountService();
        CreditAccountService creditAccountService = GlobalServiceLocator.getInstance().getCreditAccountService();

        try {
            bankService.addBank(new Bank("1", "Test 1"));
            bankService.addBank(new Bank("2", "Test 2"));
            bankService.addBank(new Bank("3", "Test 3"));
            bankService.addBank(new Bank("4", "Test 4"));
            bankService.addBank(new Bank("5", "Test 5"));

            for (Bank bank : bankService.getAllBanks()) {
                bankOfficeService.create(new BankOffice(
                        "1" + bank.getId(),
                        "Stub office1 name for bank named " + bank.getName(),
                        "Some address" + bank.getId(),
                        true,
                        true,
                        bank,
                        0,
                        0,
                        true,
                        true,
                        true,
                        RandomGenerator.getDefault().nextDouble(10_000, 100_000),
                        RandomGenerator.getDefault().nextInt()

                ));
                bankOfficeService.create(new BankOffice(
                        "2" + bank.getId(),
                        "Stub office2 name for bank named " + bank.getName(),
                        "Some address" + bank.getId(),
                        true,
                        true,
                        bank,
                        0,
                        0,
                        false,
                        false,
                        true,
                        RandomGenerator.getDefault().nextDouble(10_000, 100_000),
                        RandomGenerator.getDefault().nextInt()

                ));
                bankOfficeService.create(new BankOffice(
                        "3" + bank.getId(),
                        "Stub office3 name for bank named " + bank.getName(),
                        "Some address" + bank.getId(),
                        true,
                        true,
                        bank,
                        0,
                        0,
                        true,
                        false,
                        true,
                        RandomGenerator.getDefault().nextDouble(10_000, 100_000),
                        RandomGenerator.getDefault().nextInt()

                ));
            }

            for (BankOffice office : bankOfficeService.getAllOffices()) {
                atmService.create(new BankAtm(
                        "1" + office.getId(),
                        "Stub atm name1",
                        office,
                        OpenStatus.OPEN,
                        true,
                        office.getBank(),
                        "Default placement",
                        null,
                        true,
                        true,
                        RandomGenerator.getDefault().nextDouble(10_000, 40_000),
                        10_00
                ));
                atmService.create(new BankAtm(
                        "2" + office.getId(),
                        "Stub atm name2",
                        office,
                        OpenStatus.OPEN,
                        true,
                        office.getBank(),
                        "Default placement",
                        null,
                        false,
                        true,
                        RandomGenerator.getDefault().nextDouble(10_000, 40_000),
                        10_00
                ));
            }

            for (BankOffice bankOffice : bankOfficeService.getAllOffices()) {
                employeeService.create(new Employee(
                        "1" + bankOffice.getId(),
                        "Name1",
                        LocalDate.of(2000, 12, 13),
                        "Some position",
                        bankOffice.getBank(),
                        true,
                        bankOffice,
                        true,
                        50_000
                ));
                employeeService.create(new Employee(
                        "2" + bankOffice.getId(),
                        "Name2",
                        LocalDate.of(2000, 12, 13),
                        "Some position",
                        bankOffice.getBank(),
                        true,
                        bankOffice,
                        true,
                        50_000
                ));
                employeeService.create(new Employee(
                        "3" + bankOffice.getId(),
                        "Name3",
                        LocalDate.of(2000, 12, 13),
                        "Some position",
                        bankOffice.getBank(),
                        true,
                        bankOffice,
                        true,
                        50_000
                ));
                employeeService.create(new Employee(
                        "4" + bankOffice.getId(),
                        "Name4",
                        LocalDate.of(2000, 12, 13),
                        "Some position",
                        bankOffice.getBank(),
                        true,
                        bankOffice,
                        true,
                        50_000
                ));
                employeeService.create(new Employee(
                        "5" + bankOffice.getId(),
                        "Name5",
                        LocalDate.of(2000, 12, 13),
                        "Some position",
                        bankOffice.getBank(),
                        true,
                        bankOffice,
                        true,
                        50_000
                ));
            }

            for (Bank bank : bankService.getAllBanks()) {
                userService.create(new User(
                        "1" + bank.getId(),
                        "Name1",
                        LocalDate.of(2001, 5, 4),
                        "Some place of work",
                        bank,
                        0,
                        0
                ));
                userService.create(new User(
                        "2" + bank.getId(),
                        "Name2",
                        LocalDate.of(2001, 5, 4),
                        "Some place of work",
                        bank,
                        0,
                        0
                ));
            }

            for (User user : userService.getAllUsers()) {
                paymentAccountService.create(new PaymentAccount(
                        "1" + user.getId(),
                        user,
                        user.getBank(),
                        10_000
                ));
                paymentAccountService.create(new PaymentAccount(
                        "2" + user.getId(),
                        user,
                        user.getBank(),
                        30_000
                ));
            }

            for (User user : userService.getAllUsers()) {
                creditAccountService.create(new CreditAccount(
                        "1" + user.getId(),
                        user,
                        user.getBank(),
                        LocalDate.of(2022, 4, 1),
                        LocalDate.of(2022, 6, 1),
                        2,
                        20_000.0,
                        10_000.0,
                        1,
                        employeeService.getEmployeeById("1" + user.getId()),
                        paymentAccountService.getAccountById("1" + user.getId())
                ));
                creditAccountService.create(new CreditAccount(
                        "2" + user.getId(),
                        user,
                        user.getBank(),
                        LocalDate.of(2022, 4, 1),
                        LocalDate.of(2022, 6, 1),
                        2,
                        20_000.0,
                        10_000.0,
                        1,
                        employeeService.getEmployeeById("2" + user.getId()),
                        paymentAccountService.getAccountById(2 + user.getId())
                ));
            }

            Scanner in = new Scanner(System.in);
            StringBuilder bankOption = new StringBuilder("Вывод информации о банках\n");
            bankOption.append("Введите айди банка\n");
            bankOption.append("Введите -1 для выхода\n");
            bankOption.append("Доступные айди: ");
            bankService.getAllBanks().forEach(bank -> bankOption.append(bank.getId()).append("  "));

            String inputValue = "";
            while (true) {
                System.out.println(bankOption);
                inputValue = Integer.toString(in.nextInt());
                if (inputValue.equals("-1")) { break; }
                System.out.println(bankService.stringRepresentation(inputValue));
            }

            StringBuilder userOption = new StringBuilder("Вывод информации о пользователях\n");
            userOption.append("Введите айди пользователя\n");
            userOption.append("Введите -1 для выхода\n");
            userOption.append("Доступные айди: ");
            userService.getAllUsers().forEach(user -> userOption.append(user.getId()).append("  "));

            while (true) {
                System.out.println(userOption);
                inputValue = Integer.toString(in.nextInt());
                if (inputValue.equals("-1")) { break; }
                System.out.println(userService.stringRepresentation(inputValue));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String userId = "";
        try {
            Scanner in = new Scanner(System.in);
            System.out.println("Механика взятия кредита");
            System.out.println("Введите айди пользователя, на которого хотите оформить кредит");
            System.out.println("Введите -1 для выхода");
            System.out.print("Айди пользователей в системе: ");
            for (User user : userService.getAllUsers()) {
                System.out.print(user.getId() + " ");
            }
            userId = Integer.toString(in.nextInt());
            System.out.println();

            int creditAccountId = 0;
            while (!userId.equals("-1")) {
                System.out.print("Введите сумму кредита: ");
                double sum = in.nextDouble();
                System.out.print("Введите срок кредита в месяцах: ");
                int monthCount = in.nextInt();

                System.out.print("Айди подхожящих банков: ");
                for (Bank bank : bankService.getSuitableForLoanBanks(sum, monthCount)) {
                    System.out.print(bank.getId() + " ");
                }

                System.out.println("Введите айди банка: ");
                String bankID = Integer.toString(in.nextInt());

                Bank bank = bankService.getBankById(bankID);
                BankOffice bankOffice = bankService.getSuitableForLoanOffices(bankID, sum).get(0);
                Employee employee = bankOfficeService.getSuitableForLoanEmployees(bankOffice.getId()).get(0);
                PaymentAccount paymentAccount = paymentAccountService.getBestAccountForUser(userId);
                CreditAccount creditAccount = creditAccountService.create(new CreditAccount(
                        Integer.toString(creditAccountId),
                        userService.getUserById(userId),
                        bank,
                        LocalDate.now(),
                        LocalDate.now().plusMonths(monthCount),
                        monthCount,
                        sum,
                        1.0,
                        12,
                        employee,
                        paymentAccount
                ));

                System.out.println(creditAccount);
                creditAccountId++;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}