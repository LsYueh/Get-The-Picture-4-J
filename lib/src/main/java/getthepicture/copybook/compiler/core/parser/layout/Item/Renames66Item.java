package getthepicture.copybook.compiler.core.parser.layout.Item;

import java.io.PrintWriter;
import java.util.List;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.parser.layout.core.AbstractDataItem;
import getthepicture.copybook.compiler.core.parser.layout.core.DataItem;

/**
 * RENAMES
 */
public final class Renames66Item extends AbstractDataItem {
    // ----------------------------
    // RENAMES
    // ----------------------------
    private final String fromName;
    private ElementaryDataItem from = null;
    private final String thruName;
    private ElementaryDataItem thru = null;

    public Renames66Item(AreaT area, String name, String fromName, String thruName) {
        this(area, name, fromName, thruName, null);
    }

    public Renames66Item(AreaT area, String name, String fromName, String thruName, String comment) {
        super(area, 66, name, null, comment);
        this.fromName = fromName;
        this.thruName = thruName;
    }

    public String              getFromName() { return fromName; }
    public ElementaryDataItem  getFrom()     { return from;     }
    public String              getThruName() { return thruName; }
    public ElementaryDataItem  getThru()     { return thru;     }

    /**
     * 解析 66 層級 RENAMES，對應 From ~ Through 範圍
     */
    public void setAffectedItems(List<DataItem> flatten) {
        int start = -1;
        for (int i = 0; i < flatten.size(); i++) {
            if (flatten.get(i).getName().equals(fromName)) {
                start = i;
                from = validateTarget(flatten.get(i), "from");
                break;
            }
        }
        if (start < 0)
            throw new IllegalStateException(
                "RENAMES from '" + fromName + "' not found.");

        int end = start;
        if (thruName != null && !thruName.isEmpty()) {
            // TODO: the OCCURS DEPENDING clause must not be specified for any
            //       item defined between data-name-2 and data-name-3.
            for (int i = start; i < flatten.size(); i++) {
                if (flatten.get(i).getName().equals(thruName)) {
                    end = i;
                    thru = validateTarget(flatten.get(i), "thru");
                    break;
                }
            }
            if (end < 0)
                throw new IllegalStateException(
                    "RENAMES thru '" + thruName + "' not found.");
        }

        if (end < start)
            throw new IllegalStateException(
                "RENAMES range invalid: " + fromName + " thru " + thruName);
    }

    private static ElementaryDataItem validateTarget(DataItem item, String role) {
        int lvl = item.getLevel();
        if (lvl == 1 || lvl == 77 || lvl == 88 || lvl == 66)
            throw new IllegalStateException(
                "RENAMES " + role + " '" + item.getName()
                + "' cannot reference level " + lvl + " items.");

        if (!(item instanceof ElementaryDataItem e))
            throw new IllegalStateException(
                "RENAMES " + role + " '" + item.getName()
                + "' must reference an Elementary Data Item.");

        if (e.getOccurs() != null && e.getOccurs() > 0)
            throw new IllegalStateException(
                "RENAMES " + role + " '" + item.getName()
                + "' cannot reference an OCCURS item.");

        return e;
    }

    // ----------------------------
    // Dump
    // ----------------------------
    @Override
    public void dump(PrintWriter w, int indent) {
        w.printf("%s66 %s%s >> Renames %s", indent(indent), getName(), formatComment(), fromName);
        
        if (thruName != null && !thruName.isEmpty()) {
            w.print(" through ");
            w.print(thruName);
        }

        w.println();

        from.dump(w, indent + 1);
        if (thru != null)
            thru.dump(w, indent + 1);
    }
}
