package io.github.lsyueh.getthepicture.picture.core.symbols;

import io.github.lsyueh.getthepicture.picture.core.symbols.lexer.PicSymbolsLexer;
import io.github.lsyueh.getthepicture.picture.core.symbols.parser.PicSymbolsMeta;
import io.github.lsyueh.getthepicture.picture.core.symbols.parser.PicSymbolsParser;

public final class PicSymbols {

    private static final PicSymbolsLexer  lexer  = new PicSymbolsLexer();
    private static final PicSymbolsParser parser = new PicSymbolsParser();

    private PicSymbols() { }

    public static PicSymbolsMeta read(String symbols) {
        var tokens = lexer.tokenize(symbols);
        var meta   = parser.analyze(tokens);
        return meta;
    }
}
