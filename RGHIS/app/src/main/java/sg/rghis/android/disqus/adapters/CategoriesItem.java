package sg.rghis.android.disqus.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.Category;
import sg.rghis.android.utils.ColorUtils;
import sg.rghis.android.views.widgets.RoundedLetterView;

public class CategoriesItem extends RecyclerView.ViewHolder implements ViewHolderItem {
    @Inject
    Context context;

    @Bind(R.id.title)
    TextView titleTextView;

    @Bind(R.id.image)
    RoundedLetterView imageView;

    public CategoriesItem(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        injectThis();
    }

    public static CategoriesItem createInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item, parent, false);
        return new CategoriesItem(view);
    }

    @Override
    public ViewHolderType getViewItemType() {
        return ViewHolderType.CATEGORIES;
    }

    @Override
    public void onBindViewHolder(Object data) {
        Category category = (Category) data;

        titleTextView.setText(category.title);

        int colorRes = ColorUtils.getColorGenerator().getColor(category.title);
        int color = ContextCompat.getColor(context, colorRes);
        imageView.setInitials(String.valueOf(category.title.charAt(0)));
        imageView.setBackgroundColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setTransitionName(context.getString(R.string.avatar_transition) + category.id);
        }
    }

    @Override
    public void injectThis() {
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
    }
}
