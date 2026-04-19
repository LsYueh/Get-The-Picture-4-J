package getthepicture.copybook.resolver.storage.node;

import java.io.PrintWriter;

import getthepicture.copybook.resolver.storage.core.AbstractStorageNode;
import getthepicture.copybook.resolver.storage.core.StorageNode;

public class GroupNode extends AbstractStorageNode {

    public GroupNode(int level, String name, int offset, Integer index) {
        super(level, name, offset, null, index);
    }

    public GroupNode(int level, String name, int offset) {
        this(level, name, offset, null);
    }

    public GroupNode(int level, String name) {
        this(level, name, 0, null);
    }

    // ----------------------------
    // StorageNode
    // ----------------------------

    /**
     * 若 Group Item 標記為 FILLER，則為不具名的群組項目 (Unnamed Group Item)
     */
    public void unnamed() {
        setIgnored(true);
    }

    // ----------------------------
    // Dump
    // ----------------------------

    @Override
    public void dump(PrintWriter writer, int indent) {
        writer.println(indent(indent) + String.format("%02d", getLevel()) + " " + getDisplayName()
            + formatIndex() + formatOffset(true) + formatOccupied(true));
        for (StorageNode child : getChildren()) {
            child.dump(writer, indent + 1);
        }
    }

    private String getDisplayName() {
        return isIgnored() ? "<Group>" : getName();
    }
}
