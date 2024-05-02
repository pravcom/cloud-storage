package org.akhtyamov.files;

import org.akhtyamov.Commands;
import org.akhtyamov.MessageExchange;

import java.io.File;

public class FileName implements MessageExchange {
    private File message;
    @Override
    public Commands getType() {
        return Commands.GET_CURRENT_FILE;
    }

    @Override
    public Object getMessage() {
        return message;
    }

    public FileName(File message) {
        this.message = message;
    }
}
