package getthepicture.copybook.resolver.storage;

import java.io.PrintWriter;

import getthepicture.copybook.resolver.storage.core.StorageNode;
import getthepicture.copybook.resolver.storage.node.GroupNode;

/**
 * Root of Group Nodes
 */
public final class CbStorage extends GroupNode {

    private final int totalLength;

    public CbStorage(int totalLength) {
        super(0, "COPYBOOK-STORAGE-MAP");
        this.totalLength = totalLength;
    }

    public int getTotalLength() { return totalLength; }

    // ----------------------------
    // Dump
    // ----------------------------

    // Root node: no offset / length
    @Override
    public void dump(PrintWriter w, int indent) {
        w.println(indent(indent) + getName());
        for (StorageNode child : getChildren()) {
            child.dump(w, indent + 1);
        }
    }
}
