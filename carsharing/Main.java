package carsharing;


import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class Main {


    public static void main(String[] argsArr) {
        List<String> args = Arrays.asList(argsArr);
        String dbName = readArg(args, "-databaseFileName", "carsharing");

        Database database = new Database(dbName);

        CompanyDao companyDao = new CompanyDaoImpl(database);
        companyDao.createTable();

        CarDao carDao = new CarDaoImpl(database);
        carDao.createTable();

        CustomerDao customerDao = new CustomerDaoImpl(database);
        customerDao.createTable();

        DaoContainer daoContainer = new DaoContainer(companyDao, carDao, customerDao);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            out.println("1. Log in as a manager");
            out.println("2. Log in as a customer");
            out.println("3. Create a customer");
            out.println("0. Exit");

            String input = scanner.next();
            out.println();

            switch (input) {
                case "1": mainMenu(companyDao, carDao); break;
                case "2": customerMenu(daoContainer); break;
                case "3": createCustomer(customerDao); break;
                case "0": exit = true; break;
            }
        }
    }

    private static void customerMenu(DaoContainer container) {
        Customer customer = chooseCustomer(container.customers());
        if (customer != null) {
            rentMenu(customer, container);
        }
    }

    private static void rentMenu(Customer customer, DaoContainer container) {
        Scanner scanner = new Scanner(System.in);

        boolean back = false;
        while (!back) {
            out.println("1. Rent a car");
            out.println("2. Return a rented car");
            out.println("3. My rented car");
            out.println("0. Back");

            String input = scanner.next();
            out.println();

            switch (input) {
                case "1": rentCar(customer.getId(), container); break;
                case "2": returnRantedCar(customer.getId(), container.customers()); break;
                case "3": printRentedCar(customer.getId(), container.customers()); break;
                case "0": back = true; break;
            }
        }
    }

    private static void rentCar(int customerId, DaoContainer container) {
        Scanner scanner = new Scanner(System.in);

        CarDetails rentedCar = container.customers().getRentedCar(customerId);
        if (rentedCar != null) {
            out.println("You've already rented a car!\n");
            return;
        }

        Company company = chooseCompany(container.companies());
        if (company == null) {
            return;
        }

        List<Car> cars = container.cars().getAvailableCars(company.getId());
        if (cars.isEmpty()) {
            out.printf("No available cars in the '%s' company.\n", company.getName());
        }

        Car car;
        while (true) {
            out.println("Choose a car:");
            printCars(cars);
            out.println("0. Back");

            Integer id = parseInt(scanner.next());
            out.println();

            if (id != null && id == 0) {
                return;
            }

            if (id == null || id < 1 || id > cars.size()) {
                continue;
            }

            car = cars.get(id - 1);
            break;
        }

        container.customers().rentCar(customerId, car.getId());
        out.printf("You rented '%s'\n", car.getName());
    }

    private static void returnRantedCar(int customerId, CustomerDao customerDao) {
        CarDetails rentedCar = customerDao.getRentedCar(customerId);
        if (rentedCar == null) {
            out.println("You didn't rent a car!\n");
            return;
        }

        out.println("You've returned a rented car!");
        customerDao.returnCar(customerId);
    }

    private static void printRentedCar(int customerId, CustomerDao customerDao) {
        CarDetails rentedCar = customerDao.getRentedCar(customerId);
        if (rentedCar == null) {
            out.println("You didn't rent a car!\n");
            return;
        }

        out.println("Your rented car:");
        out.println(rentedCar.getName());
        out.println("Company:");
        out.println(rentedCar.getCompany());
        out.println();
    }

    private static void createCustomer(CustomerDao customerDao) {
        Scanner scanner = new Scanner(System.in);
        out.println("Enter the customer name:");

        String customerName = scanner.nextLine();
        customerDao.createCustomer(customerName);
        out.println("The customer was added!\n");
    }

    private static Customer chooseCustomer(CustomerDao customerDao) {
        List<Customer> customers = customerDao.getAllCustomers();

        if (customers.isEmpty()) {
            out.println("The customer list is empty!\n");
            return null;
        }

        while (true) {
            out.println("Customer list:");
            for (int i = 0; i < customers.size(); i++) {
                out.printf("%d. %s\n", i + 1, customers.get(i).getName());
            }
            out.println("0. Back");

            Scanner scanner = new Scanner(System.in);
            Integer id = parseInt(scanner.next());
            out.println();

            if (id != null && id == 0) {
                return null;
            }

            if (id == null || id < 1 || id > customers.size()) {
                continue;
            }

            return customers.get(id - 1);
        }
    }

    private static void mainMenu(CompanyDao companyDao, CarDao carDao) {
        Scanner scanner = new Scanner(System.in);

        boolean back = false;
        while (!back) {
            out.println("1. Company list");
            out.println("2. Create a company");
            out.println("0. Back");

            String input = scanner.next();
            out.println();

            switch (input) {
                case "1": editCompany(companyDao, carDao); break;
                case "2": createCompany(companyDao); break;
                case "0": back = true; break;
            }
        }
    }

    private static void editCompany(CompanyDao companyDao, CarDao carDao) {
        Company company = chooseCompany(companyDao);
        if (company != null) {
            companyMenu(carDao, company);
        }
    }

    private static void companyMenu(CarDao carDao, Company company) {
        Scanner scanner = new Scanner(System.in);

        boolean back = false;
        while (!back) {
            out.printf("'%s' company\n", company.getName());

            out.println("1. Car list");
            out.println("2. Create a car");
            out.println("0. Back");

            String input = scanner.next();
            out.println();

            switch (input) {
                case "1": printCars(carDao, company); break;
                case "2": createCar(carDao, company); break;
                case "0": back = true; break;
            }
        }
    }

    private static void createCar(CarDao carDao, Company company) {
        Scanner scanner = new Scanner(System.in);
        out.println("Enter the car name:");

        String carName = scanner.nextLine();
        carDao.createCar(company.getId(), carName);
        out.println("The car was added!\n");
    }

    private static void printCars(CarDao carDao, Company company) {
        List<Car> cars = carDao.getCars(company.getId());
        printCars(cars);
    }

    private static void printCars(List<Car> cars) {
        if (cars.isEmpty()) {
            out.println("The car list is empty!");
        } else {
            for (int i = 0; i < cars.size(); i++) {
                out.printf("%d. %s\n", i + 1, cars.get(i).getName());
            }
        }
        out.println();
    }

    private static Company chooseCompany(CompanyDao db) {
        List<Company> companies = db.getAllCompanies();

        while (true) {
            if (!companies.isEmpty()) {
                out.println("Choose the company:");
                for (int i = 0; i < companies.size(); i++) {
                    out.printf("%d. %s\n", i + 1, companies.get(i).getName());
                }
            } else {
                out.println("The company list is empty");
                return null;
            }

            out.println("0. Back");

            Scanner scanner = new Scanner(System.in);
            Integer id = parseInt(scanner.next());
            out.println();

            if (id != null && id == 0) {
                return null;
            }

            if (id == null || id < 1 || id > companies.size()) {
                continue;
            }

            return companies.get(id - 1);
        }
    }

    private static void createCompany(CompanyDao db) {
        Scanner scanner = new Scanner(System.in);
        out.println("Enter the company name:");

        String companyName = scanner.nextLine();
        db.createCompany(companyName);
        out.println("The company was created!\n");
    }

    private static String readArg(List<String> args, String argName, String defaultValue) {
        int argIndex = args.indexOf(argName);
        if (argIndex > 0) {
            return args.get(argIndex + 1);
        }

        return defaultValue;
    }

    private static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception exc) {
            return null;
        }

    }
}