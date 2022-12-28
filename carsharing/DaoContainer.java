package carsharing;

public class DaoContainer {
    private final CompanyDao companyDao;

    private final CarDao carDao;
    private final CustomerDao customerDao;

    public DaoContainer(CompanyDao companyDao, CarDao carDao, CustomerDao customerDao) {

        this.companyDao = companyDao;
        this.carDao = carDao;
        this.customerDao = customerDao;
    }

    public CompanyDao companies() {
        return companyDao;
    }

    public CarDao cars() {
        return carDao;
    }

    public CustomerDao customers() {
        return customerDao;
    }
}
