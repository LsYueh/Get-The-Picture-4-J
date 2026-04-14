package getthepicture.copybook.compiler.core.lexer;

import java.util.ArrayList;
import java.util.List;

import getthepicture.cobol.CobolLine;
import getthepicture.cobol.core.AreaT;

public class Lexer {
    private final List<CobolLine> lines;
    private int pos = 0;

    public Lexer() {
        this(null);
    }

    public Lexer(List<CobolLine> lines) {
        this.lines = lines;
    }

    public List<Token> tokenize() {
        if (lines == null) throw new IllegalStateException("CobolLine not provided.");

        List<Token> allTokens = new ArrayList<>();

        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            CobolLine l = lines.get(lineNumber);

            // Column 7 是延續符號
            if (l.getIndicator() == '-') {
                allTokens.add(new Token(TokenType.HYPHEN, String.valueOf(l.getIndicator()), lineNumber, l.getArea()));
            }

            allTokens.addAll(tokenize(l.getText(), l.getLineNumber(), l.getArea()));
        }

        return allTokens;
    }

    public List<Token> tokenize(String line, int lineNumber, AreaT area) {
        pos = 0;
        List<Token> tokens = new ArrayList<>();

        while (pos < line.length()) {
            char current = line.charAt(pos);

            // Floating comment: *>（最優先）
            if (pos + 1 < line.length() && line.charAt(pos) == '*' && line.charAt(pos + 1) == '>') {
                String comment = line.substring(pos + 2).trim();
                tokens.add(new Token(TokenType.COMMENT, comment, lineNumber, area));
                break; // ⚠️ 結束 while（本行）
            }

            if (isWordChar(current)) {
                tokens.add(scanWord(line, lineNumber, area));
                continue;
            }

            // Alphanumeric Literal (String Literal)
            if (current == '\'') {
                tokens.add(scanAlphanumericLiteral(line, lineNumber, area, '\''));
                continue;
            }

            // Alphanumeric Literal (String Literal)
            if (current == '"') {
                tokens.add(scanAlphanumericLiteral(line, lineNumber, area, '"'));
                continue;
            }

            if (current == '(') {
                tokens.add(new Token(TokenType.L_PAREN, String.valueOf(line.charAt(pos++)), lineNumber, area));
                continue;
            }

            if (current == ')') {
                tokens.add(new Token(TokenType.R_PAREN, String.valueOf(line.charAt(pos++)), lineNumber, area));
                continue;
            }

            if (current == '.') {
                tokens.add(new Token(TokenType.DOT, String.valueOf(line.charAt(pos++)), lineNumber, area));
                continue;
            }

            if (Character.isWhitespace(current)) {
                pos++;
                continue;
            }

            // Fallback: Unknown char
            tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(line.charAt(pos++)), lineNumber, area));
        }

        return tokens;
    }

    // ----------------------------
    // Scanners
    // ----------------------------

    /**
     * 掃描單詞 token，包括： <br>
     * - Reserved Word (如 PIC, VALUE, OCCURS) <br>
     * - Alphanumeric Literal (識別字/名稱) <br>
     * - Numeric Literal <br>
     */
    private Token scanWord(String line, int lineNumber, AreaT area) {
        int start = pos;

        while (pos < line.length() && isWordChar(line.charAt(pos)))
            pos++;

        String word = line.substring(start, pos);
        return classifyWord(word, lineNumber, area);
    }

    private Token scanAlphanumericLiteral(String line, int lineNumber, AreaT area, char quoteChar) {
        int start = pos;
        pos++; // skip opening quote

        while (pos < line.length()) {
            if (line.charAt(pos) == quoteChar) {
                // COBOL 雙引號內的兩個 quote 表示內部 quote
                if (pos + 1 < line.length() && line.charAt(pos + 1) == quoteChar) {
                    pos += 2;
                    continue;
                } else {
                    pos++; // closing quote
                    break;
                }
            } else {
                pos++;
            }
        }

        // 即使沒有閉合也會生成 Token
        String tokenText = line.substring(start, pos);
        return new Token(TokenType.ALPHANUMERIC_LITERAL, tokenText, lineNumber, area);
    }

    // ----------------------------
    // Helpers
    // ----------------------------

    private static Token classifyWord(String word, int lineNumber, AreaT area) {
        // NumericLiteral
        if (isNumeric(word))
            return new Token(TokenType.NUMERIC_LITERAL, word, lineNumber, area);

        // Reserved Word or Alphanumeric Literal
        return switch (word) {
            case "PICTURE",
                 "PIC"              -> new Token(TokenType.PICTURE,         word, lineNumber, area);
            case "USAGE"            -> new Token(TokenType.USAGE,           word, lineNumber, area);
            case "DISPLAY"          -> new Token(TokenType.DISPLAY,         word, lineNumber, area);
            case "COMPUTATIONAL",
                 "COMP"             -> new Token(TokenType.COMP,            word, lineNumber, area);
            case "COMPUTATIONAL-1",
                 "COMP-1"           -> new Token(TokenType.COMP_1,          word, lineNumber, area);
            case "COMPUTATIONAL-2",
                 "COMP-2"           -> new Token(TokenType.COMP_2,          word, lineNumber, area);
            case "COMPUTATIONAL-3",
                 "COMP-3"           -> new Token(TokenType.COMP_3,          word, lineNumber, area);
            case "COMPUTATIONAL-4",
                 "COMP-4"           -> new Token(TokenType.COMP_4,          word, lineNumber, area);
            case "COMPUTATIONAL-5",
                 "COMP-5"           -> new Token(TokenType.COMP_5,          word, lineNumber, area);
            case "COMPUTATIONAL-6",
                 "COMP-6"           -> new Token(TokenType.COMP_6,          word, lineNumber, area);
            case "BINARY"           -> new Token(TokenType.BINARY,          word, lineNumber, area);
            case "PACKED-DECIMAL"   -> new Token(TokenType.PACKED_DECIMAL,  word, lineNumber, area);
            case "VALUE"            -> new Token(TokenType.VALUE,           word, lineNumber, area);
            case "VALUES"           -> new Token(TokenType.VALUES,          word, lineNumber, area);
            case "THROUGH",
                 "THRU"             -> new Token(TokenType.THROUGH,         word, lineNumber, area);
            case "SPACE",
                 "SPACES"           -> new Token(TokenType.SPACE,           word, lineNumber, area);
            case "ZERO",
                 "ZEROS",
                 "ZEROES"           -> new Token(TokenType.ZERO,            word, lineNumber, area);
            case "REDEFINES"        -> new Token(TokenType.REDEFINES,       word, lineNumber, area);
            case "RENAMES"          -> new Token(TokenType.RENAMES,         word, lineNumber, area);
            case "OCCURS"           -> new Token(TokenType.OCCURS,          word, lineNumber, area);
            case "TIMES"            -> new Token(TokenType.TIMES,           word, lineNumber, area);
            case "FILLER"           -> new Token(TokenType.FILLER,          word, lineNumber, area);
            default                 -> new Token(TokenType.ALPHANUMERIC_LITERAL, word, lineNumber, area);
        };
    }

    private static boolean isWordChar(char c) {
        return Character.isLetterOrDigit(c) || c == '-';
    }

    private static boolean isNumeric(String word) {
        if (word == null || word.isEmpty()) return false;
        for (char c : word.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
