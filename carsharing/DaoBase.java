package carsharing;

public abstract class DaoBase implements Dao {
    protected Database database;

    public DaoBase(Database database) {
        this.database = database;
    }

    public void createTable() {
        database.executeUpdate(createTableStatement());
    }

    protected abstract String createTableStatement();
}
