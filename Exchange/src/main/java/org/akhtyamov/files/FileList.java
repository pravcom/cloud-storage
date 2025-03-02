package org.akhtyamov.files;

import org.akhtyamov.Commands;
import org.akhtyamov.MessageExchange;

import java.util.List;

public class FileList implements MessageExchange {
    private final List<String> list;

    public FileList(List<String> list) {
        this.list = list;
    }

    @Override
    public Commands getType() {
        return Commands.LIST_FILE;
    }

    @Override
    public Object getMessage() {
        return list;
    }
}
