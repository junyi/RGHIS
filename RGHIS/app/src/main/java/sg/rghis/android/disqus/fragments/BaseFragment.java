package sg.rghis.android.disqus.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import sg.rghis.android.disqus.DisqusSdkProvider;


public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
    }
}
