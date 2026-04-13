package getthepicture.picture.core.meta;

import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.symbols.PicSymbols;
import getthepicture.picture.core.symbols.parser.PicSymbolsMeta;

class PictureMetaBuilder {

    public static PictureMeta parse(String symbols, PicClauseSemantic semantic, PicClauseUsage usage) {
        if (symbols == null || symbols.isBlank())
            throw new IllegalArgumentException("PIC clause is empty.");

        symbols = symbols.toUpperCase().replace(" ", "");

        PicSymbolsMeta meta = PicSymbols.read(symbols);

        return switch (meta.getBaseClass()) {
            // ─────────────────────────
            // Numeric
            // ─────────────────────────
            case NUMERIC -> {
                PictureMeta pic = new PictureMeta();
                pic.setRaw(symbols);
                pic.setBaseClass(meta.getBaseClass());
                pic.setSemantic(semantic);
                pic.setUsage(usage);
                pic.setSigned(meta.isSigned());
                pic.setIntegerDigits(meta.getIntegerDigits());
                pic.setDecimalDigits(meta.getDecimalDigits());
                yield pic;
            }
            // ─────────────────────────
            // Alphanumeric
            // ─────────────────────────
            case ALPHANUMERIC -> {
                PictureMeta pic = new PictureMeta();
                pic.setRaw(symbols);
                pic.setBaseClass(meta.getBaseClass());
                pic.setSemantic(semantic);
                pic.setUsage(PicClauseUsage.DISPLAY);
                pic.setSigned(false);
                pic.setIntegerDigits(meta.getIntegerDigits());
                pic.setDecimalDigits(0);
                yield pic;
            }
            // ─────────────────────────
            // Alphabetic
            // ─────────────────────────
            case ALPHABETIC -> {
                PictureMeta pic = new PictureMeta();
                pic.setRaw(symbols);
                pic.setBaseClass(meta.getBaseClass());
                pic.setSemantic(semantic);
                pic.setUsage(PicClauseUsage.DISPLAY);
                pic.setSigned(false);
                pic.setIntegerDigits(meta.getIntegerDigits());
                pic.setDecimalDigits(0);
                yield pic;
            }
            default -> throw new UnsupportedOperationException(
                "Unsupported PIC clause: " + symbols);
        };
    }
}
