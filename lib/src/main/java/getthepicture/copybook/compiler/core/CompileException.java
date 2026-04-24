package getthepicture.copybook.compiler.core;

import getthepicture.copybook.compiler.core.lexer.Token;

public final class CompileException extends RuntimeException {
    private final Token token;

    public CompileException(String message) {
        this(message, null);
    }

    public CompileException(String message, Token token) {
        super(formatMessage(message, token));
        this.token = token;
    }

    public Token getToken() { return token; }

    private static String formatMessage(String message, Token token) {
        if (token == null)
            return message;
        
        return message + " (line " + token.getLineNumber()
                + ", token " + token.getType()
                + ", value '" + token.getValue() + "')";
    }
}
