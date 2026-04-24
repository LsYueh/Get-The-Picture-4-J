package getthepicture.cobol.core;

/**
 * COBOL 程式的區域類型 <br>
 * A 區：程式的主體，包含程式碼和註解 <br>
 * B 區：程式的副體，包含資料定義和其他非程式碼內容 <br>
 * FREE：自由格式的程式碼區域，不受 A 區和 B 區的限制 <br> 
 */
public enum AreaT {
    NONE,
    A, B, FREE
}
