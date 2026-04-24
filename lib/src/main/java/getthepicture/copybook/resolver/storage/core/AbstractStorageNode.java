package getthepicture.copybook.resolver.storage.core;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractStorageNode implements StorageNode {

    private final int level;
    private final String name;
    private final Integer index;
    private String info = null;
    private boolean ignored = false;

    // ----------------------------
    // Alias and Offset (For implementing REDEFINES)
    // ----------------------------
    private StorageAlias alias = null;
    private final int _offset;

    // ----------------------------
    // Storage Occupied
    // ----------------------------
    private final Integer storageOccupied;

    // ----------------------------
    // Node Operations
    // ----------------------------
    private final List<StorageNode> children = new ArrayList<>();

    protected AbstractStorageNode(int level, String name, int offset, Integer storageOccupied, Integer index) {
        this.level = level;
        this.name = name;
        this._offset = offset;
        this.storageOccupied = storageOccupied;
        this.index = index;
    }

    protected AbstractStorageNode(int level, String name) {
        this(level, name, 0, null, null);
    }

    protected AbstractStorageNode(int level, String name, int offset) {
        this(level, name, offset, null, null);
    }

    @Override
    public int getLevel() { return level; }

    @Override
    public String getName() { return name; }

    @Override
    public Integer getIndex() { return index; }

    @Override
    public String getInfo() { return info; }

    public void setInfo(String info) { this.info = info; }

    @Override
    public boolean isIgnored() { return ignored; }

    public void setIgnored(boolean ignored) { this.ignored = ignored; }

    // ----------------------------
    // Alias and Offset (For implementing REDEFINES)
    // ----------------------------

    @Override
    public StorageAlias getAlias() { return alias; }

    public void setAlias(StorageAlias alias) { this.alias = alias; }

    public void setAlias(StorageNode target) {
        // Note: 不知道 COBOL Runtime 本身會不會檢查
        if (storageOccupied != null &&
            target.getStorageOccupied() != null &&
            storageOccupied > target.getStorageOccupied()) {
            throw new IllegalStateException(
                "Alias '" + name + "' exceeds target storage size."
            );
        }
        this.alias = new StorageAlias(target);
    }

    @Override
    public int getOffset() {
        return alias != null ? alias.getTarget().getOffset() : _offset;
    }

    // ----------------------------
    // Storage Occupied
    // ----------------------------

    @Override
    public Integer getStorageOccupied() { return storageOccupied; }

    // ----------------------------
    // Node Operations
    // ----------------------------

    @Override
    public List<StorageNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addNode(StorageNode node) {
        if (node == null) throw new IllegalArgumentException("node must not be null");
        children.add(node);
    }

    // ----------------------------
    // Dump
    // ----------------------------

    @Override
    public abstract void dump(PrintWriter writer, int indent);

    @Override
    public void dump(PrintWriter writer) {
        dump(writer, 0);
    }

    protected void dumpBase(PrintWriter writer, int indent) {
        writer.println(indent(indent) + String.format("%02d", level) + " " + name
            + formatIndex() + formatOffset(true) + formatOccupied(true));
    }

    protected static String indent(int i) {
        return " ".repeat(i * 2);
    }

    protected String formatIndex() {
        return index != null ? "(" + index + ")" : "";
    }

    protected String formatOffset(boolean oneBased) {
        return oneBased ? " start=" + (getOffset() + 1) : " offset=" + getOffset();
    }

    protected String formatOccupied(boolean showEnd) {
        if (storageOccupied == null) return "";
        String result = " len=" + storageOccupied;
        if (showEnd) {
            int end = getOffset() + storageOccupied + 1;
            result += " end=" + end;
        }
        return result;
    }
}
