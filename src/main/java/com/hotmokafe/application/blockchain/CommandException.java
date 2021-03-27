package com.hotmokafe.application.blockchain;

import java.util.Objects;

public class CommandException extends RuntimeException {

    private static final long serialVersionUID = 3026861370427646020L;

    CommandException(Exception wrapped) {
        super(wrapped);

        Objects.requireNonNull(wrapped);
    }

    CommandException(String message) {
        super(message);
    }

    @Override
    public synchronized Exception getCause() {
        return (Exception) super.getCause();
    }
}