package miyuki.model;

public final class MessageHandler {
    public static final String REPEATED_EVENT         = "I'm sorry! You already have an event with the same name created!";
    public static final String CANT_PARSE             = "I don't understand what you want!";
    public static final String COULD_NOT_CREATE_EVENT = "I'm sorry! I couldn't create your event!";
    public static final String CREATED_SUCCESSFULLY   = "Your event was created successfully!";
    public static final String NO_EVENT_FOUND         = "I didn't find any event with that name.\n";
    public static final String COULD_NOT_CLEAR_EVENT  = "I'm sorry, something went wrong!\nI failed to clear this event!";
    public static final String CLEARED_EVENT          = "Alright!\nYour event was cleared!";
    public static final String COULD_NOT_ADD_LINE     = "I'm sorry. I couldn't add the line to your event.";
    public static final String ADDED_LINE_EVENT       = "Okay! I added it to the list.";
    public static final String COULD_NOT_MARK_EVENT   = "I'm sorry, I couldn't change the event!";
    public static final String MARKED_EVENT           = "I marked this TODO as done!";
    public static final String REMOVED_LINE           = "Removed the line successfully!";
    public static final String COULD_NOT_REMOVE_LINE  = "I couldn't remove this line! I'm sorry!";
    public static final String POSITION_NOT_NUMBER    = "The position must be a number!";
    public static final String NOT_PRESENT            = "The event doesn't exist!";
    public static final String EMPTY_EVENT            = "The event doesn't have any lines in it!";
    public static final String BAD_ARGUMENTS          = "I don't know what you want!\nType !help for the command list.";
    public static final String FEW_ARGUMENTS          = "I don't know what you want!\nAn event must have at least 3 characters.";

    public static final String NEED_NAME              = "You need to name to your poll!";
    public static final String NEED_OPTIONS           = "You need to give your poll at least one option!";
}
