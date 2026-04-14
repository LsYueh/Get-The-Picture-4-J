package getthepicture.copybook.compiler.core.parser.layout;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.parser.layout.Item.ElementaryDataItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.GroupItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.RedefinesItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.Renames66Item;
import getthepicture.copybook.compiler.core.parser.layout.core.DataItem;

/**
 * Root of Group Items
 */
public final class CbLayout extends GroupItem {
    private boolean sealed = false;
    private final List<Renames66Item> renames66 = new ArrayList<>();

    public CbLayout() {
        super(AreaT.NONE, 0, "COPYBOOK-LAYOUT");
    }

    /**
     * Freeze semantic layout and build runtime cache.
     * Must be called after Analyze().
     */
    public void seal() {
        if (sealed) return;
        calculateStorage();
        validateRenamesReferences();
        sealed = true;
    }

    private void validateRenamesReferences() {
        List<DataItem> flatten = new ArrayList<>();
        renames66.clear();
        walkForRenames(this, flatten);
        for (Renames66Item rename : renames66)
            rename.setAffectedItems(flatten);
    }

    private void walkForRenames(DataItem node, List<DataItem> flatten) {
        if (node instanceof Renames66Item re) {
            renames66.add(re);
            return;
        }
        if (node instanceof RedefinesItem r) {
            flatten.add(r);
            for (DataItem child : r.getChildren())
                walkForRenames(child, flatten);
            return;
        }
        if (node instanceof GroupItem g) {
            flatten.add(g);
            for (DataItem child : g.getChildren())
                walkForRenames(child, flatten);
            return;
        }
        if (node instanceof ElementaryDataItem e) {
            if (!e.isFiller())
                flatten.add(e);
            return;
        }
        // Defensive traversal for future DataItem extension
        for (DataItem child : node.getChildren())
            walkForRenames(child, flatten);
    }

    /**
     * RENAMES 66 collection.
     */
    public List<Renames66Item> getRenames66() {
        return Collections.unmodifiableList(renames66);
    }

    // ----------------------------
    // Dump
    // ----------------------------
    /**
     * Diagnostic dump.
     */
    @Override
    public void dump(PrintWriter w, int indent) {
        if (!sealed)
            throw new IllegalStateException("Layout must be sealed before dump.");

        w.println(indent(indent) + getName());
        for (DataItem child : getChildren())
            child.dump(w, indent + 1);
    }
}
