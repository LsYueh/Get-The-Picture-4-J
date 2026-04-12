package getthepicture.picture;

import getthepicture.picture.codec.CodecOptions;
import getthepicture.picture.codec.Decoder;
import getthepicture.picture.codec.Encoder;
import getthepicture.picture.codec.Initializer;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.clause.options.DataStorageOptions;
import getthepicture.picture.core.clause.options.SignOptions;
import getthepicture.picture.core.meta.PictureMeta;

/**
 * COBOL PICTURE Clause Codec Context
 */
public final class PictureCodec {

    private final PictureMeta picMeta;
    private final CodecOptions options = new CodecOptions();

    private PictureCodec(PictureMeta meta) {
        this.picMeta = meta;
    }

    /**
     * Codec Builder entry point
     */
    public static PictureCodec forMeta(PictureMeta meta) {
        return new PictureCodec(meta);
    }

    // -------------------------------------------------------------------------
    // COBOL Compile Options
    // -------------------------------------------------------------------------

    /**
     * Data Length Enforcement
     */
    public PictureCodec withStrict() {
        options.setStrict(true);
        return this;
    }

    /**
     * For PIC S9 DISPLAY. (Overpunch Codex)
     */
    public PictureCodec withDataStorageOption(DataStorageOptions opt) {
        options.setDataStorage(opt != null ? opt : DataStorageOptions.CI);
        return this;
    }

    /**
     * For PIC S9 DISPLAY. (Overpunch Codex)
     */
    public PictureCodec withSignIsLeading() {
        options.setSign(SignOptions.IS_LEADING);
        return this;
    }

    /**
     * For COMP-5 use only.
     */
    public PictureCodec isLittleEndian() {
        options.setBigEndian(false);
        return this;
    }

    // -------------------------------------------------------------------------
    // COBOL PICTURE Clause
    // -------------------------------------------------------------------------

    public PictureCodec asSemantic(PicClauseSemantic picSemantic) {
        picMeta.setSemantic(picSemantic);
        return this;
    }

    public PictureCodec asSemantic() {
        return asSemantic(PicClauseSemantic.NONE);
    }

    public PictureCodec usage(PicClauseUsage usage) {
        picMeta.setUsage(usage);
        return this;
    }

    public PictureCodec usage() {
        return usage(PicClauseUsage.DISPLAY);
    }

    // -------------------------------------------------------------------------
    // Codec
    // -------------------------------------------------------------------------

    /**
     * COBOL Elementary Item (buffer) → Java value
     *
     * @param buffer COBOL Elementary Item
     * @return decoded value
     */
    public Object decode(byte[] buffer) {
        if (buffer.length == 0)
            throw new IllegalArgumentException("Buffer is empty.");
        return Decoder.decode(buffer, picMeta, options);
    }

    /**
     * Java value → COBOL Elementary Item (buffer)
     *
     * @param value
     * @return encoded buffer
     */
    public byte[] encode(Object value) {
        if (value == null)
            throw new IllegalArgumentException("value must not be null");
        return Encoder.encode(value, picMeta, options);
    }

    /**
     * Creates the default byte representation for a COBOL elementary item
     * based on its PICTURE clause and codec options.
     *
     * <p>The returned buffer contains the initialized (default) value:
     * <ul>
     *   <li>Numeric types are initialized to zero.</li>
     *   <li>Alphanumeric and alphabetic types are initialized to spaces.</li>
     * </ul>
     * The exact representation depends on the underlying codec
     * (e.g., DISPLAY, COMP-3, COMP-5).
     *
     * @return A newly allocated byte array containing the initialized representation.
     */
    public byte[] createDefaultRepresentation() {
        return Initializer.initialize(picMeta, options);
    }
}
