package miyuki.poll;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static miyuki.model.MessageHandler.*;

public class PollHandler {
    public static class PollHandlerError extends RuntimeException {
        private final String message;

        PollHandlerError(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public static final int    POLL_WAIT_DELAY = 30;
    public static final String NO_RESULT_FOUND = "No one voted!";
    public static final String TIE             = "There was a tie!";

    @NotNull
    public static Poll parse(String receivedMsg) {
        if (receivedMsg == null)
            throw new PollHandlerError(CANT_PARSE);

        String[] splitMessage;

        String pollName;
        String opt;

        boolean hasQuotes = false;
        if (receivedMsg.contains("\"")) {
            splitMessage = receivedMsg.split("\"", 3);
            // 0 => !poll (with a space after), 1 => pollName, 2 => Options (with an empty space preceding)

            hasQuotes = true;
        } else {
            splitMessage = receivedMsg.split(" ", 3);
            // 0 => !poll, 1 => pollName, 2 => Options
        }

        if (splitMessage.length < 2)
            throw new PollHandlerError(NEED_NAME);

        if (splitMessage.length < 3)
            throw new PollHandlerError(NEED_OPTIONS);


        pollName = splitMessage[1];
        if (hasQuotes)
            opt = splitMessage[2].strip(); // Remove the empty space after the regex
        else
            opt = splitMessage[2];


        if ((opt.contains("{") && opt.contains("}")) || (opt.contains("[") && opt.contains("]") )) {
            String optionsWithSeparator = parseOuterCharacter(opt);
            String[] options = parseSeparator(optionsWithSeparator);

            return new Poll(pollName, options);
        }

        // !poll Name YES;NO
        // !poll Name YES,NO

        String[] options = parseSeparator(opt.strip());
        return new Poll(pollName, options);
    }
    
    private static String parseOuterCharacter(@NotNull String enclosedOptions) {
        if (enclosedOptions.strip().length() < 2)
            throw new PollHandlerError(NEED_OPTIONS);

        String options = enclosedOptions.substring(1, enclosedOptions.length() - 1).strip();

        if (options.length() < 1) // e.g. !poll NAME {            } is invalid.
            throw new PollHandlerError(NEED_OPTIONS);

        return options;
    }

    // Y,N  => [Y, N]
    // Y:N  => [Y, N]
    // Y;N  => [Y, N]
    // Y.N  => [Y. N]
    @Nullable
    private static String @NotNull [] parseSeparator(@NotNull String options) {
        String[] separated = null;

        if (options.contains(";"))
            separated = options.split(";");

        if (options.contains(","))
            separated = options.split(",");

        if (options.contains(":"))
            separated = options.split(":");

        if (options.contains("."))
            separated = options.split("\\.");

        if (separated == null || options.length() < 2)
            throw new PollHandlerError(NEED_OPTIONS);

        return separated;
    }
}
