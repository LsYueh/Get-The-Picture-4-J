package getthepicture.copybook.compiler.core.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.CompileException;
import getthepicture.copybook.compiler.core.lexer.Token;
import getthepicture.copybook.compiler.core.lexer.TokenType;
import getthepicture.copybook.compiler.core.parser.layout.CbLayout;
import getthepicture.copybook.compiler.core.parser.layout.Item.Condition88Item;
import getthepicture.copybook.compiler.core.parser.layout.Item.ElementaryDataItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.GroupItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.RedefinesItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.Renames66Item;
import getthepicture.copybook.compiler.core.parser.layout.core.DataItem;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;

public class Parser {
    private final List<Token> tokens;
    private int position = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ----------------------------
    // Pointer
    // ----------------------------

    private Token previous = null;

    public Token getPrevious() { return previous; }

    private Token current() {
        return position < tokens.size() ? tokens.get(position) : null;
    }

    private Token lookahead(int n) {
        int idx = position + n;
        return (idx >= 0 && idx < tokens.size()) ? tokens.get(idx) : null;
    }

    // ----------------------------
    // Operations
    // ----------------------------

    private Token consume() {
        Token cur = current();
        if (cur == null) throw new IllegalStateException("Cannot consume EOF.");
        previous = cur;
        return tokens.get(position++);
    }

    private Token expect(TokenType type) {
        Token cur = current();
        if (cur == null)
            throw new CompileException("Expected type: '" + type + "' but got end of input");
        if (cur.getType() != type)
            throw new CompileException("Expected type: '" + type + "' but got '" + cur.getType() + "'");
        return consume();
    }

    // ----------------------------
    // Syntactic / Semantic Analysis
    // ----------------------------

    /**
     * Syntactic / Semantic Analysis
     */
    public CbLayout analyze() {
        CbLayout root = new CbLayout();
        while (current() != null) {
            parseDataItem(root, 0);
        }
        return root;
    }

    // ----------------------------
    // COBOL Data Item Parsers/Helpers
    // ----------------------------

    /**
     * Recursive Descent Parser (Top-down Parser).
     */
    private void parseDataItem(DataItem parent, int previousLevel) {
        // 遞迴終止
        if (current() == null) return;

        // 解析
        DataItem item = parseSingleDataItem();

        // 位置順序限制：重新定義的項目必須緊接在被重新定義項目的描述之後。
        if (item instanceof RedefinesItem redefines) {
            // 同級別限制
            DataItem target = parent.getChildren().stream()
                    .filter(e -> e.getName().equals(redefines.getTargetName()))
                    .findFirst().orElse(null);

            if (target == null)
                throw new CompileException(
                    "Cannot resolve REDEFINES target '" + redefines.getTargetName()
                    + "' for '" + redefines.getName() + "'.");

            if (target instanceof Renames66Item)
                throw new CompileException(
                    "Cannot redefine 66-level item '" + redefines.getTargetName()
                    + "' with '" + redefines.getName() + "'.");

            if (target instanceof Condition88Item)
                throw new CompileException(
                    "Cannot redefine 88-level item '" + redefines.getTargetName()
                    + "' with '" + redefines.getName() + "'.");

            redefines.setTarget(target);
        }

        // 加入 parent 的 Subordinates / 88 處理
        if (parent instanceof ElementaryDataItem e) {
            if (item instanceof Condition88Item c88) {
                e.addCondition(c88);
            } else {
                throw new CompileException(
                    "Elementary data item cannot have subordinates.",
                    current() != null ? current() : previous);
            }
        } else if (parent instanceof RedefinesItem r) {
            r.addSubordinate(item);
        } else if (parent instanceof GroupItem g) {
            g.addSubordinate(item);
        }

        // 過濾可遞迴的子項

        DataItem currentItem = (item instanceof RedefinesItem
                             || item instanceof GroupItem
                             || item instanceof ElementaryDataItem
                             || item instanceof Renames66Item)
                ? item : null;

        if (currentItem != null) {
            AreaT currentArea  = currentItem.getArea();
            int   currentLevel = currentItem.getLevel();

            while (isNextDataItemStart()) {
                AreaT nextArea  = current().getArea();
                int   nextLevel = Integer.parseInt(current().getValue());

                // Note: Break 是離開遞迴 (退回上一層)
                //       ParseDataItem 是繼續處理下一個 DataItem 或進入下一層

                // 從 Area B 出去到 Area A (或是非 Area B)
                if (currentArea == AreaT.B && nextArea != AreaT.B) break;

                if (nextLevel == 66 || currentLevel == 66) {
                    parseDataItem(parent, nextLevel); break;
                }

                if (nextLevel <= currentLevel) break;

                parseDataItem(currentItem, nextLevel);
            }
        }
    }

