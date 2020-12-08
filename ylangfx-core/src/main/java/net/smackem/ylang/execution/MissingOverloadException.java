package net.smackem.ylang.execution;

public class MissingOverloadException extends Exception {
    private static final long serialVersionUID = 1230455435L;

    public MissingOverloadException(String message) {
        super(message);
    }
}
