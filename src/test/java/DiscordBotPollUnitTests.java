import org.junit.Assert;
import org.junit.Test;
import miyuki.poll.Poll;
import miyuki.poll.PollHandler;

import static miyuki.model.MessageHandler.*;

public class DiscordBotPollUnitTests {

    @Test
    public void takesInvalidArguments() {
        String error = catchError(null);
        Assert.assertEquals("Can't accept null", CANT_PARSE, error);

        String name = catchError("!poll");
        Assert.assertEquals("A Poll needs a name", NEED_NAME, name);

        String options = catchError("!poll NAME");
        Assert.assertEquals("A Poll needs options", NEED_OPTIONS, options);
    }

    @Test
    public void checkSeparator() {
        String badOpts = catchError("!poll NAME {}");
        Assert.assertEquals("A Poll needs options", NEED_OPTIONS, badOpts);

        String badOpts2 = catchError("!poll NAME []");
        Assert.assertEquals("A Poll needs options", NEED_OPTIONS, badOpts2);

        String badOpts3 = catchError("!poll NAME ");
        Assert.assertEquals("A Poll needs options", NEED_OPTIONS, badOpts3);

        String badOpts4 = catchError("!poll NAME {:}");
        Assert.assertEquals("Invalid options", NEED_OPTIONS, badOpts4);

        String badOpts5 = catchError("!poll NAME [;]");
        Assert.assertEquals("Invalid options", NEED_OPTIONS, badOpts5);

        String badOpts6 = catchError("!poll NAME [Y\\N]");
        Assert.assertEquals("Invalid options", NEED_OPTIONS, badOpts6);

        String badOpts7 = catchError("!poll NAME [Y&N]");
        Assert.assertEquals("Invalid options", NEED_OPTIONS, badOpts7);
    }

    @Test
    public void checkValidName() {
        Poll poll = PollHandler.parse("!poll NAME [Y;N]");
        Assert.assertEquals("The Poll needs to be valid (NAME)", "NAME", poll.getName());

        Poll poll2 = PollHandler.parse("!poll \"Hello I am\" [Y;N]");
        Assert.assertEquals("The Poll needs to be valid (NAME)", "Hello I am", poll2.getName());
    }

    @Test
    public void checkValidOptions() {
        Poll poll        = PollHandler.parse("!poll NAME [Y;N]");
        String[] options = poll.getOptions();

        Assert.assertEquals("The Poll needs to be valid (OPTIONS)", "Y", options[0]);
        Assert.assertEquals("The Poll needs to be valid (OPTIONS)", "N", options[1]);


        Poll poll2 = PollHandler.parse("!poll \"Hello I am\" [Y;N]");
        String[] options2 = poll2.getOptions();

        Assert.assertEquals("The Poll needs to be valid (OPTIONS)", "Y", options2[0]);
        Assert.assertEquals("The Poll needs to be valid (OPTIONS)", "N", options2[1]);
    }

    private static String catchError(String messageToSend) {
        try {
            Poll poll = PollHandler.parse(messageToSend);
            throw new RuntimeException("An exception was expected, got: " + poll.toString());
        } catch (PollHandler.PollHandlerError error) {
            return error.getMessage();
        }
    }
}
