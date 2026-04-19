package getthepicture.copybook.resolver;

import getthepicture.copybook.compiler.core.CompileException;
import getthepicture.copybook.compiler.core.parser.layout.CbLayout;
import getthepicture.copybook.compiler.core.parser.layout.Item.ElementaryDataItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.GroupItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.RedefinesItem;
import getthepicture.copybook.compiler.core.parser.layout.Item.Renames66Item;
import getthepicture.copybook.compiler.core.parser.layout.core.DataItem;
import getthepicture.copybook.resolver.storage.CbStorage;
import getthepicture.copybook.resolver.storage.core.StorageNode;
import getthepicture.copybook.resolver.storage.node.GroupNode;
import getthepicture.copybook.resolver.storage.node.LeafNode;
import getthepicture.copybook.resolver.storage.node.Builder;

public final class CbResolver {

    public static CbStorage fromLayout(CbLayout layout) {
        if (layout == null) throw new IllegalArgumentException("layout must not be null");

        CbStorage storage = new CbStorage(layout.getStorageOccupied());

        resolveGroupNodes(layout, storage, 0);

        return storage;
    }

    /**
     * @param item       資料項目
     * @param node       群組節點
     * @param baseOffset 起始位置 (group 開始)
     * @return 已佔用的 storage 大小
     * @throws IllegalStateException
     */
    private static int resolveGroupNodes(DataItem item, GroupNode node, int baseOffset) {
        // 已佔用 storage，只有 GroupItem 和 ElementaryDataItem 推進
        int storageOffset = 0;

        for (DataItem child : item.getChildren()) {
            int occurs = child.getOccurs() != null ? child.getOccurs() : 1;

            for (int i = 0; i < occurs; i++) {
                Integer occursIndex = occurs > 1 ? i + 1 : null;

                if (child instanceof RedefinesItem r) {
                    StorageNode alias = resolveAlias(r.getTargetName(), node);

                    GroupNode groupNode = new GroupNode(r.getLevel(), r.getName(), -1);
                    groupNode.setAlias(alias);
                    node.addNode(groupNode);

                    // 會直接用 Alias 的 Offset 作為 baseOffset
                    resolveGroupNodes(r, groupNode, groupNode.getOffset());

                    // REDEFINES does not advance storage offset

                } else if (child instanceof GroupItem g) {
                    int instanceOffset = baseOffset + storageOffset;

                    GroupNode groupNode = Builder.buildGroupNode(g, instanceOffset, occursIndex);
                    node.addNode(groupNode);

                    int groupSize = resolveGroupNodes(g, groupNode, instanceOffset);

                    storageOffset += groupSize;

                } else if (child instanceof ElementaryDataItem e) {
                    int instanceOffset = baseOffset + storageOffset;
                    int storageOccupied = e.getPic().getStorageOccupied();

                    LeafNode leafNode = Builder.buildLeafNode(e, instanceOffset, storageOccupied, occursIndex);
                    node.addNode(leafNode);

                    storageOffset += storageOccupied;

                } else if (child instanceof Renames66Item re) {
                    LeafNode from = resolveAliasRecursive(re.getFrom().getName(), node);
                    LeafNode thru = (re.getThru() != null)
                        ? resolveAliasRecursive(re.getThru().getName(), node)
                        : null;

                    LeafNode leafNode = Builder.buildLeafNode(re, from, thru);
                    node.addNode(leafNode);

                    // RENAMES does not advance storage offset

                } else {
                    throw new IllegalStateException(
                        "Unsupported DataItem type: " + child.getClass().getSimpleName()
                    );
                }
            }
        }

        return storageOffset;
    }

    /**
     * 找同層的 GroupNode 或 LeafNode
     *
     * @throws CompileException
     */
    private static StorageNode resolveAlias(String name, StorageNode parent) {
        // Assumes node names are unique within the same level

        for (StorageNode node : parent.getChildren()) {
            if (node.getName().equals(name)) return node;
        }

        throw new CompileException(
            "Cannot resolve REDEFINES target '" + name + "' in group '" + parent.getName() + "'."
        );
    }

    /**
     * 遞迴搜尋 StorageNode（包含所有子節點）
     *
     * @throws CompileException
     */
    private static LeafNode resolveAliasRecursive(String name, StorageNode parent) {
        LeafNode result = tryResolveAliasRecursive(name, parent);
        if (result == null)
            throw new CompileException(
                "Cannot resolve RENAMES target '" + name + "' in group '" + parent.getName() + "'."
            );
        return result;
    }

    private static LeafNode tryResolveAliasRecursive(String name, StorageNode parent) {
        for (StorageNode node : parent.getChildren()) {
            // 檢查自己
            if (node.getName().equals(name) && node instanceof LeafNode leafNode)
                return leafNode;

            // 遞迴往下
            LeafNode found = tryResolveAliasRecursive(name, node);
            if (found != null)
                return found;
        }

        return null;
    }
}
