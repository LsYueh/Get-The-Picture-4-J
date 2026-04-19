package getthepicture.copybook.resolver.storage.core;

import java.io.PrintWriter;
import java.util.List;

public interface StorageNode {

    int getLevel();

    /**
     * Tree/節點名稱
     */
    String getName();

    /**
     * OCCURS index
     */
    Integer getIndex();

    /**
     * Floating Comments in Copybook
     */
    String getInfo();

    /**
     * Indicates whether this node represents a filler field (FILLER).
     */
    boolean isIgnored();

    // ----------------------------
    // Alias and Offset (For implementing REDEFINES)
    // ----------------------------

    /**
     * Storage alias indicates that this node does not define its own
     * starting offset, but reuses the offset of another storage node.
     * The occupied length is still defined by this node.
     */
    StorageAlias getAlias();

    default boolean isAlias() {
        return getAlias() != null;
    }

    int getOffset();

    // ----------------------------
    // Storage Occupied
    // ----------------------------

    Integer getStorageOccupied();

    // ----------------------------
    // Node Operations
    // ----------------------------

    /**
     * Child storage nodes in resolved layout order.
     */
    List<StorageNode> getChildren();

    // ----------------------------
    // Dump
    // ----------------------------

    void dump(PrintWriter writer, int indent);

    default void dump(PrintWriter writer) {
        dump(writer, 0);
    }
}
