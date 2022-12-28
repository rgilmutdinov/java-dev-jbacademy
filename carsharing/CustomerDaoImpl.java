package carsharing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomerDaoImpl extends DaoBase implements CustomerDao {
    public CustomerDaoImpl(Database database) {
        super(database);
    }

    @Override
    public void createCustomer(String name) {
        String insertSql = "INSERT INTO CUSTOMER(NAME) VALUES(?)";
        try (PreparedStatement statement = database.prepareStatement(insertSql)) {
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        String selectSql = "SELECT * FROM CUSTOMER;";
        return database.getList(selectSql, (rs) -> new Customer(
                rs.getInt("ID"),
                rs.getString("NAME"),
                rs.getInt("RENTED_CAR_ID")));
    }

    @Override
    public void returnCar(int customerId) {
        String updateSql = "UPDATE CUSTOMER SET RENTED_CAR_ID = NULL WHERE CUSTOMER.ID = ?";
        try (PreparedStatement statement = database.prepareStatement(updateSql)) {
            statement.setInt(1, customerId);
            statement.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        }
    }

    @Override
    public void rentCar(int customerId, int carId) {
        String updateSql = "UPDATE CUSTOMER SET RENTED_CAR_ID = ? WHERE CUSTOMER.ID = ?";
        try (PreparedStatement statement = database.prepareStatement(updateSql)) {
            statement.setInt(1, carId);
            statement.setInt(2, customerId);
            statement.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        }
    }

    @Override
    public CarDetails getRentedCar(int customerId) {
        String query =
                "SELECT CAR.ID AS ID, CAR.NAME AS NAME, COMPANY.NAME AS COMPANY \n" +
                "FROM CUSTOMER \n" +
                "JOIN CAR ON CUSTOMER.RENTED_CAR_ID = CAR.ID \n" +
                "JOIN COMPANY ON CAR.COMPANY_ID = COMPANY.ID \n" +
                "WHERE CUSTOMER.ID = ?;";

        try (PreparedStatement statement = database.prepareStatement(query)) {
            statement.setInt(1, customerId);
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
    protected String createTableStatement() {
        return
            //"DROP TABLE IF EXISTS CUSTOMER CASCADE;\n" +

            "CREATE TABLE IF NOT EXISTS CUSTOMER(" +
            " ID INT AUTO_INCREMENT PRIMARY KEY," +
            " NAME VARCHAR(255) NOT NULL UNIQUE," +
            " RENTED_CAR_ID INT," +
            " FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID));";
    }
}
