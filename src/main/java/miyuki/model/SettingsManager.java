package miyuki.model;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SettingsManager {
    public static final int INVALID_TOKEN  = 10;

    private String token;

    public SettingsManager() {
        try (BufferedReader br = new BufferedReader(new FileReader("settings/token.txt"))){
            token = br.readLine();
        } catch (IOException e) {
            LoggerFactory.getLogger(SettingsManager.class).error("Please add a token in: settings/token.txt");
            System.exit(INVALID_TOKEN);
        }
    }

    @NotNull
    public static StringBuilder getCommandList() {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader("settings/command_list.md"))){
            br.lines().forEach(string -> builder.append(string).append('\n'));
        } catch (IOException e) {
            LoggerFactory.getLogger(SettingsManager.class).error("Please add the command list to: settings/command_list.md");
        }

        return builder;
    }

    public String getToken() {
        return token;
    }
}
