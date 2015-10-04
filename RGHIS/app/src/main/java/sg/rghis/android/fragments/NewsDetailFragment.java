package sg.rghis.android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.rghis.android.R;
import sg.rghis.android.utils.SystemUtils;
import sg.rghis.android.views.MainActivity;

public class NewsDetailFragment extends Fragment {
    public static final String ARG_URL = "url";
    private static final String ARG_LARGE_LAYOUT = "large_layout";

    @Bind(R.id.webview)
    WebView webView;

    private String url;
    private boolean isLargeLayout = false;

    public static NewsDetailFragment newInstance(boolean isLargeLayout, String url) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_LARGE_LAYOUT, isLargeLayout);
        args.putString(ARG_URL, url);

        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(ARG_URL)) {
            throw new IllegalStateException("URL must be provided.");
        } else {
            url = bundle.getString(ARG_URL);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_detail_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isLargeLayout = getArguments().getBoolean(ARG_LARGE_LAYOUT);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!url.equals("about:blank")) {
                    if (!isLargeLayout)
                        setToolbarTitle(view.getTitle());
                    hideProgressBar();
                }
            }
        });
        if (url != null) {
            webView.loadUrl("about:blank");
            webView.loadUrl(url);
            showProgressBar();
        }

    }

    public void loadUrl(String url) {
        this.url = url;
        if (webView != null) {
            hideProgressBar();
            webView.loadUrl("about:blank");
            webView.loadUrl(url);
            hideProgressBar();
        }
    }

    private void setToolbarTitle(CharSequence s) {
        MainActivity activity = SystemUtils.getMainActivityFromContext(getContext());
        activity.getMainFragment().setToolbarTitle(s);
    }

    private void hideProgressBar() {
        MainActivity activity = SystemUtils.getMainActivityFromContext(getContext());
        activity.getMainFragment().hideProgressBar();
    }

    private void showProgressBar() {
        MainActivity activity = SystemUtils.getMainActivityFromContext(getContext());
        activity.getMainFragment().showProgressBar();
    }

    @OnClick(R.id.fab)
    public void openInBrowser() {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }
        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(openUrlIntent);
    }

    public void stopLoading() {
        if (webView != null) {
            webView.stopLoading();
        }
    }

}
