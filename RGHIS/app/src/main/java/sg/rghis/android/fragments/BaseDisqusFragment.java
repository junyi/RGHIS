package sg.rghis.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import sg.rghis.android.disqus.DisqusSdkProvider;
import sg.rghis.android.views.MainActivity;


public abstract class BaseDisqusFragment extends BaseFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DisqusSdkProvider.getInstance().getObjectGraph().inject(this);
    }
}