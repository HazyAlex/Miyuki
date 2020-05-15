import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import miyuki.todos.TodoHandler;
import org.junit.Assert;
import org.junit.Test;

import static miyuki.model.MessageHandler.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DiscordBotIntegrationTests {
    private static final long TESTING_AUTHOR = -1L;

    @Test
    public void test01_CreateNewEvent() {
        String newEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo new event"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE            , newEvent);
        Assert.assertNotEquals("The event is repeated!"              , REPEATED_EVENT        , newEvent);
        Assert.assertNotEquals("The event was not created!"          , COULD_NOT_CREATE_EVENT, newEvent);
        Assert.assertEquals   ("The event has to be created!"        , CREATED_SUCCESSFULLY  , newEvent);
    }

    @Test
    public void test02_AddNewLine() {
        String addLine = TodoHandler.parse(
                TESTING_AUTHOR, "!todo add event Water the plants"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE        , addLine);
        Assert.assertNotEquals("The event wasn't found!"             , NO_EVENT_FOUND    , addLine);
        Assert.assertNotEquals("The line was not added to the event!", COULD_NOT_ADD_LINE, addLine);
        Assert.assertEquals   ("The line has to be added!"           , ADDED_LINE_EVENT  , addLine);
    }
    // 1 - todo - Water the plants

    @Test
    public void test03_ListEvent() {
        String listEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo list event"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!"  , CANT_PARSE    , listEvent);
        Assert.assertNotEquals("The event wasn't found!"               , NO_EVENT_FOUND, listEvent);
        Assert.assertNotEquals("The event is empty!"                   , EMPTY_EVENT   , listEvent);
        Assert.assertTrue     ("The event must contain the added line!", listEvent.contains("TODO - Water the plants"));
    }

    @Test
    public void test04_MarkEvent() {
        String markEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo mark event 1"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE          , markEvent);
        Assert.assertNotEquals("The line wasn't marked correctly!"   , COULD_NOT_MARK_EVENT, markEvent);
        Assert.assertEquals   ("The line has to be marked!"          , MARKED_EVENT        , markEvent);
    }
    // 1 - DONE - Water the plants

    @Test
    public void test05_ListEvent() { // Check if it was marked correctly.
        String listEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo list event"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!"  , CANT_PARSE    , listEvent);
        Assert.assertNotEquals("The event wasn't found!"               , NO_EVENT_FOUND, listEvent);
        Assert.assertNotEquals("The event is empty!"                   , EMPTY_EVENT   , listEvent);
        Assert.assertTrue     ("The event must contain the added line!", listEvent.contains("DONE - Water the plants"));
    }

    @Test
    public void test06_AddNewLine() {
        String addLine = TodoHandler.parse(
                TESTING_AUTHOR, "!todo add event Water the plants"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE        , addLine);
        Assert.assertNotEquals("The event wasn't found!"             , NO_EVENT_FOUND    , addLine);
        Assert.assertNotEquals("The line was not added to the event!", COULD_NOT_ADD_LINE, addLine);
        Assert.assertEquals   ("The line has to be added!"           , ADDED_LINE_EVENT  , addLine);
    }
    // 1 - DONE - Water the plants
    // 2 - todo - Water the plants

    @Test
    public void test07_RemoveLine() {
        String removeLine = TodoHandler.parse(
                TESTING_AUTHOR, "!todo remove event 1"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE           , removeLine);
        Assert.assertNotEquals("The line wasn't removed correctly!"  , COULD_NOT_REMOVE_LINE, removeLine);
        Assert.assertEquals   ("The line has to be removed!"         , REMOVED_LINE         , removeLine);
    }
    // 1 - todo - Water the plants

    @Test
    public void test08_ListEvent() { // Check if the line was removed.
        String listEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo list event"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!"  , CANT_PARSE    , listEvent);
        Assert.assertNotEquals("The event wasn't found!"               , NO_EVENT_FOUND, listEvent);
        Assert.assertNotEquals("The event is empty!"                   , EMPTY_EVENT   , listEvent);
        Assert.assertTrue     ("The event must contain the added line!", listEvent.contains("TODO - Water the plants"));
    }

    @Test
    public void test09_ClearEvent() {
        String clearedEvent = TodoHandler.parse(
                TESTING_AUTHOR, "!todo clear event"
        ).getStringBuilder().toString();

        Assert.assertNotEquals("The message wasn't parsed correctly!", CANT_PARSE           , clearedEvent);
        Assert.assertNotEquals("The event wasn't cleared correctly!" , COULD_NOT_CLEAR_EVENT, clearedEvent);
        Assert.assertEquals   ("The event has to be cleared!"        , CLEARED_EVENT        , clearedEvent);
    }
}
