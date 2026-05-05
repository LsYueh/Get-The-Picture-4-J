package io.github.lsyueh.getthepicture.picture.core.meta;

import io.github.lsyueh.getthepicture.picture.core.clause.items.PicClauseSemantic;
import io.github.lsyueh.getthepicture.picture.core.clause.items.PicClauseUsage;
import io.github.lsyueh.getthepicture.picture.core.symbols.PicSymbols;
import io.github.lsyueh.getthepicture.picture.core.symbols.parser.PicSymbolsMeta;

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
