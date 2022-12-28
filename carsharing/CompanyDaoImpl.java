package carsharing;

import java.sql.*;
import java.util.List;

public class CompanyDaoImpl extends DaoBase implements CompanyDao {
    public CompanyDaoImpl(Database database) {
        super(database);
    }

    @Override
    public List<Company> getAllCompanies() {
        String selectSql = "SELECT * FROM COMPANY;";
        return database.getList(selectSql, (rs) -> new Company(
            rs.getInt("ID"),
            rs.getString("NAME")
        ));
    }

    @Override
    public void createCompany(String name) {
        String insertSql = "INSERT INTO COMPANY(NAME) VALUES(?)";
        try (PreparedStatement statement = database.prepareStatement(insertSql)) {
            statement.setString(1, name);
            statement.executeUpdate();
        } catch (SQLException sqlEx) {
            throw new RuntimeException(sqlEx);
        }
    }

    @Override
    protected String createTableStatement() {
        return
            //"DROP TABLE IF EXISTS COMPANY CASCADE;\n" +

            "CREATE TABLE IF NOT EXISTS COMPANY(" +
            " ID INT AUTO_INCREMENT PRIMARY KEY," +
            " NAME VARCHAR(255) NOT NULL UNIQUE);";
    }
}
