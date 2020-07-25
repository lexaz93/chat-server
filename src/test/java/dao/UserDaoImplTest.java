package dao;

import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserDaoImplTest {
    UserDao userDao = new UserDaoImpl();

    @Test
    public void shouldCorrectFindByName() {
        User expectedUser = new User("test", "12345");
        userDao.createUser(expectedUser);
        final User actualUser = userDao.findByName("test");
        userDao.deleteUser(expectedUser);

        Assertions.assertEquals(expectedUser, actualUser);
    }
}
