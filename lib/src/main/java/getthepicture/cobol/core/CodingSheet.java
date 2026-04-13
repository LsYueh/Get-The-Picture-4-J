package getthepicture.cobol.core;

/**
 * COBOL coding sheet
 */
public interface CodingSheet {

    /** Column 1-6 */
    String getSequence();

    /** Column 7 */
    char getIndicator();

    /** Column 8-11 */
    String getAreaA();

    /** Column 12-72 */
    String getAreaB();

    /** Column 73-80（可選） */
    String getRemark();
}
