package sqlibrary.connection;

import java.util.List;

public interface DAOInterface<T> {

    int insert(T model);
    boolean delete(T model);
    boolean update(T model);
    T selectById(T model);
    T selectByName(String name);
    List<T> selectAll();

}
