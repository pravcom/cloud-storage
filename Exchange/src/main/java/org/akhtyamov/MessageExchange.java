package org.akhtyamov;

import java.io.Serializable;

public interface MessageExchange extends Serializable {
    Commands getType();
    Object getMessage();

}
