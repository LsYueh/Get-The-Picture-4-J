package getthepicture.copybook.compiler.core.parser.layout.core;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import getthepicture.cobol.core.AreaT;

public abstract class AbstractDataItem implements DataItem {
    private final AreaT area;
    private final int level;
    private final String name;
    private final Integer occurs;
    private final String comment;

    protected AbstractDataItem(AreaT area, int level, String name) {
        this(area, level, name, null, null);
    }

    protected AbstractDataItem(AreaT area, int level, String name, Integer occurs) {
        this(area, level, name, occurs, null);
    }

    protected AbstractDataItem(AreaT area, int level, String name, Integer occurs, String comment) {
        this.area    = area;
        this.level   = level;
        this.name    = name;
        this.occurs  = occurs;
        this.comment = comment;
    }

    @Override public AreaT   getArea()    { return area;    }
    @Override public int     getLevel()   { return level;   }
    @Override public String  getName()    { return name;    }
    @Override public Integer getOccurs()  { return occurs;  }
    @Override public String  getComment() { return comment; }

    @Override
    public List<DataItem> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public abstract void dump(PrintWriter writer, int indent);

    @Override
    public void dump(PrintWriter writer) {
        dump(writer, 0);
    }

    protected String indent(int i) {
        return " ".repeat(i * 2) + margin();
    }

    protected String margin() {
        return switch (area) {
            case A -> "[A] ";
            case B -> "[B] ";
            default -> "";
        };
    }

    protected String formatOccurs() {
        return (occurs != null && occurs > 1) ? " OCCURS " + occurs : "";
    }

    protected String formatComment() {
        return (comment != null) ? " [" + comment + "]" : "";
    }
}
