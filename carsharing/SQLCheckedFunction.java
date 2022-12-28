package carsharing;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLCheckedFunction<T, R> {
    R apply(T t) throws SQLException;
}
