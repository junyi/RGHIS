package sg.rghis.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.MaterialScrollBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Observer;
import sg.rghis.android.R;
import sg.rghis.android.models.EmergencyTopic;
import sg.rghis.android.views.RecyclerItemClickListener;
import sg.rghis.android.views.adapters.EmergencyTopicAdapter;
import sg.rghis.android.views.adapters.RealmEmergencyTopicAdapter;
import sg.rghis.android.views.widgets.DividerItemDecoration;

public class EmergencyInfoFragment extends BaseMasterDetailFragment {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.right_container)
    FrameLayout container;

    private Realm realm;
    private EmergencyTopicAdapter adapter;
    private EmergencyInfoDetailFragment detailFragment;

    public static EmergencyInfoFragment newInstance() {

        Bundle args = new Bundle();

        EmergencyInfoFragment fragment = new EmergencyInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getTitleRes() {
        return R.string.emergency_info;
    }

    @Override
    public boolean shouldShowSearchView() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_info_two_pane_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailFragment = EmergencyInfoDetailFragment.newInstance(isLargeLayout);
        setDetailFragment(detailFragment);
//        getChildFragmentManager()
//                .beginTransaction()
//                .replace(R.id.right_container, detailFragment)
//                .commit();

        realm = Realm.getDefaultInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        adapter = new EmergencyTopicAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        EmergencyTopic emergencyTopic = adapter.getItem(position);
                        detailFragment.bindData(emergencyTopic);
//                        if (!isLargeLayout && slidingPaneLayout != null) {
//                            slidingPaneLayout.closePane();
//                        }
                        if (!isLargeLayout)
                            showDetailFragment();
                        detailFragment.resetView();
                    }
                }));

        MaterialScrollBar materialScrollBar = new MaterialScrollBar(getContext(), recyclerView, true);
        materialScrollBar.addIndicator(new AlphabetIndicator(getContext()));

    }

    @Override
    public void onResume() {
        super.onResume();
        RealmResults<EmergencyTopic> results = realm.where(EmergencyTopic.class)
                .findAllSorted(EmergencyTopic.TITLE);
        RealmEmergencyTopicAdapter realmAdapter = new RealmEmergencyTopicAdapter(
                getActivity().getApplicationContext(), results, true);
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public Observer<CharSequence> getSearchObserver() {
        return new Observer<CharSequence>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(CharSequence searchTerm) {
                RealmResults<EmergencyTopic> results = realm.where(EmergencyTopic.class)
                        .contains(EmergencyTopic.TITLE, searchTerm.toString(), RealmQuery.CASE_INSENSITIVE)
                        .findAllSorted(EmergencyTopic.TITLE);

                RealmEmergencyTopicAdapter realmAdapter = new RealmEmergencyTopicAdapter(
                        getContext().getApplicationContext(), results, true);
                adapter.setRealmAdapter(realmAdapter);
                adapter.notifyDataSetChanged();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null)
            realm.close();
    }
}
