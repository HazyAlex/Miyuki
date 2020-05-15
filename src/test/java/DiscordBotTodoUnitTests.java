import org.junit.Assert;
import org.junit.Test;
import miyuki.todos.TodoHandler;

import static miyuki.model.MessageHandler.*;

public class DiscordBotTodoUnitTests {
    private static final long TESTING_AUTHOR = -1L;

    @Test
    public void test01_ParseFewArguments() {
        String fewArgs = TodoHandler.parse(
                TESTING_AUTHOR, "!todo"
        ).getStringBuilder().toString();

        Assert.assertEquals("Called with few arguments.", BAD_ARGUMENTS, fewArgs);

        String badCommand = TodoHandler.parse(
                TESTING_AUTHOR, "!todo $wa$wd&%"
        ).getStringBuilder().toString();

        Assert.assertEquals("Called with bad command/parameters.", BAD_ARGUMENTS, badCommand);
    }

    @Test
    public void test01_NewEvent() {
        badAndEmptyArgs("new");

        String newEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo new \"event\""
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE            , newEvent);
        Assert.assertNotEquals("The event is repeated!"              , REPEATED_EVENT        , newEvent);
        Assert.assertNotEquals("The event was not created!"          , COULD_NOT_CREATE_EVENT, newEvent);
        Assert.assertEquals   ("The event has to be created!"        , CREATED_SUCCESSFULLY  , newEvent);

        String newRepeatedEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo new event"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE            , newRepeatedEvent);
        Assert.assertNotEquals("The event was not created!"          , COULD_NOT_CREATE_EVENT, newRepeatedEvent);
        Assert.assertNotEquals("The event has to be created!"        , CREATED_SUCCESSFULLY  , newRepeatedEvent);
        Assert.assertEquals   ("The event is repeated!"              , REPEATED_EVENT        , newRepeatedEvent);

