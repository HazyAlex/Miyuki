package miyuki.todos;

import miyuki.model.Database;
import miyuki.model.MessageHandler;
import net.dv8tion.jda.core.MessageBuilder;
import org.slf4j.LoggerFactory;

import java.sql.*;

import static miyuki.model.MessageHandler.*;

public class TodoHandler {
    private static final Database database = new Database();

    public static MessageBuilder parse(long author, String message) {
        String[] msg = message.split(" ", 3);
        // 0 -> Command @ignore
        // 1 -> Keyword
        // 2 -> Infer based on context

        if (msg.length < 3)
            return new MessageBuilder(BAD_ARGUMENTS);

        String command     = msg[1];
        String messageBody = msg[2];

        if (messageBody.length() < 3)
            return new MessageBuilder(FEW_ARGUMENTS);

        // Check the command
        switch (command.toLowerCase()) {
            case "list":
                if (messageBody.startsWith("\"") && messageBody.endsWith("\"")) // e.g. !todo list "event"
                    return TodoHandler.list(author, messageBody.substring(1, messageBody.length() - 1));

                return TodoHandler.list(author, messageBody); // e.g. !todo list event

            case "new":
                if (messageBody.startsWith("\"") && messageBody.endsWith("\"")) // e.g. !todo new "event"
                    return TodoHandler.newEvent(author, messageBody.substring(1, messageBody.length() - 1));

                return TodoHandler.newEvent(author, messageBody); // e.g. !todo new event

            case "clear": // e.g. !todo clear event
                if (messageBody.startsWith("\"") && messageBody.endsWith("\"")) // e.g. !todo clear "event"
                    return TodoHandler.clear(author, messageBody.substring(1, messageBody.length() - 1));

                return TodoHandler.clear(author, messageBody);

            case "add":
                String[] eventAndContent;

                if (messageBody.contains("\"")) { // e.g. !todo add "event" line to add

                    eventAndContent = messageBody.split("\"", 3);
                    // 0 => Empty, 1 => Between "" (event), 2 => Content (with an empty space preceding)

                    if (eventAndContent.length < 3 || eventAndContent[2].length() < 2)
                        return new MessageBuilder(FEW_ARGUMENTS);

                    return TodoHandler.addLine(author, eventAndContent[1], eventAndContent[2].substring(1));
                }

                // e.g. !todo add event line to add
                eventAndContent = messageBody.split(" ", 2);

                if (eventAndContent.length < 2)
                    return new MessageBuilder(CANT_PARSE);

                return TodoHandler.addLine(author, eventAndContent[0], eventAndContent[1]);

            case "mark":
                String[] eventAndPosition;
                if (messageBody.contains("\"")) { // e.g. !todo add "event" line to add

                    eventAndPosition = messageBody.split("\"", 3);
                    // 0 => Empty, 1 => Between "" (event), 2 => Content (with an empty space preceding)

                    if (eventAndPosition.length < 3 || eventAndPosition[2].length() < 2)
                        return new MessageBuilder(FEW_ARGUMENTS);

                    return TodoHandler.mark(author, eventAndPosition[1], eventAndPosition[2].substring(1));
                }

                eventAndPosition = messageBody.split(" ", 2); // e.g. !todo mark event 3

                if (eventAndPosition.length < 2)
                    return new MessageBuilder(CANT_PARSE);

                if (!eventAndPosition[1].matches("\\d"))
                    return new MessageBuilder(MessageHandler.POSITION_NOT_NUMBER);

                return TodoHandler.mark(author, eventAndPosition[0], eventAndPosition[1]);

            case "remove":
                if (messageBody.contains("\"")) { // !todo remove "event" 2
                    eventAndContent = messageBody.split("\"", 3);
                    // 0 => Empty, 1 => Between "" (event), 2 => Content (with an empty space preceding)

                    if (eventAndContent.length < 3 || eventAndContent[2].length() < 2) // Will print out the todo list
                        return TodoHandler.remove(author, eventAndContent[1], "");

                    return TodoHandler.remove(author, eventAndContent[1], eventAndContent[2].substring(1));
                }

                // e.g. !todo remove event 2
                eventAndPosition = messageBody.split(" ", 2);

                if (eventAndPosition.length < 2)
                    return TodoHandler.remove(author, eventAndPosition[0], "");

                if (!eventAndPosition[1].matches("\\d"))
                    return new MessageBuilder(MessageHandler.POSITION_NOT_NUMBER);

                return TodoHandler.remove(author, eventAndPosition[0], eventAndPosition[1]);

            default:
                return new MessageBuilder(BAD_ARGUMENTS);
        }
    }


