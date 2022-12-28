package carsharing;

import java.util.List;

public interface CarDao extends Dao {
    List<Car> getCars(int companyId);

    void createCar(int companyId, String carName);

    CarDetails getCar(int carId);

    List<Car> getAvailableCars();
    List<Car> getAvailableCars(int companyId);
}
