package carsharing;

public class CarDetails extends Car {
    private final String company;

    public CarDetails(int id, String name, String company) {
        super(id, name);
        this.company = company;
    }

    public String getCompany() {
        return company;
    }
}
