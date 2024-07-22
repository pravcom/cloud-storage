package org.akhtyamov.files;

import org.akhtyamov.Commands;
import org.akhtyamov.MessageExchange;

public class PartFile implements MessageExchange {

    private final byte[] message;
    private final String filename;

    public PartFile(byte[] message, String filename) {
        this.message = message;
        this.filename = filename;
    }

    @Override
    public Commands getType() {
        return Commands.FILE;
    }

    @Override
    public Object getMessage() {
         return message;
    }

    public String getFilename() {
        return filename;
    }
}
