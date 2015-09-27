package sg.rghis.android.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import sg.rghis.android.BuildConfig;
import sg.rghis.android.R;
import sg.rghis.android.disqus.adapters.ThreadsAdapter;
import sg.rghis.android.disqus.models.PaginatedList;
import sg.rghis.android.disqus.models.ResponseItem;
import sg.rghis.android.disqus.models.Thread;
import sg.rghis.android.disqus.services.CategoriesService;
import sg.rghis.android.disqus.services.ThreadsService;
import sg.rghis.android.disqus.utils.UrlUtils;
import sg.rghis.android.views.RecyclerItemClickListener;
import sg.rghis.android.views.widgets.DividerItemDecoration;
import timber.log.Timber;

public class ThreadsFragment extends BaseDisqusFragment implements Validator.ValidationListener {
    public static final String PREFIX_ADAPTER = ".ThreadsFragment.MyAdapter";

    @Inject
    CategoriesService categoriesService;

    @Inject
    ThreadsService threadsService;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;

    private Subscription subscription;
    private Subscription createThreadSubscription;
    private ThreadsAdapter threadsAdapter;
    private long categoryId;
    private MaterialDialog dialogInstance = null;
    private final DialogViewHolder dialogViewHolder = new DialogViewHolder();
    private Validator validator;

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

    public static ThreadsFragment newInstance(long categoryId) {
        Bundle args = new Bundle();
        args.putLong(ThreadsContainerFragment.ARG_CATEGORY_ID, categoryId);

        ThreadsFragment fragment = new ThreadsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle == null || !bundle.containsKey(ThreadsContainerFragment.ARG_CATEGORY_ID)) {
            throw new IllegalStateException("Category ID must be provided.");
        } else {
            categoryId = bundle.getLong(ThreadsContainerFragment.ARG_CATEGORY_ID);
        }

        threadsAdapter = new ThreadsAdapter(categoryId);

        if (savedInstanceState != null) {
            threadsAdapter.onRestoreInstanceState(PREFIX_ADAPTER, savedInstanceState);
        } else {
            Observable<PaginatedList<Thread>> observable =
                    categoriesService.listThreads(categoryId, UrlUtils.author(), null);
            subscription = observable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new GetThreadsObserver());
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thread_list_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(threadsAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Thread thread = (Thread) threadsAdapter.getItem(position);
                        ((ThreadsContainerFragment) getParentFragment()).loadPosts(thread.id);
                    }
                }));

    }

    @OnClick(R.id.fab)
    public void showCreateThreadDialog() {
        if (dialogInstance != null)
            dialogInstance.cancel();

        MaterialDialog.Builder dialogBuilder = getDialogBuilder();

        dialogInstance = dialogBuilder.build();
        bindDialogViews(dialogInstance, dialogViewHolder);

        dialogInstance.show();
    }

    private MaterialDialog.Builder getDialogBuilder() {

        boolean wrapInScrollView = true;
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity())
                .backgroundColorRes(R.color.white)
                .title("Create Thread")
                .customView(R.layout.dialog_create_thread, wrapInScrollView)
                .positiveText("Create")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String title = dialogViewHolder.titleEditText.getText().toString();
                        String message = dialogViewHolder.messageEditText.getText().toString();
                        Map<String, String> extras = new UrlUtils.MapBuilder()
                                .add("category", String.valueOf(categoryId))
                                .add("message", message)
                                .build();

                        Observable<ResponseItem<Thread>> observable =
                                threadsService.create(BuildConfig.FORUM_SHORTNAME, title, extras);
                        createThreadSubscription = observable.observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new CreateThreadObserver());

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialogInstance = null;
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
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
            threadsAdapter.addItem(threadResponseItem.getResponse());
            threadsAdapter.notifyDataSetChanged();
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