    /**
     * 解析單一 COBOL Data Item <br>
     * 根據是否出現 PIC 子句判定為 ElementaryDataItem 或 GroupItem <br>
     * 並處理其附屬的子句（如 OCCURS、VALUE） <br>
     */
    private DataItem parseSingleDataItem() {
        // [ Tokens ]
        //     │
        //     ▼
        // [ DataItem Header ]
        //     │
        //     ▼
        // [ Clauses Collection ]
        //  (PIC / VALUE / OCCURS)
        //     │
        //     ▼
        // [ (Build DataItem) ]
        //     ├─ Lv 1 ~ 49 ──► ElementaryDataItem / GroupItem
        //     ├─ Lv 66 ──► Renames66Item
        //     ├─ Lv 77 ──► [Unsupported]
        //     └─ Lv 88 ──► Condition88Item
        
        // Lv 1 ~ 49

        PictureMeta  pic      = null;
        String       value    = null;
        Integer      occurs   = null;
        List<String> comments = new ArrayList<>();

        // REDEFINES

        String targetName  = null;

        // Level 66

        String lv66From    = "";
        String lv66Through = null;

        // Level 88

        List<Object> lv88Values  = null;
        Object       lv88Through = null;

        // ----------------------------

        collectComments(comments); // 行首 comment（很少，但合法）

        DataItemHeader header = parseDataItemHeader();
        AreaT   area     = header.area;
        int     level    = header.level;
        String  name     = header.name;
        boolean isFiller = header.isFiller;

        while (current() != null && current().getType() != TokenType.DOT) {
            switch (current().getType()) {
                case PICTURE -> pic = parsePicClause();
                case OCCURS  -> occurs = parseOccursClause();
                case REDEFINES -> targetName = parseRedefinesClause();
                case RENAMES -> {
                    if (level != 66)
                        throw new CompileException(
                            "'RENAMES' clause is only valid for level 66 items.",
                            current() != null ? current() : previous);
                    var lv66 = parseLv66RenamesClause();
                    lv66From    = lv66.from;
                    lv66Through = lv66.thru;
                }
                case VALUE -> {
                    if (level == 88) {
                        var lv88 = parseLv88ValuesClause();
                        lv88Values  = lv88.values;
                        lv88Through = lv88.through;
                    } else {
                        value = parseValueClause();
                    }
                }
                case VALUES -> {
                    if (level != 88)
                        throw new CompileException(
                            "'VALUES' clause is only valid for level 88 items.",
                            current() != null ? current() : previous);
                    var lv88 = parseLv88ValuesClause();
                    lv88Values  = lv88.values;
                    lv88Through = lv88.through;
                }
                default -> throw new CompileException(
                    "Invalid or unsupported clause after data item '" + name + "'.",
                    current() != null ? current() : previous);
            }
        }

        expect(TokenType.DOT);
        collectComments(comments);

        String comment = comments.isEmpty() ? null : String.join(", ", comments);

        // Build Full DataItem
        if (level >= 1 && level <= 49) {
            if (targetName != null)
                return new RedefinesItem(area, level, name, targetName, comment);
            if (pic == null)
                return new GroupItem(area, level, name, occurs, isFiller, comment);
            return new ElementaryDataItem(area, level, name, pic, occurs, value, isFiller, comment);
        } else if (level == 66) {
            return new Renames66Item(area, name, lv66From, lv66Through, comment);
        } else if (level == 88) {
            return new Condition88Item(area, name, lv88Values, lv88Through);
        } else {
            throw new CompileException(
                "Unsupported level " + level + " for data item '" + name + "'",
                current() != null ? current() : previous);
        }
    }

    // ----------------------------
    // DataItem Header
    // ----------------------------

    private record DataItemHeader(AreaT area, int level, String name, boolean isFiller) {}

    private DataItemHeader parseDataItemHeader() {
        Token levelToken = expect(TokenType.NUMERIC_LITERAL);

        int level;
        try {
            level = Integer.parseInt(levelToken.getValue());
        } catch (NumberFormatException e) {
            throw new CompileException("Invalid level number.", current() != null ? current() : previous);
        }

        validateLevelNumber(level, levelToken);
        AreaT area = levelToken.getArea();

        if (current() != null && current().getType() == TokenType.FILLER) {
            consume();
            return new DataItemHeader(area, level, "FILLER", true);
        }

        String name = expect(TokenType.ALPHANUMERIC_LITERAL).getValue();
        return new DataItemHeader(area, level, name, false);
    }

