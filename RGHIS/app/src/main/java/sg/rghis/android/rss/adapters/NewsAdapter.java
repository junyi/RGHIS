package sg.rghis.android.rss.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.rss.models.Item;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.RssItem> {

    private List<Item> rssItemList;

    public static class RssItem extends RecyclerView.ViewHolder {
        @Bind(R.id.title)
        TextView titleTextView;

        public RssItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public NewsAdapter() {
        this.rssItemList = new ArrayList<>();
    }

    @Override
    public RssItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new RssItem(v);
    }

    @Override
    public void onBindViewHolder(RssItem holder, int position) {
        Item rssItem = rssItemList.get(position);
        holder.titleTextView.setText(rssItem.getTitle());
    }

    public void addList(List<Item> items) {
        rssItemList.addAll(items);
    }

    public Item getItem(int position) {
        return rssItemList.get(position);
    }

    @Override
    public int getItemCount() {
        return rssItemList.size();
    }
}
