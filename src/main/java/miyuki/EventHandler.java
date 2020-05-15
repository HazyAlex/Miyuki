package miyuki;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;

public class EventHandler extends ListenerAdapter {
    private final Miyuki miyuki;

    EventHandler() {
        this.miyuki = new Miyuki();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event == null || !event.getMessage().getContentRaw().startsWith("!") || event.getAuthor().isBot())
            return;

        // !todo Events
        if (event.getMessage().getContentRaw().startsWith("!todo")) {
            miyuki.parseTODO(event);
            return;
        }

        // !poll Events
        if (event.getMessage().getContentRaw().startsWith("!poll")) {
            miyuki.parsePoll(event);
            return;
        }

        // More general commands
        switch (event.getMessage().getContentRaw().toLowerCase())
        {
            case "!ping":
                miyuki.replyPong(event);
                break;

            case "!hello":
                miyuki.greeting(event);
                break;

            case "!help":
                miyuki.commandList(event);
                break;

            default:
                LoggerFactory.getLogger(EventHandler.class).debug("Couldn't parse: " +
                        event.getMessage().getContentRaw() + "\nFrom: " + event.getAuthor().getName()
                );
                break;
        }
    }
}
