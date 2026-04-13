package getthepicture.picture.utils;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;

public class EncodingFactory {

    private static final Charset STRICT_CP950 = buildCharset(
        CodingErrorAction.REPORT,   // string → byte[]
        CodingErrorAction.REPORT    // byte[] → string
    );

    private static final Charset CP950 = buildCharset(
        CodingErrorAction.REPORT,
        CodingErrorAction.REPLACE   // 容錯顯示
    );

    private static Charset buildCharset(CodingErrorAction encoderAction, CodingErrorAction decoderAction) {
        return Charset.forName("CP950")
                      .newDecoder()
                      .onMalformedInput(decoderAction)
                      .onUnmappableCharacter(decoderAction)
                      .charset();
        // Note: Java 的 Charset 本身不帶 encoder/decoder action，
        // 實際 encode/decode 時需透過 CharsetEncoder / CharsetDecoder 套用 action
    }

    public static Charset getStrictCP950() { return STRICT_CP950; }
    public static Charset getCP950()       { return CP950; }
}
