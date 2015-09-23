package sg.rghis.android.views.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

import sg.rghis.android.fragments.HtmlTextFragment;
import sg.rghis.android.models.HealthTopic;
import timber.log.Timber;

public class HealthDetailFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int TAB_SIZE = 3;
    private final Context context;
    private final int viewPagerId;
    private HealthTopic healthTopic;
    private List<String> tabTags;
    private final FragmentManager fragmentManager;
    private SparseArray<Fragment> fragments;

    public HealthDetailFragmentPagerAdapter(Context context, FragmentManager fm, int viewPagerId) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
        this.viewPagerId = viewPagerId;
    }

    private void clearAllFragments() {
        int size = fragments.size();
        for (int i = 0; i < size; i++)
            fragmentManager.beginTransaction().remove(fragments.get(i)).commit();
        fragments.clear();
    }

    @Override
    public Fragment getItem(int position) {
        return HtmlTextFragment.newInstance(getUrlByPosition(position));
    }

    private String getUrlByPosition(int position) {
        String url = null;

        if (healthTopic != null)
            switch (position) {
                case 0:
                    url = healthTopic.getOverview();
                    break;
                case 1:
                    url = healthTopic.getSymptoms();
                    break;
                case 2:
                    url = healthTopic.getTreatment();
                    break;

            }
        return url;
    }

    public void setHealthTopic(HealthTopic healthTopic) {
        this.healthTopic = healthTopic;

        for (int i = 0; i < TAB_SIZE; i++) {
            Fragment f = fragmentManager.findFragmentByTag(getFragmentName(viewPagerId, getItemId(i)));
            Timber.d("Fragment is null", f == null);
            if (f != null) {
                ((HtmlTextFragment) f).setUrl(context, getUrlByPosition(i));
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return "Symptoms";
            case 2:
                return "Treatment";
            default:
            case 0:
                return "Overview";
        }
    }

    private static String getFragmentName(int viewPagerId, long index) {
        return "android:switcher:" + viewPagerId + ":" + index;
    }

    @Override
    public int getCount() {
        return TAB_SIZE;
    }

//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
