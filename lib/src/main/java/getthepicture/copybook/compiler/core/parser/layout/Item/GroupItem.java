package getthepicture.copybook.compiler.core.parser.layout.Item;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.parser.layout.core.AbstractDataItem;
import getthepicture.copybook.compiler.core.parser.layout.core.DataItem;

public class GroupItem extends AbstractDataItem {
    /**
     * Unnamed Group Item
     */
    private final boolean isFiller;

    // ----------------------------
    // IDataItem
    // ----------------------------
    private final List<DataItem> children = new ArrayList<>();
    private int storageOccupied = 0;

    public GroupItem(AreaT area, int level, String name) {
        this(area, level, name, null, false, null);
    }

    public GroupItem(AreaT area, int level, String name, Integer occurs,
                     boolean isFiller, String comment) {
        super(area, level, name, occurs, comment);
        this.isFiller = isFiller;
    }

    public boolean isFiller() { return isFiller; }

    @Override
    public List<DataItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addSubordinate(DataItem subordinate) {
        Objects.requireNonNull(subordinate, "subordinate must not be null");
        children.add(subordinate);
    }

    // ----------------------------
    // (Cache)
    // ----------------------------
    public int getStorageOccupied() { return storageOccupied; }

    public void calculateStorage() {
        int total = 0;
        for (DataItem child : children) {
            if (child instanceof ElementaryDataItem e) {
                total += e.getPic().getStorageOccupied() * (e.getOccurs() != null ? e.getOccurs() : 1);
            } else if (child instanceof GroupItem g) {
                g.calculateStorage();
                total += g.getStorageOccupied() * (g.getOccurs() != null ? g.getOccurs() : 1);
            } else if (child instanceof RedefinesItem r) {
                // 只計算，不納入佔位空間計算
                r.calculateStorage();
            }
        }
        storageOccupied = total;
    }

    // ----------------------------
    // Dump
    // ----------------------------
    @Override
    public void dump(PrintWriter w, int indent) {
        w.printf("%s%02d %s%s%s%n", indent(indent), getLevel(), getName(), formatOccurs(), formatComment());
        for (DataItem child : children)
            child.dump(w, indent + 1);
    }
}
