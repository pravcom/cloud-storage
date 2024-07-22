package org.akhtyamov;

public class Action implements MessageExchange{
    private final String filePath;
    private final Commands command;

    public Action(String filePath, Commands command) {
        this.filePath = filePath;
        this.command = command;
    }

    @Override
    public Commands getType() {
        return command;
    }

    @Override
    public Object getMessage() {
        return filePath;
    }
}
