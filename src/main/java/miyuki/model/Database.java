package miyuki.model;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static miyuki.model.MessageHandler.*;

public class Database  {
    private static final int ERROR_COULD_NOT_GET_CONNECTION     = 1;
    private static final int ERROR_COULD_NOT_CLOSE_CONNECTION   = 2;
    private static final int ERROR_COULD_NOT_FIND_DB_ENV_FILE   = 3;
    private static final int ERROR_COULD_NOT_FIND_DB_ENV_DRIVER = 4;

    private static final String databaseURL;
    private static final String databaseUser;
    private static final String databasePass;

    static {
        Properties properties = null;

        try {
            properties = new Properties();
            FileInputStream in = new FileInputStream("settings/db.properties");
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LoggerFactory.getLogger(Database.class).error(e.toString());
            System.exit(ERROR_COULD_NOT_FIND_DB_ENV_FILE);
        }

        try {
            String driver = properties.getProperty("jdbc.driver");
            if (driver != null) {
                Class.forName(driver);
            }
        } catch (ClassNotFoundException e) {
            LoggerFactory.getLogger(Database.class).error(e.toString());
            System.exit(ERROR_COULD_NOT_FIND_DB_ENV_DRIVER);
        }

        databaseURL = properties.getProperty("jdbc.url");
        databaseUser = properties.getProperty("jdbc.username");
        databasePass = properties.getProperty("jdbc.password");
    }

    public Connection startConnection() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(
                    databaseURL, databaseUser, databasePass
            );
            conn.setAutoCommit(false);

        } catch (SQLException e) {
            LoggerFactory.getLogger(Database.class).error(e.toString());
            System.exit(ERROR_COULD_NOT_GET_CONNECTION);
        }

        return conn;
    }

    public void close(@NotNull Connection connection) {
        try {
            connection.close();

        } catch (SQLException e) {
            LoggerFactory.getLogger(Database.class).error(e.toString());
            System.exit(ERROR_COULD_NOT_CLOSE_CONNECTION);
        }
    }

    /**
     * @param connection A connection from <code>IDatabase.startConnection()</code>
     * @param author The event's author
     * @param event The event's name
     * @return A <code>ResultSet</code> with the resulting event's information
     * @throws SQLException If a database access error occurs.<br>
     * If this method is called on a closed <code>PreparedStatement</code> or the SQL statement
     * does not return a <code>ResultSet</code> object
     */
    public ResultSet selectEvent(@NotNull Connection connection, long author, @NotNull String event) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM todos WHERE author = ? AND LOWER(event) = ? ORDER BY sequence"
        );

        ps.setLong  (1, author);
        ps.setString(2, event.toLowerCase());

        return ps.executeQuery();
    }

    /**
     * @param connection A connection from <code>IDatabase.startConnection()</code>
     * @param author The event's author
     * @param event The event's name
     *
     * @apiNote Re-indexes the 'sequence' field in the database
     */
    public void reIndexSequence(@NotNull Connection connection, long author, String event) {
        try {
            ResultSet rs = selectEvent(connection, author, event);

            short sequence = 0;
            while (rs.next()) {
                long id = rs.getLong("id");

                PreparedStatement update = connection.prepareStatement(
                        "UPDATE todos SET sequence = ? WHERE id = ?"
                );

                update.setShort(1, sequence);
                update.setLong (2, id);
                update.execute();

                sequence += 1;
            }
        } catch (SQLException e) {
            LoggerFactory.getLogger(Database.class).error(e.toString() + '\n' + author + '\n' + event);
        }
    }

    /**
     * @param connection A connection from <code>IDatabase.startConnection()</code>
     * @param author The event's author
     * @param event The event's name
     * @return REPEATED_EVENT if an event already exists<br>
     *         CANT'T PARSE   if there's an SQLException<br>
     *         NOT_PRESENT    if the Event doesn't exist
     */
    public String isEventPresent(@NotNull Connection connection, long author, String event) {
        try {
            ResultSet results = selectEvent(connection, author, event);

            while (results.next()) {
                if (results.getString("state") == null)
                    return REPEATED_EVENT;
            }

        } catch (SQLException e) {
            LoggerFactory.getLogger(Database.class).error(e.toString() + '\n' + author + '\n' + event);
            return CANT_PARSE;
        }

        return NOT_PRESENT;
    }
}
