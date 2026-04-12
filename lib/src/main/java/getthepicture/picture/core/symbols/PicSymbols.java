package getthepicture.picture.core.symbols;

import getthepicture.picture.core.symbols.lexer.PicSymbolsLexer;
import getthepicture.picture.core.symbols.parser.PicSymbolsParser;
import getthepicture.picture.core.symbols.parser.PicSymbolsMeta;

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
