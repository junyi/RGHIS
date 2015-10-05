package sg.rghis.android.fragments;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.dynamicbox.DynamicBox;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import sg.rghis.android.BuildConfig;
import sg.rghis.android.R;
import sg.rghis.android.disqus.adapters.ThreadsAdapter;
import sg.rghis.android.disqus.models.AuthorlessThread;
import sg.rghis.android.disqus.models.Category;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.ResponseItem;
import sg.rghis.android.disqus.models.Thread;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.disqus.services.ThreadsService;
import sg.rghis.android.disqus.utils.UrlUtils;
import sg.rghis.android.utils.ColorUtils;
import sg.rghis.android.utils.DisqusUtils;
import sg.rghis.android.utils.SystemUtils;
import sg.rghis.android.views.RecyclerItemClickListener;
import sg.rghis.android.views.widgets.RGSlidingPaneLayout;
import sg.rghis.android.views.widgets.RoundedLetterView;
import timber.log.Timber;

public class ThreadsFragment extends BaseDisqusFragment implements Validator.ValidationListener {
    public final static String ARG_CATEGORY = "category";
    public static final String PREFIX_ADAPTER = ".ThreadsFragment.MyAdapter";

    @Inject
    CategoriesService categoriesService;

    @Inject
    ThreadsService threadsService;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Bind(R.id.dummy_view)
    FrameLayout dummyView;

    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Bind(R.id.image)
    RoundedLetterView avatarView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    private Category category;
    private PostListFragment postsFragment;
    private RGSlidingPaneLayout slidingPaneLayout;
    private boolean isLargeLayout = false;

    private Subscription subscription;
    private Subscription createThreadSubscription;
    private ThreadsAdapter threadsAdapter;
    private MaterialDialog dialogInstance = null;
    private final DialogViewHolder dialogViewHolder = new DialogViewHolder();
    private Validator validator;
    private DynamicBox dynamicBox;

    static class DialogViewHolder {
        @NotEmpty
        @Nullable
        @Bind(R.id.title_edit_text)
        EditText titleEditText;

        @NotEmpty
        @Nullable
        @Bind(R.id.message_edit_text)
        EditText messageEditText;

        @Nullable
        @Bind(R.id.title_text_input_layout)
        TextInputLayout titleTextInputLayout;

        @Nullable
        @Bind(R.id.message_text_input_layout)
        TextInputLayout messageTextInputLayout;
    }

