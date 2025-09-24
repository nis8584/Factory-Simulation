package factory.communication.message;

public class SetConcurrencyMessage implements Message{
    private final int count;

    public SetConcurrencyMessage(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
