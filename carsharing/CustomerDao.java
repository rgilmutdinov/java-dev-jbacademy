package carsharing;

import java.util.List;

public interface CustomerDao extends Dao {
    void createCustomer(String name);

    List<Customer> getAllCustomers();

    void returnCar(int customerId);

    CarDetails getRentedCar(int customerId);

    void rentCar(int customerId, int carId);
}
