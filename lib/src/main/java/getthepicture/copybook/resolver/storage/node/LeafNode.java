package getthepicture.copybook.resolver.storage.node;

import java.io.PrintWriter;

import getthepicture.copybook.resolver.storage.core.AbstractStorageNode;
import getthepicture.copybook.resolver.storage.core.StorageNode;
import getthepicture.picture.codec.semantic.Rules;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.meta.PictureMeta;

public class LeafNode extends AbstractStorageNode {

    public LeafNode(int level, String name, PictureMeta pic, int offset, int storageOccupied, Integer index) {
        super(level, name, offset, storageOccupied, index);
        this.pic = pic;
    }

    public LeafNode(int level, String name, PictureMeta pic, int offset, int storageOccupied) {
        this(level, name, pic, offset, storageOccupied, null);
    }

    public LeafNode(int level, String name, PictureMeta pic) {
        this(level, name, pic, 0, 0, null);
    }

    // ----------------------------
    // StorageNode
    // ----------------------------

    /**
     * 標記為 FILLER，可忽略
     */
    public void canIgnore() {
        setIgnored(true);
    }

    @Override
    public void setAlias(StorageNode target) {
        throw new UnsupportedOperationException("Leaf nodes cannot be aliased.");
    }

    // ----------------------------
    // PICTURE Clause
    // ----------------------------

    private final PictureMeta pic;
    private boolean isRenames66 = false;

    public PictureMeta getPic() { return pic; }

    public boolean isRenames66() { return isRenames66; }

    public void setSemantic(PicClauseSemantic semantic) {
        pic.setSemantic(semantic);
        ensureConsistency();
    }

    private void ensureConsistency() {
        var rule = Rules.getConstraint(pic.getSemantic());
        if (!rule.isStructureValid(pic)) {
            throw new IllegalStateException(
                "Semantic '" + pic.getSemantic() + "' is not compatible with PIC structure '" + pic + "'."
            );
        }
    }

    public void asRenames() {
        isRenames66 = true;
    }

    // ----------------------------
    // Dump
    // ----------------------------

    @Override
    public void dump(PrintWriter writer, int indent) {
        String displayName = isRenames66 ? "*" + getName() : getName();
        writer.println(indent(indent) + String.format("%02d", getLevel()) + " " + displayName
            + formatIndex() + formatOffset(true) + formatOccupied(true));
    }
}
