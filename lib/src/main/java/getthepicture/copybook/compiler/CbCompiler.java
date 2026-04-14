package getthepicture.copybook.compiler;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

import getthepicture.cobol.CobolLine;
import getthepicture.copybook.compiler.core.lexer.Lexer;
import getthepicture.copybook.compiler.core.lexer.Token;
import getthepicture.copybook.compiler.core.parser.Parser;
import getthepicture.copybook.compiler.core.parser.layout.CbLayout;

public final class CbCompiler {

    private CbCompiler() {}

    public static CbLayout fromReader(Reader reader) throws IOException {
        Objects.requireNonNull(reader, "reader must not be null");

        List<CobolLine> lines = CobolLine.fromReader(reader);
        Lexer lexer = new Lexer(lines);
        
        List<Token> tokens = lexer.tokenize();
        Parser parser = new Parser(tokens);
        
        CbLayout layout = parser.analyze();
        
        /**
         * Note:
         * parser.Analyze() 在這個設計裡已經確定回傳 CbLayout
         * 所以 C# 的 if (obj is not CbLayout layout) 的防禦檢查不再需要
         */
        
        layout.seal();

        return layout;
    }
}
