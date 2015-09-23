package sg.rghis.android.disqus.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sg.rghis.android.R;
import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.disqus.models.Category;

public class CategoriesItem extends RecyclerView.ViewHolder implements ViewHolderItem {
    @Inject
    Context context;

    @Bind(R.id.title)
    TextView titleTextView;

    @Bind(R.id.image)
    CircleImageView imageView;

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
    public void onBindViewHolder(Object data, int leftPixelOffset) {
        Category category = (Category) data;

        titleTextView.setText(category.title);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(category.title);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(72)
                .height(72)
                .endConfig()
                .buildRound(String.valueOf(category.title.charAt(0)), color);
        imageView.setImageDrawable(drawable);
    }

    @Override
    public void injectThis() {
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
    }
}
