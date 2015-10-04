package sg.rghis.android.disqus.adapters;

/**
 * Common interface for view holder items to implement
 */
public interface ViewHolderItem extends ViewBinderInterface {

    ViewHolderType getViewItemType();

    void onBindViewHolder(Object data);

    void injectThis();

}
