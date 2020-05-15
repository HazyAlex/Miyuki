import miyuki.model.Database;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseIntegrationTest {
    private final Database database;
    private static final String[] testData = new String[]{
            "INSERT INTO todos(author, event, sequence, state, content) VALUES (-1, 'bday', 0, null, null);",
            "INSERT INTO todos(author, event, sequence, state, content)VALUES(-1, 'bday', 1, 'TODO'::states, 'First line');",
            "INSERT INTO todos(author, event, sequence, state, content) VALUES (-1,'bday',2,'TODO'::states,'Second line');",
            "INSERT INTO todos(author, event, sequence, state, content) VALUES (-1,'bday',3,'TODO'::states,'Third line');",
            "INSERT INTO todos(author, event, sequence, state, content) VALUES (-1,'bday',4,'TODO'::states,'Fourth line');"
    };

    public DatabaseIntegrationTest() {
        database = new Database();
    }

    @Test
    public void test01_checkDatabase() throws SQLException {
        Connection connection = database.startConnection();

        Assert.assertFalse("The database isn't available!", connection.isClosed());
        Assert.assertTrue ("The connection has been closed and is not valid.", connection.isValid(0));

        database.close(connection);
    }

    @Test
    public void test02_insertTestValues() throws SQLException {
        Connection connection = database.startConnection();

        PreparedStatement ps = connection.prepareStatement(String.join("", testData));
        ps.execute();
        connection.commit();

        ps = connection.prepareStatement("SELECT COUNT(*) FROM todos WHERE author = -1");
        ResultSet rs = ps.executeQuery();
        rs.next();

        Assert.assertEquals("Failed inserting the test values.", testData.length, rs.getLong("count"));

        database.close(connection);
    }

    @Test
    public void test03_cleanDatabase() throws SQLException {
        Connection connection = database.startConnection();

        PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM todos WHERE author = -1"
        );
        ps.execute();
        connection.commit();

        database.close(connection);
    }
}
