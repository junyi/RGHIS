package sg.rghis.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.utils.HtmlTagHandler;
import sg.rghis.android.utils.SystemUtils;

public class HtmlTextFragment extends Fragment {
    public static final String ARG_URL = "url";

    @Bind(R.id.text)
    TextView textView;
    @Bind(R.id.scroll_view)
    NestedScrollView scrollView;

    private String url;
    private CharSequence parsedHtml;

    public static HtmlTextFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        HtmlTextFragment fragment = new HtmlTextFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.html_text_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUrl(view.getContext(), getArguments().getString(ARG_URL));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (parsedHtml != null) {
            textView.setText(parsedHtml);
        }
        scrollView.scrollTo(0, 0);
    }

    public void setUrl(Context context, String url) {
        this.url = url;
        if (url != null && !url.equals("null")) {
            String html = SystemUtils.loadAssetTextAsString(context, "content/" + url);
            if (html != null) {
                html = Jsoup.clean(html, Whitelist.basic());
                parsedHtml = Html.fromHtml(html, null, new HtmlTagHandler());
                if (parsedHtml != null && textView != null) {
                    textView.setText(parsedHtml);
                }
            }
        }
    }
}
