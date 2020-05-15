package miyuki;

import miyuki.model.Database;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import miyuki.model.SettingsManager;
import miyuki.poll.Poll;
import miyuki.poll.PollHandler;
import miyuki.poll.PollHandler.PollHandlerError;
import miyuki.todos.TodoHandler;

import java.util.LinkedList;
import java.util.concurrent.*;

class Miyuki {
    public  static final String STATUS_MESSAGE  = "Use !help to talk to me \uD83C\uDF75";
    private static final String GREETING        = "Hello!\nI'm a bot made by HazyAlex.\nType !help to see the command list.";
    private static final String PING_REPLY      = "Pong!";

    // !hello
    void greeting(@NotNull MessageReceivedEvent event) {
        event.getChannel().sendMessage(GREETING).queue();
    }

    // !ping
    void replyPong(@NotNull MessageReceivedEvent event) {
        event.getChannel().sendMessage(PING_REPLY).queue();
    }

    // !help
    void commandList(@NotNull MessageReceivedEvent event) {

        MessageBuilder message = new MessageBuilder(
                SettingsManager.getCommandList()
        );

        PrivateChannel privateChannel = event.getAuthor().openPrivateChannel().complete();
        message.sendTo(privateChannel).queue();
    }

    void parseTODO(@NotNull MessageReceivedEvent event) {

        // Parse the message
        MessageBuilder msgBuilder = TodoHandler.parse(
                event.getMessage().getAuthor().getIdLong(), // Author
                event.getMessage().getContentRaw()          // Message
        );

        // Send the results back
        msgBuilder.sendTo(event.getChannel()).queue();
    }

    void parsePoll(@NotNull MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        Poll poll;
        try {
            poll = PollHandler.parse(event.getMessage().getContentRaw());
        } catch (PollHandlerError error) {
            channel.sendMessage(error.getMessage()).queue();
            return;
        }

        // ```Poll: NAME```
        new MessageBuilder("```css\n[Poll]: ").append(poll.getName()).append("```").sendTo(channel).submit();

        LinkedList<Message> messageList = new LinkedList<>();
        for (String option : poll.getOptions()) {

            // Send the messages, when they complete save them in the list to check the results later on
            new MessageBuilder(option).sendTo(channel).submit().whenComplete(((message, throwable) ->
                    messageList.add(message)
            ));
        }

        Runnable checkResults = () -> {
            int largestReactionCount = 0;
            String largestReactionName = null;
            boolean tie = false;

            for (Message message : messageList) {

                // Update the counts
                Message updatedMessage = channel.getMessageById(message.getId()).complete();

                // Count reactions
                for (MessageReaction reaction : updatedMessage.getReactions()) {
                    if (reaction.getCount() == largestReactionCount)
                        tie = true;

                    if (reaction.getCount() > largestReactionCount) {
                        largestReactionCount = reaction.getCount();
                        largestReactionName  = updatedMessage.getContentRaw();
                        tie = false;
                    }
                }
            }

            if (largestReactionName == null) {
                channel.sendMessage(PollHandler.NO_RESULT_FOUND).queue();
                return;
            }

            if (tie) {
                channel.sendMessage(PollHandler.TIE).queue();
                return;
            }

            MessageBuilder winner = new MessageBuilder("The winner is: ").append(largestReactionName)
                    .append("\nWith: ").append(largestReactionCount).append(" vote(s).");

            winner.sendTo(channel).queue();
        };

        // Wait for POLL_WAIT_DELAY seconds and then check the results
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(checkResults, PollHandler.POLL_WAIT_DELAY, TimeUnit.SECONDS);
        scheduler.shutdown();
    }
}
