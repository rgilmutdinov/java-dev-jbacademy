package carsharing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarDaoImpl extends DaoBase implements CarDao {

    public CarDaoImpl(Database database) {
        super(database);
    }

    @Override
    public List<Car> getCars(int companyId) {
        List<Car> cars = new ArrayList<>();

        String selectSql = "SELECT * FROM CAR WHERE COMPANY_ID = ?";
        try (PreparedStatement statement = database.prepareStatement(selectSql)) {
            statement.setInt(1, companyId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Car car = new Car(
                    rs.getInt("ID"),
                    rs.getString("NAME")
                );
                cars.add(car);
            }

            return cars;
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        }
    }

    @Override
    public void createCar(int companyId, String carName) {
        String insertSql = "INSERT INTO CAR(NAME, COMPANY_ID) VALUES(?, ?)";
        try (PreparedStatement statement = database.prepareStatement(insertSql)) {
            statement.setString(1, carName);
            statement.setInt(2, companyId);
            statement.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        }
    }

    @Override
    public CarDetails getCar(int carId) {
        String query =
            "SELECT CAR.id AS ID, CAR.NAME as NAME, COMPANY.NAME AS COMPANY \n" +
            "FROM CAR \n" +
            "INNER JOIN company \n" +
            "    ON CAR.COMPANY_ID = COMPANY.ID \n" +
            "WHERE CAR.ID = ?;";

        try (PreparedStatement statement = database.prepareStatement(query)) {
            statement.setInt(1, carId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return new CarDetails(
                    rs.getInt("ID"),
                    rs.getString("NAME"),
                    rs.getString("COMPANY")
                );
            }

            return null;
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        }
    }

    @Override
    public List<Car> getAvailableCars() {
        String query =
            "SELECT CAR.ID AS ID, CAR.NAME AS NAME, CUSTOMER.NAME \n" +
            "FROM CAR \n" +
            "LEFT JOIN CUSTOMER \n" +
            "   ON CAR.ID = CUSTOMER.RENTED_CAR_ID \n" +
            "WHERE CUSTOMER.NAME IS NULL;";

        return database.getList(query, rs -> new Car(rs.getInt("ID"), rs.getString("NAME")));
    }

    @Override
    public List<Car> getAvailableCars(int companyId) {
        String query =
            "SELECT CAR.ID AS ID, CAR.NAME AS NAME, CUSTOMER.NAME \n" +
            "FROM CAR \n" +
            "LEFT JOIN CUSTOMER \n" +
            "   ON CAR.ID = CUSTOMER.RENTED_CAR_ID \n" +
            "WHERE CUSTOMER.NAME IS NULL AND CAR.COMPANY_ID = ?;";

        List<Car> cars = new ArrayList<>();
        try (PreparedStatement statement = database.prepareStatement(query)) {
            statement.setInt(1, companyId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                cars.add(new Car(
                    rs.getInt("ID"),
                    rs.getString("NAME")
                ));
            }

            return cars;
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        }
    }

    @Override
    protected String createTableStatement() {
        return
            //"DROP TABLE IF EXISTS CAR CASCADE;\n" +

            "CREATE TABLE IF NOT EXISTS CAR(" +
            " ID INT AUTO_INCREMENT PRIMARY KEY," +
            " NAME VARCHAR(255) NOT NULL UNIQUE," +
            " COMPANY_ID INT NOT NULL," +
            " FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));";
    }
}
