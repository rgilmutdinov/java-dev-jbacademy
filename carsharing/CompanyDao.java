package carsharing;

import java.util.List;

public interface CompanyDao extends Dao {
    List<Company> getAllCompanies();

    void createCompany(String name);
}