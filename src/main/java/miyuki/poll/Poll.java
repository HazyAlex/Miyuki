package miyuki.poll;

public class Poll {
    private final String name;
    private final String[] options;

    Poll(String name, String[] options) {
        this.name = name;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String[] getOptions() {
        return options;
    }
}