    // ----------------------------
    // Next-item detection
    // ----------------------------

    /**
     * DataItem 的結束一定是 Dot，Dot 後面可能插入 Floating Comment。
     * 例如：<br>
     * 05 A PIC X. <br>
     * *> comment <br>
     * 05 B PIC 9. <br>
     * <br>
     * Token 串實際上會是： <br>
     * Dot -> Comment -> NumericLiteral(05) <br>
     * <br>
     * 因此： <br>
     * - Comment 不能影響結構判斷 <br>
     * - 必須找「前一個非 Comment token」 <br>
     *   以及「下一個非 Comment token」來判斷邊界 <br>
     */
    private boolean isNextDataItemStart() {
        return previousMeaningfulType() == TokenType.DOT
            && currentMeaningfulType()  == TokenType.NUMERIC_LITERAL;
    }

    /**
     * 找前一個有語意的 token（忽略 Comment）
     */
    private TokenType previousMeaningfulType() {
        for (int i = -1; ; i--) {
            Token t = lookahead(i);
            if (t == null) return null;
            if (t.getType() != TokenType.COMMENT) return t.getType();
        }
    }

    /**
     * 找下一個有語意的 token（忽略 Comment）
     */
    private TokenType currentMeaningfulType() {
        for (int i = 0; ; i++) {
            Token t = lookahead(i);
            if (t == null) return null;
            if (t.getType() != TokenType.COMMENT) return t.getType();
        }
    }

    // ----------------------------
    // Clause Parsers
    // ----------------------------

    private PictureMeta parsePicClause() {
        consume(); // PIC

        PicClauseUsage usage = PicClauseUsage.DISPLAY;
        StringBuilder picString = new StringBuilder();

        while (current() != null
                && current().getType() != TokenType.DOT
                && current().getType() != TokenType.OCCURS
                && current().getType() != TokenType.VALUE) {

            switch (current().getType()) {
                case ALPHANUMERIC_LITERAL, NUMERIC_LITERAL ->
                    picString.append(consume().getValue());

                case L_PAREN -> {
                    picString.append(expect(TokenType.L_PAREN).getValue());
                    picString.append(expect(TokenType.NUMERIC_LITERAL).getValue());
                    picString.append(expect(TokenType.R_PAREN).getValue());
                }

                case USAGE   -> consume();

                // COBOL COMPUTATIONAL

                case COMP_3  -> { consume(); usage = PicClauseUsage.PACKED_DECIMAL;   }
                case BINARY, COMP, COMP_4 -> { consume(); usage = PicClauseUsage.BINARY; }
                case COMP_5  -> { consume(); usage = PicClauseUsage.NATIVE_BINARY;    }
                case PACKED_DECIMAL, COMP_6 -> { consume(); usage = PicClauseUsage.U_PACKED_DECIMAL; }
                case COMP_1, COMP_2 ->
                    throw new CompileException("COMP-1/2 not supported yet.", current());
                
                default -> throw new CompileException(
                    "Invalid token in PIC clause: " + current().getValue(), current());
            }
        }

        PictureMeta pic = PictureMeta.parse(picString.toString());
        pic.setUsage(usage);
        return pic;
    }

    private int parseOccursClause() {
        consume(); // OCCURS
        if (current() == null)
            throw new CompileException("OCCURS clause requires a literal.", previous);

        int occurs = Integer.parseInt(expect(TokenType.NUMERIC_LITERAL).getValue());
        if (current() != null && current().getType() == TokenType.TIMES) consume(); // TIMES

        return occurs;
    }

    private String parseValueClause() {
        consume(); // VALUE
        if (current() == null)
            throw new CompileException("VALUE clause requires a literal.", previous);

        StringBuilder sb = new StringBuilder();

        while (current() != null && current().getType() != TokenType.DOT) {
            switch (current().getType()) {
                case ALPHANUMERIC_LITERAL -> {
                    String raw = consume().getValue();
                    sb.append(unquoteValue(raw));
                }
                case SPACE   -> consume();
                case NUMERIC_LITERAL -> sb.append(consume().getValue());
                case ZERO    -> { sb.append('0'); consume(); }
                case HYPHEN  -> consume(); // continuation indicator, skip
                default -> { return sb.toString(); } // VALUE 結束（例如遇到 DOT 或下一個 clause）
            }
        }

        // TODO: 要根據TokenType輸出成string或decimal...
        return sb.toString();
    }

    private String parseRedefinesClause() {
        consume(); // REDEFINES
        return expect(TokenType.ALPHANUMERIC_LITERAL).getValue();
    }

    private record Lv66Result(String from, String thru) {}

