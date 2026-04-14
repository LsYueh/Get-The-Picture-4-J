package getthepicture.copybook.compiler.core.parser.layout.Item;

import java.io.PrintWriter;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.parser.layout.core.DataItem;

public final class RedefinesItem extends GroupItem {
    // ----------------------------
    // REDEFINES
    // ----------------------------
    private final String targetName;
    private DataItem target = null;

    public RedefinesItem(AreaT area, int level, String name, String targetName) {
        this(area, level, name, targetName, null);
    }

    public RedefinesItem(AreaT area, int level, String name, String targetName, String comment) {
        super(area, level, name, null, false, comment);
        this.targetName = targetName;
    }

    public String   getTargetName() { return targetName; }
    public DataItem getTarget()     { return target;     }

    public void setTarget(DataItem target) {
        this.target = target;
    }

    // ----------------------------
    // Dump
    // ----------------------------
    @Override
    public void dump(PrintWriter writer, int indent) {
        writer.printf("%s%02d %s REDEFINES %s.", indent(indent), getLevel(), getName(), targetName);

        String comment = getComment();
        if (comment != null && !comment.isBlank())
            writer.printf("  *> %s", comment);

        writer.println();
        for (DataItem child : getChildren())
            child.dump(writer, indent + 1);
    }
}
