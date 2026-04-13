package getthepicture.cobol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import getthepicture.cobol.core.Area;
import getthepicture.cobol.core.CodingSheet;

public class CobolLine implements CodingSheet {

    // ----------------------------
    // COBOL coding sheet
    // ----------------------------

    /** Column 1-6 */
    private String sequence = "";

    /** Column 7 */
    private char indicator = ' ';

    /** Column 8-11 */
    private String areaA = "";

    /** Column 12-72 */
    private String areaB = "";

    /** Column 73-80（可選） */
    private String remark = "";

    // ----------------------------
    // COBOL Line
    // ----------------------------

    private final String rawText;

    /** 自動編號 */
    private int lineNumber;

    /** 空行或註解行 */
    private boolean ignored = false;

    /** Area A / Area B */
    private Area area = Area.NONE;

    private CobolLine(int lineNumber, String rawText) {
        this.lineNumber = lineNumber;
        this.rawText = rawText;
    }

    // ----------------------------
    // Getters / Setters
    // ----------------------------

    @Override public String getSequence()  { return sequence;  }
    @Override public char   getIndicator() { return indicator; }
    @Override public String getAreaA()     { return areaA;     }
    @Override public String getAreaB()     { return areaB;     }
    @Override public String getRemark()    { return remark;    }

    public String  getRawText()    { return rawText;    }
    public int     getLineNumber() { return lineNumber; }
    public void    setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    public boolean isIgnored()     { return ignored;    }
    public void    setIgnored(boolean ignored)   { this.ignored = ignored; }
    public Area    getArea()       { return area;       }

    /** AreaA + AreaB */
    public String getText() { return areaA + areaB; }

    // ----------------------------
    // toString
    // ----------------------------

    @Override
    public String toString() {
        // 格式化成 Coding Sheet（固定欄位寬度）
        return String.format("%-6s%c%-4s%-61s%-8s",
                sequence, indicator, areaA, areaB, remark);
    }

    // ----------------------------
    // Factory
    // ----------------------------

    public static List<CobolLine> fromReader(Reader reader) throws IOException {
        var lines = new ArrayList<CobolLine>();
        int lineNumber = 1;

        try (var br = new BufferedReader(reader)) {
            String rawText;
            while ((rawText = br.readLine()) != null) {
                var cobolLine = parse(rawText, lineNumber++);
                if (cobolLine.ignored) continue;
                lines.add(cobolLine);
            }
        }

        return Collections.unmodifiableList(lines);
    }

    // ----------------------------
    // Parse
    // ----------------------------

    private static CobolLine parse(String rawText, int lineNumber) {
        var line = new CobolLine(lineNumber, rawText);

        // 空行 → 標記為忽略
        if (rawText.isBlank()) {
            line.ignored = true;
            return line;
        }

        // Column 1–6: Sequence
        line.sequence = rawText.length() >= 6 ? rawText.substring(0, 6) : "";

        // Column 7: Indicator
        line.indicator = rawText.length() >= 7 ? rawText.charAt(6) : ' ';

        // 註解行 → 標記為忽略
        if (line.indicator == '*') {
            line.ignored = true;
            return line;
        }

        // Column 8–11: Area A
        line.areaA = rawText.length() >= 11 ? rawText.substring(7, 11) : "";

        // Column 12–72: Area B
        if (rawText.length() >= 12) {
            int end = Math.min(11 + 61, rawText.length());
            line.areaB = rawText.substring(11, end);
        }

        // Column 73–80: Remark
        if (rawText.length() >= 73) {
            int end = Math.min(72 + 8, rawText.length());
            line.remark = rawText.substring(72, end);
        }

        line.classifyArea();
        return line;
    }

    // ----------------------------
    // Area classification
    // ----------------------------

    private void classifyArea() {
        if (!areaA.isBlank()) {
            validateLevelIfNumeric(areaA);
            area = Area.A;
            return;
        }
        if (!areaB.isBlank()) {
            area = Area.B;
            return;
        }
        area = Area.FREE;
    }

    private void validateLevelIfNumeric(String areaA) {
        var text = areaA.trim();

        // 不是純數字 → 不處理（可能是 SECTION / FD）
        if (!text.chars().allMatch(Character::isDigit)) return;

        if (text.length() != 2)
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": Level number must be 2 digits.");

        int level;
        try {
            level = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return;
        }

        if (!isValidLevel(level))
            throw new IllegalArgumentException(
                    "Line " + lineNumber + ": Invalid level number '" + level + "'.");
    }

    /** 只做範圍檢查，進階判斷交給編譯器處理 */
    private static boolean isValidLevel(int level) {
        if (level >= 1 && level <= 49) return true;
        return level == 66 || level == 77 || level == 88;
    }
}
