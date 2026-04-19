package getthepicture.copybook.resolver.storage.core;

public final class StorageAlias {

    private final StorageNode target;

    public StorageAlias(StorageNode target) {
        this.target = target;
    }

    public StorageNode getTarget() {
        return target;
    }
}