    public static ThreadsFragment newInstance(Category category) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_CATEGORY, category);

        ThreadsFragment fragment = new ThreadsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(ThreadsFragment.ARG_CATEGORY)) {
            throw new IllegalStateException("Category must be provided.");
        } else {
            if (bundle.containsKey(ThreadsFragment.ARG_CATEGORY))
                category = bundle.getParcelable(ThreadsFragment.ARG_CATEGORY);
        }

        threadsAdapter = new ThreadsAdapter(category.id);

        if (savedInstanceState != null) {
            threadsAdapter.onRestoreInstanceState(PREFIX_ADAPTER, savedInstanceState);
        } else {
            Observable<PaginatedList<Thread>> observable =
                    categoriesService.listThreads(category.id,
                            UrlUtils.author(), null)
                            .flatMap(new Func1<PaginatedList<Thread>, Observable<PaginatedList<Thread>>>() {
                                @Override
                                public Observable<PaginatedList<Thread>> call(PaginatedList<Thread> threadPaginatedList) {
                                    ArrayList<Thread> newThreads = new ArrayList<>();
                                    ArrayList<Thread> threads = threadPaginatedList.getResponseData();
                                    int size = threadPaginatedList.getResponseData().size();
                                    for (int i = 0; i < size; i++) {
                                        Thread thread = threads.get(i);
                                        if (!thread.isClosed)
                                            newThreads.add(thread);
                                    }
                                    PaginatedList<Thread> newList =
                                            new PaginatedList<Thread>(threadPaginatedList.getCursor(),
                                                    threadPaginatedList.getCode(), newThreads);
                                    return Observable.just(newList);
                                }
                            });

            subscription = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GetThreadsObserver());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.threads_two_pane_layout, container, false);
        ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            avatarView.setTransitionName(getString(R.string.avatar_transition) + category.id);
        }
        toolbar.setTitle(category.title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getContext(),
                R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        determineCurrentLayout();

        dynamicBox = new DynamicBox(getContext(), dummyView);
        dynamicBox.showLoadingLayout();
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(threadsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Thread thread = (Thread) threadsAdapter.getItem(position);
                        loadPosts(thread);
                    }
                }));

        int colorRes = ColorUtils.getColorGenerator().getColor(category.title);
        int color = ContextCompat.getColor(getContext(), colorRes);
        int bgColor = ColorUtils.combineColors(0.4f, color, 0x000000);
        avatarView.setInitials(String.valueOf(category.title.charAt(0)));
        avatarView.setBackgroundColor(color);
        appBarLayout.setBackgroundColor(bgColor);
        if (Build.VERSION.SDK_INT >= 21) {
            int darkerColor = ColorUtils.darker(bgColor, 1.3f);
            getActivity().getWindow().setStatusBarColor(darkerColor);
        }

    }

    private void determineCurrentLayout() {
        if (getView() != null) {
            isLargeLayout = getView().findViewById(R.id.two_pane_divider) != null;

            if (!isLargeLayout) {
                slidingPaneLayout = (RGSlidingPaneLayout) getView()
                        .findViewById(R.id.sliding_panel_layout);
                slidingPaneLayout.setShadowResourceLeft(
                        R.drawable.material_drawer_shadow_left);
                slidingPaneLayout.openPane();
            }
        }
    }

    public void closeDetailPane() {
        if (!isLargeLayout && slidingPaneLayout != null) {
            slidingPaneLayout.openPane();
        }
    }

    private void loadPosts(Thread thread) {
        postsFragment = PostListFragment.newInstance(thread);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.right_container, postsFragment)
                .commit();
        if (!isLargeLayout && slidingPaneLayout != null) {
            slidingPaneLayout.closePane();
        }
    }

    @OnClick(R.id.fab)
    public void showCreateThreadDialog() {
        if (!SystemUtils.promptLoginIfNecessary()) {
            if (dialogInstance != null)
                dialogInstance.cancel();

            MaterialDialog.Builder dialogBuilder = getDialogBuilder();

            dialogInstance = dialogBuilder.build();
            bindDialogViews(dialogInstance, dialogViewHolder);

            dialogInstance.show();
        }
    }

    private MaterialDialog.Builder getDialogBuilder() {

        boolean wrapInScrollView = true;
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity());
        dialogBuilder.backgroundColorRes(R.color.white);
        dialogBuilder.title("Create Thread");
        dialogBuilder.customView(R.layout.dialog_create_thread, wrapInScrollView);
        dialogBuilder.positiveText("Create");
        dialogBuilder.negativeText("Cancel");
        dialogBuilder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                String title = dialogViewHolder.titleEditText.getText().toString();
                String message = dialogViewHolder.messageEditText.getText().toString();
                Map<String, String> extras = new UrlUtils.MapBuilder()
                        .add("message", message)
                        .build();

                Observable<ResponseItem<Thread>> observable =
                        threadsService.create(BuildConfig.FORUM_SHORTNAME, title, extras)
                                .flatMap(new Func1<ResponseItem<AuthorlessThread>, Observable<ResponseItem<AuthorlessThread>>>() {
                                    @Override
                                    public Observable<ResponseItem<AuthorlessThread>> call(ResponseItem<AuthorlessThread> threadResponseItem) {
                                        AuthorlessThread authorlessThread = threadResponseItem.getResponse();

                                        return threadsService.update(String.valueOf(authorlessThread.id),
                                                String.valueOf(category.id),
                                                null);
                                    }
                                }).flatMap(new Func1<ResponseItem<AuthorlessThread>, Observable<ResponseItem<Thread>>>() {
                            @Override
                            public Observable<ResponseItem<Thread>> call(ResponseItem<AuthorlessThread> authorlessThreadResponseItem) {
                                AuthorlessThread authorlessThread = authorlessThreadResponseItem.getResponse();
                                return threadsService.details(authorlessThread.id,
                                        BuildConfig.FORUM_SHORTNAME, UrlUtils.author());
                            }
                        });

                createThreadSubscription = observable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CreateThreadObserver());

            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
        dialogBuilder.cancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogInstance = null;
            }
        });
        dialogBuilder.dismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialogInstance = null;
            }
        });

        return dialogBuilder;
    }

    private void bindDialogViews(MaterialDialog dialog, final DialogViewHolder dialogViewHolder) {
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        ButterKnife.bind(dialogViewHolder, dialog);

        validator = new Validator(dialogViewHolder);
        validator.setValidationListener(this);

        if (dialogViewHolder.titleEditText == null || dialogViewHolder.messageEditText == null) {
            return;
        }

        dialogViewHolder.titleEditText.setTag(dialogViewHolder.titleTextInputLayout);
        dialogViewHolder.messageEditText.setTag(dialogViewHolder.messageTextInputLayout);

        dialogViewHolder.titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                dialogViewHolder.titleTextInputLayout.setErrorEnabled(false);
                dialogViewHolder.messageTextInputLayout.setErrorEnabled(false);
                validator.validate();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialogViewHolder.messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialogViewHolder.titleTextInputLayout.setErrorEnabled(false);
                dialogViewHolder.messageTextInputLayout.setErrorEnabled(false);
                validator.validate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        if (dialogInstance != null) {
            View postiveButton = dialogInstance.getActionButton(DialogAction.POSITIVE);
            if (postiveButton != null) {
                postiveButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        if (dialogInstance != null) {
            View postiveButton = dialogInstance.getActionButton(DialogAction.POSITIVE);
            if (postiveButton != null) {
                postiveButton.setEnabled(false);
            }
        }
    }

    private class GetThreadsObserver implements Observer<PaginatedList<Thread>> {
        @Override
        public void onCompleted() {
            dynamicBox.hideAll();
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(Log.getStackTraceString(e));
        }

        @Override
        public void onNext(PaginatedList<Thread> threadPaginatedList) {
            threadsAdapter.addList(threadPaginatedList);
            threadsAdapter.notifyDataSetChanged();
        }
    }

    private class CreateThreadObserver implements Observer<ResponseItem<Thread>> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(Log.getStackTraceString(e));
        }

        @Override
        public void onNext(ResponseItem<Thread> threadResponseItem) {
            Thread thread = threadResponseItem.getResponse();
            threadsAdapter.addItem(thread);
            threadsAdapter.notifyDataSetChanged();
            ParsePush.subscribeInBackground(DisqusUtils.getChannelName(thread.id), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Timber.d(Log.getStackTraceString(e));
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null)
            subscription.unsubscribe();
        if (createThreadSubscription != null)
            createThreadSubscription.unsubscribe();
    }
}
