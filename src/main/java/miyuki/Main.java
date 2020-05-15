package miyuki;

import miyuki.model.SettingsManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) {
        SettingsManager settingsManager = new SettingsManager();

        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT)
                .setToken(settingsManager.getToken())
                .addEventListener(new EventHandler())
                .setGame(Game.playing(Miyuki.STATUS_MESSAGE));

        try {
            jdaBuilder.build();
        } catch (LoginException e) {
            LoggerFactory.getLogger(Main.class).error(e.toString());
            System.exit(SettingsManager.INVALID_TOKEN);
        }
    }
}
