package getthepicture.copybook.compiler.core.lexer;

import getthepicture.cobol.core.AreaT;

public final class Token {
    private final TokenType type;
    private final String value;
    private final int lineNumber;
    private final AreaT area;

    public Token(TokenType type, String value, int lineNumber, AreaT area) {
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
        this.area = area;
    }

    public TokenType getType() { return type; }
    public String getValue() { return value; }
    public int getLineNumber() { return lineNumber; }
    public AreaT getArea() { return area; }

    @Override
    public String toString() {
        return type + ": '" + value + "' (Line " + lineNumber + ")";
    }
}