    private Lv66Result parseLv66RenamesClause() {
        consume(); // RENAMES
        String from = expect(TokenType.ALPHANUMERIC_LITERAL).getValue();
        String thru = null;
        if (current() != null && current().getType() == TokenType.THROUGH) {
            consume(); // THRU / THROUGH
            thru = expect(TokenType.ALPHANUMERIC_LITERAL).getValue();
        }
        return new Lv66Result(from, thru);
    }

    private record Lv88Result(List<Object> values, Object through) {}

    private Lv88Result parseLv88ValuesClause() {
        if (current() == null)
            throw new CompileException(
                "'VALUE(S)' clause is required for level 88 items.", previous);

        // VALUE 或 VALUES
        switch (current().getType()) {
            case VALUE, VALUES -> consume();
            default -> throw new CompileException(
                "'VALUE(S)' clause is required for level 88 items.", current());
        }

        List<Object> values  = new ArrayList<>();
        Object       through = null;

        // 解析第一個值
        values.add(parseLv88SingleValue());

        // 後續可能是：
        // VALUE A B C
        // VALUE 1 THROUGH 9
        // VALUE 'A' THROUGH 'Z'

        while (current() != null && current().getType() != TokenType.DOT) {
            if (current().getType() == TokenType.THROUGH) {
                consume(); // THROUGH
                through = parseLv88SingleValue();
                break; // THROUGH 結束語意範圍，不再接續值
            }

            // 多值情況
            values.add(parseLv88SingleValue());
        }

        return new Lv88Result(values.isEmpty() ? null : values, through);
    }

    private Object parseLv88SingleValue() {
        if (current() == null)
            throw new CompileException(
                "Unexpected end of input in level 88 VALUE clause.", previous);

        // Note: 跟 C# 寫法有出入
        return switch (current().getType()) {
            case ALPHANUMERIC_LITERAL -> {
                String raw = consume().getValue();
                yield unquoteValue(raw);
            }
            case NUMERIC_LITERAL -> {
                String n = consume().getValue();
                try { yield Integer.parseInt(n); }
                catch (NumberFormatException e) { yield new BigDecimal(n); }
            }
            default -> throw new CompileException(
                "Invalid VALUE token for level 88: " + current().getType(), current());
        };
    }

    // ----------------------------
    // Helpers
    // ----------------------------

    private static void validateLevelNumber(int level, Token token) {
        // ------------------------------------------------------------
        // Level number rules
        //
        // 1. Level number is a one or two-digit numeric value.
        //
        // 2. Valid level numbers are:
        //      01–49
        //      66
        //      77
        //      88
        //
        // 3. Area rules (Fixed Format only):
        //      - 01 and 77 must begin in Area A.
        //      - 02–49, 66 and 88 may begin in Area A or Area B.
        //      - Area_t.Free skips area validation.
        // ------------------------------------------------------------

        // 必須 1~2 位數
        if (token.getValue().length() < 1 || token.getValue().length() > 2)
            throw new CompileException("Level number must be one or two digits.", token);

        // 合法範圍
        if (!isValidLevel(level))
            throw new CompileException(
                "Invalid level number '" + level + "'. Valid values are 01-49, 66, 77, 88.", token);

        // Free format → 不檢查 Area 規則
        if (token.getArea() == AreaT.FREE) return;

        // Level numbers 01 or 77 should begin in Area A.
        if (level == 1 || level == 77) {
            if (token.getArea() != AreaT.A)
                throw new CompileException(
                    String.format("Level %02d must begin in Area A.", level), token);
        }

        // A level-numbers 02 through 49, 66 and 88 can begin in either Area A or Area B.
        return;
    }

    private static boolean isValidLevel(int level) {
        return (level >= 1 && level <= 49) || level == 66 || level == 77 || level == 88;
    }

    private void collectComments(List<String> target) {
        while (current() != null && current().getType() == TokenType.COMMENT) {
            target.add(current().getValue().trim());
            consume();
        }
    }

    private static String unquoteValue(String tokenValue) {
        if (tokenValue == null || tokenValue.isEmpty()) return tokenValue;

        char quote = tokenValue.charAt(0);

        // 只接受 ' 或 "
        if (quote != '\'' && quote != '"') return tokenValue;

        // 必須成對
        if (tokenValue.length() < 2 || tokenValue.charAt(tokenValue.length() - 1) != quote)
            return tokenValue; // 不完整 literal，保持原樣

        // 去掉外層 quote
        String inner   = tokenValue.substring(1, tokenValue.length() - 1);

        // COBOL escape: '' 或 ""
        String escaped = String.valueOf(new char[]{quote, quote});
        
        return inner.replace(escaped, String.valueOf(quote));
    }
}
