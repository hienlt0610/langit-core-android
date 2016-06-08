package core.helper.fetchable.list;

public interface PinableSectionInterface {
    /**
     * This method shall return 'true' if views of given type has to be pinned.
     */
    boolean isItemViewTypePinned(int viewType);
}