        clearEvent();
    }

    @Test
    public void test02_AddLine() {
        badAndEmptyArgs("add");
        createEvent();


        String addLineBadArgs = TodoHandler.parse(
                TESTING_AUTHOR, "!todo add "
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE        , addLineBadArgs);
        Assert.assertNotEquals("The line was not added to the event!", COULD_NOT_ADD_LINE, addLineBadArgs);
        Assert.assertNotEquals("The line can't be added!"            , ADDED_LINE_EVENT  , addLineBadArgs);
        Assert.assertEquals   ("The event doesn't exist!"            , FEW_ARGUMENTS     , addLineBadArgs);

        String addLineNonExistingEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo add \"hello\" Water the plants"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE        , addLineNonExistingEvent);
        Assert.assertNotEquals("The line was not added to the event!", COULD_NOT_ADD_LINE, addLineNonExistingEvent);
        Assert.assertNotEquals("The line can't be added!"            , ADDED_LINE_EVENT  , addLineNonExistingEvent);
        Assert.assertEquals   ("The event doesn't exist!"            , NOT_PRESENT       , addLineNonExistingEvent);

        String addLineInvalidEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo add \"\" Water the plants"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE        , addLineInvalidEvent);
        Assert.assertNotEquals("The line was not added to the event!", COULD_NOT_ADD_LINE, addLineInvalidEvent);
        Assert.assertNotEquals("The line can't be added!"            , ADDED_LINE_EVENT  , addLineInvalidEvent);
        Assert.assertEquals   ("The event doesn't exist!"            , NOT_PRESENT       , addLineInvalidEvent);

        clearEvent();
    }

    @Test
    public void test03_List() {
        badAndEmptyArgs("list");

        String listNonExistentEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo list hello"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE    , listNonExistentEvent);
        Assert.assertNotEquals("The event must be empty!"            , EMPTY_EVENT   , listNonExistentEvent);
        Assert.assertEquals   ("The event wasn't found!"             , NO_EVENT_FOUND, listNonExistentEvent);


        createEvent(); // Create empty event

        String listEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo list event"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE    , listEvent);
        Assert.assertNotEquals("The event wasn't found!"             , NO_EVENT_FOUND, listEvent);
        Assert.assertEquals   ("The event must be empty!"            , EMPTY_EVENT   , listEvent);

        String listEventQuotes = TodoHandler.parse(
                TESTING_AUTHOR, "!todo list \"event\""
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE    , listEventQuotes);
        Assert.assertNotEquals("The event wasn't found!"             , NO_EVENT_FOUND, listEventQuotes);
        Assert.assertEquals   ("The event must be empty!"            , EMPTY_EVENT   , listEventQuotes);

        clearEvent(); // Clear the event
    }

    @Test
    public void test04_Mark() {
        badAndEmptyArgs("mark");

        String markNonExistentEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo mark hello"
        ).getStringBuilder().toString();

        Assert.assertEquals("No position given!", CANT_PARSE, markNonExistentEvent);


        createEvent();
        addLineEvent();

        String markEventBadPos = TodoHandler.parse(
                TESTING_AUTHOR, "!todo mark event pos"
        ).getStringBuilder().toString();

        Assert.assertEquals("The position has to be a number!", POSITION_NOT_NUMBER, markEventBadPos);

        String markEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo mark event 1"
        ).getStringBuilder().toString();

        Assert.assertEquals("The line needs to be marked!", MARKED_EVENT, markEvent);

        clearEvent();
        createEvent();
        addLineEvent();

        String markEventQuotes = TodoHandler.parse(
                TESTING_AUTHOR, "!todo mark \"event\" 1"
        ).getStringBuilder().toString();

        Assert.assertEquals("The line needs to be marked!", MARKED_EVENT, markEventQuotes);

        clearEvent();
    }

    @Test
    public void test05_Remove() {
        badAndEmptyArgs("remove");

        createEvent();
        addLineEvent();

        String removeLine = TodoHandler.parse(
                TESTING_AUTHOR, "!todo remove event 1"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE           , removeLine);
        Assert.assertNotEquals("The line wasn't removed correctly!"  , COULD_NOT_REMOVE_LINE, removeLine);
        Assert.assertEquals   ("The line has to be removed!"         , REMOVED_LINE         , removeLine);

        clearEvent();
        createEvent();
        addLineEvent();

        String removeLineQuotes = TodoHandler.parse(
                TESTING_AUTHOR, "!todo remove \"event\" 1"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE           , removeLineQuotes);
        Assert.assertNotEquals("The line wasn't removed correctly!"  , COULD_NOT_REMOVE_LINE, removeLineQuotes);
        Assert.assertEquals   ("The line has to be removed!"         , REMOVED_LINE         , removeLineQuotes);

        clearEvent();
    }

    @Test
    public void test06_Clear() {
        badAndEmptyArgs("clear");
        createEvent();

        String removeEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo clear event"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE           , removeEvent);
        Assert.assertNotEquals("The event wasn't removed correctly!" , COULD_NOT_CLEAR_EVENT, removeEvent);
        Assert.assertEquals   ("The event has to be removed!"        , CLEARED_EVENT        , removeEvent);

        createEvent();

        String removeEventQuotes = TodoHandler.parse(
                TESTING_AUTHOR, "!todo clear \"event\""
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE           , removeEventQuotes);
        Assert.assertNotEquals("The event wasn't removed correctly!" , COULD_NOT_CLEAR_EVENT, removeEventQuotes);
        Assert.assertEquals   ("The event has to be removed!"        , CLEARED_EVENT        , removeEventQuotes);
    }

    private void badAndEmptyArgs(String command) {
        String badArgs = TodoHandler.parse(
                TESTING_AUTHOR, "!todo " + command
        ).getStringBuilder().toString();

        Assert.assertEquals("Called without event.", BAD_ARGUMENTS, badArgs);

        String emptyArgs = TodoHandler.parse(
                TESTING_AUTHOR, "!todo " + command + " "
        ).getStringBuilder().toString();

        Assert.assertEquals("Called with empty event.", FEW_ARGUMENTS, emptyArgs);
    }

    private void createEvent() {
        TodoHandler.parse(TESTING_AUTHOR, "!todo new event");
    }

    private void clearEvent()  {
        TodoHandler.parse(TESTING_AUTHOR, "!todo clear event");
    }

    private void addLineEvent() {
        TodoHandler.parse(TESTING_AUTHOR, "!todo add event Water the plants");
    }
}
