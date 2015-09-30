package sg.rghis.android.views.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.rghis.android.fragments.HtmlTextFragment;

public class HealthDetailFragmentPagerAdapter extends FragmentPagerAdapter {
    private final static int MAX_TAB_SIZE = 3;
    private final Context context;
    private List<String> urls;
    private SparseIntArray indices;

    public HealthDetailFragmentPagerAdapter(Context context, FragmentManager fm, Map<Integer, String> urlMap) {
        super(fm);
        this.context = context;
        this.urls = new ArrayList<>();
        this.indices = new SparseIntArray();
        int index = 0;
        if (urlMap != null)
            for (int i = 0; i < MAX_TAB_SIZE; i++) {
                if (urlMap.containsKey(i)) {
                    this.urls.add(urlMap.get(i));
                    indices.put(index++, i);
                }
            }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (indices.get(position)) {
            case 1:
                return "Symptoms";
            case 2:
                return "Treatment";
            default:
            case 0:
                return "Overview";
        }
    }

    @Override
    public Fragment getItem(int position) {
        return HtmlTextFragment.newInstance(getUrlByPosition(position));
    }

    private String getUrlByPosition(int position) {
        return urls.get(position);
    }

    @Override
    public int getCount() {
        if (urls == null)
            return 0;
        return urls.size();
    }
}
