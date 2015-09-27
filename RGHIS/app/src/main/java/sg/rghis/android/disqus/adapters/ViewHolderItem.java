package sg.rghis.android.disqus.adapters;

/**
 * Common interface for view holder items to implement
 */
public interface ViewHolderItem {

    public ViewHolderType getViewItemType();
    public void onBindViewHolder(Object data);
    public void injectThis();

}
