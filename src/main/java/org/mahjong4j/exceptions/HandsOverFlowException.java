package org.mahjong4j.exceptions;

/**
 * @author yu1ro
 */
public class HandsOverFlowException extends Mahjong4jException {
    public HandsOverFlowException() {
        super("多牌です");
    }
}
