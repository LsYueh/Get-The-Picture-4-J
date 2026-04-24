package getthepicture.copybook.resolver.storage.node;

import getthepicture.copybook.compiler.core.parser.layout.Item.ElementaryDataItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.GroupItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.Renames66Item;
import getthepicture.copybook.resolver.core.ResolverException;
import getthepicture.picture.core.meta.PictureMeta;

public class Builder {

    /**
     * Group Item
     */
    public static GroupNode buildGroupNode(GroupItem g, int instanceOffset, Integer occursIndex) {
        GroupNode groupNode = new GroupNode(g.getLevel(), g.getName(), instanceOffset, occursIndex);
        if (g.isFiller())
            groupNode.unnamed();
        return groupNode;
    }

    /**
     * Elementary Data Item
     */
    public static LeafNode buildLeafNode(ElementaryDataItem e, int instanceOffset, int storageOccupied, Integer occursIndex) {
        LeafNode leafNode = new LeafNode(
            e.getLevel(), e.getName(), e.getPic(),
            instanceOffset, storageOccupied, occursIndex
        );
        if (e.getComment() != null)
            leafNode.setInfo(e.getComment());
        if (e.isFiller())
            leafNode.canIgnore();
        return leafNode;
    }

    /**
     * Lv 66 RENAMES
     *
     * @throws ResolverException
     */
    public static LeafNode buildLeafNode(Renames66Item re, LeafNode from, LeafNode thru) {
        if (thru != null && thru.getOffset() < from.getOffset())
            throw new ResolverException("RENAMES THRU must be after FROM.");

        int start = from.getOffset();
        int end = (thru != null)
            ? thru.getOffset() + thru.getPic().getStorageOccupied()
            : from.getOffset() + from.getPic().getStorageOccupied();
        int length = end - start;
        if (length <= 0)
            throw new ResolverException("Invalid RENAMES range.");

        // 用 PIC X 去呈現 RENAMES 的 raw byte View
        PictureMeta pic = PictureMeta.parse("X(" + length + ")");

        LeafNode leafNode = new LeafNode(
            re.getLevel(), re.getName(), pic,
            start, pic.getStorageOccupied()
        );
        leafNode.asRenames();
        if (re.getComment() != null)
            leafNode.setInfo(re.getComment());
        return leafNode;
    }
}