    private static MessageBuilder newEvent(long author, String event) {

        Connection connection = database.startConnection();
        MessageBuilder returnMessage = new MessageBuilder();

        switch (database.isEventPresent(connection, author, event)) {
            case NOT_PRESENT:
                break;
            case REPEATED_EVENT:
                return returnMessage.append(REPEATED_EVENT);
            default:
                return returnMessage.append(CANT_PARSE);
        }

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO todos(author,event,sequence,state,content) VALUES (?, ?, ?, ?::states, ?)"
            );

            ps.setLong  (1, author);
            ps.setString(2, event);
            ps.setShort (3, (short)0);
            ps.setString(4, null);
            ps.setString(5, null);

            returnMessage.append( ps.execute() ?
                    COULD_NOT_CREATE_EVENT :
                    CREATED_SUCCESSFULLY
            );
            connection.commit();

        } catch (SQLException e) {
            LoggerFactory.getLogger(TodoHandler.class).error(e.toString() + '\n' + author + '\n' + event);
            returnMessage.append(CANT_PARSE);
        }

        database.close(connection);
        return returnMessage;
    }


    private static MessageBuilder list(long author, String event) {
        Connection connection = database.startConnection();
        MessageBuilder returnMessage = new MessageBuilder();

        try {
            ResultSet results = database.selectEvent(connection, author, event);

            boolean recordsFound = false;
            boolean emptyEvent   = false;

            while (results.next()) {

                short todoSequence       = results.getShort ("sequence");
                String todoStateAsString = results.getString("state");
                String todoContent       = results.getString("content");

                if (todoStateAsString == null || todoSequence == 0) {
                    emptyEvent = true;
                    continue;
                }

                States todoState = States.valueOf(todoStateAsString);

                returnMessage.append(todoSequence).append(" - ")
                        .append(todoState.name()).append(" - ")
                        .append(todoContent).append('\n');

                recordsFound = true;
                emptyEvent   = false;
            }

            if (emptyEvent)
                return returnMessage.setContent(EMPTY_EVENT);

            if (!recordsFound)
                return returnMessage.setContent(NO_EVENT_FOUND);

        } catch (SQLException e) {
            LoggerFactory.getLogger(TodoHandler.class).error(e.toString() + '\n' + author + '\n' + event);
            return returnMessage.setContent(CANT_PARSE);
        }

        return returnMessage;
    }


    private static MessageBuilder addLine(long author, String event, String content) {
        Connection connection = database.startConnection();
        MessageBuilder returnMessage = new MessageBuilder();

        switch (database.isEventPresent(connection, author, event)) {
            case REPEATED_EVENT:
                break;
            case NOT_PRESENT:
                return returnMessage.append(NOT_PRESENT);
            default:
                return returnMessage.append(CANT_PARSE);
        }

        try {
            // Check if event exists and get the largest sequence
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT MAX(sequence) AS \"sequence\" FROM todos WHERE author = ? AND LOWER(event) = ?"
            );

            ps.setLong  (1, author);
            ps.setString(2, event.toLowerCase());

            ResultSet results = ps.executeQuery();

            if (!results.next())
                return returnMessage.append(NO_EVENT_FOUND);

            short largestSequence = results.getShort("sequence");
            largestSequence += 1;

            // Insert the new line with the contents provided by arguments
            ps = connection.prepareStatement(
                    "INSERT INTO todos(author, event, sequence, state, content) VALUES (?, ?, ?, ?::states, ?)"
            );

            ps.setLong  (1, author);
            ps.setString(2, event);
            ps.setShort (3, largestSequence);
            ps.setString(4, States.TODO.name());
            ps.setString(5, content);

            returnMessage.append( ps.execute() ?
                    COULD_NOT_ADD_LINE :
                    ADDED_LINE_EVENT
            );
            connection.commit();

        } catch (SQLException e) {
            LoggerFactory.getLogger(TodoHandler.class).error(e.toString() + '\n' + author + '\n' + event);
            returnMessage.append(CANT_PARSE);
        }

        database.close(connection);
        return returnMessage;
    }


    private static MessageBuilder mark(long author, String event, String positionAsString) {
        short position = parsePosition(positionAsString);
        if (position == -1)
            return new MessageBuilder(POSITION_NOT_NUMBER);

        Connection connection = database.startConnection();
        MessageBuilder returnMessage = new MessageBuilder();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE todos SET state = ?::states WHERE author = ? AND LOWER(event) = ? AND sequence = ?"
            );

            ps.setString(1, States.DONE.name());
            ps.setLong  (2, author);
            ps.setString(3, event.toLowerCase());
            ps.setShort (4, position);

            returnMessage.append( ps.executeUpdate() > 0 ?
                    MARKED_EVENT :
                    COULD_NOT_MARK_EVENT
            );
            connection.commit();

        } catch (SQLException e) {
            LoggerFactory.getLogger(TodoHandler.class).error(e.toString() + '\n' + author + '\n' + event);
            returnMessage.append(CANT_PARSE);
        }

        database.close(connection);
        return returnMessage;
    }

    private static MessageBuilder remove(long author, String event, String positionAsString) {
        if (positionAsString.isEmpty())
            return TodoHandler.list(author, event);

        short position = parsePosition(positionAsString);
        if (position == -1)
            return new MessageBuilder(POSITION_NOT_NUMBER);

        Connection connection = database.startConnection();
        MessageBuilder returnMessage = new MessageBuilder();

        // Parse the content, e.g. 2
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM todos WHERE author = ? AND LOWER(event) = ? AND sequence = ?"
            );

            ps.setLong  (1, author);
            ps.setString(2, event.toLowerCase());
            ps.setShort (3, position);

            returnMessage.append( ps.executeUpdate() > 0 ?
                    REMOVED_LINE :
                    COULD_NOT_REMOVE_LINE
            );

            // Reindex the "sequence" because the user can delete any index
            database.reIndexSequence(connection, author, event);
            connection.commit();

        } catch (SQLException e) {
            LoggerFactory.getLogger(TodoHandler.class).error(e.toString() + '\n' + author + '\n' + event);
            returnMessage.append(CANT_PARSE);
        }

        database.close(connection);
        return returnMessage;
    }

    private static MessageBuilder clear(long author, String event) {
        Connection connection = database.startConnection();
        MessageBuilder returnMessage = new MessageBuilder();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM todos WHERE author = ? AND LOWER(event) = ?"
            );

            ps.setLong  (1, author);
            ps.setString(2, event.toLowerCase());

            returnMessage.append( ps.executeUpdate() > 0 ?
                    CLEARED_EVENT :
                    COULD_NOT_CLEAR_EVENT
            );
            connection.commit();

        } catch (SQLException e) {
            LoggerFactory.getLogger(TodoHandler.class).error(e.toString() + '\n' + author + '\n' + event);
            returnMessage.append(CANT_PARSE);
        }

        database.close(connection);
        return returnMessage;
    }

    private static short parsePosition(String positionAsString) {
        try {
            return Short.parseShort(positionAsString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
