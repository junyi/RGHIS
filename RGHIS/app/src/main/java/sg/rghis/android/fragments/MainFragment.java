package sg.rghis.android.fragments;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.utils.Utils;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.hdodenhof.circleimageview.CircleImageView;
import icepick.Icepick;
import icepick.State;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import sg.rghis.android.R;
import sg.rghis.android.events.NavigationEvent;
import sg.rghis.android.events.OnViewReadyEvent;
import sg.rghis.android.events.SetDetailFragmentEvent;
import sg.rghis.android.events.SetToolbarTitleEvent;
import sg.rghis.android.events.ShowDetailFragmentEvent;
import sg.rghis.android.models.User;
import sg.rghis.android.utils.SystemUtils;
import sg.rghis.android.utils.UserManager;
import sg.rghis.android.views.drawable.IconicsDrawable;

public class MainFragment extends RxFragment implements BaseFragment.OnViewReadyListener {
    @IntDef({STATE_SIGNUP, STATE_NEWS, STATE_HEALTH_INFO,
            STATE_EMERGENCY_INFO, STATE_CATEGORIES, STATE_THREADS, STATE_PROFILE, STATE_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FragmentState {
    }

    public static final int STATE_SIGNUP = 1;
    public static final int STATE_NEWS = 2;
    public static final int STATE_HEALTH_INFO = 3;
    public static final int STATE_EMERGENCY_INFO = 4;
    public static final int STATE_CATEGORIES = 5;
    public static final int STATE_THREADS = 6;
    public static final int STATE_PROFILE = 7;
    public static final int STATE_NONE = -1;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.sliding_panel_layout)
    SlidingPaneLayout slidingPaneLayout;
    @Bind(R.id.content)
    FrameLayout container;
    @Bind(R.id.toolbar_main)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitleView;
    @Bind(R.id.custom_toolbar_view)
    LinearLayout titleSearchView;
    @Bind(R.id.search_button)
    View searchButton;
    @Bind(R.id.search_view)
    View searchView;
    @Bind(R.id.edit_text_search)
    EditText searchEditText;
    @Bind(R.id.view_switcher)
    ViewSwitcher viewSwitcher;
    @Bind(R.id.toolbar_progress_bar)
    View progressBar;

    private CircleImageView avatarView;
    private TextView nameTextView;
    private TextView usernameTextView;
    private View profileView;

    private View headerView;
    private int startPx;
    private int endPx;
    private int avatarMarginLeft;
    private IntEvaluator evaluator = new IntEvaluator();
    private ColorStateList itemTextColor;
    private DrawerArrowDrawable arrowDrawable;
    private boolean isPaneLayout = false;
    private int drawerLeftMargin;
    private int drawerWidth;
    private int leftContainerWidth;
    private int avatarSize;
    private int avatarMiniSize;
    private Subscription searchSubscription;
    private Animation slideInAnim;
    private Animation slideOutAnim;
    private boolean isDetailShowing = false;
    private final ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
    private int deviceOrientation = Configuration.ORIENTATION_PORTRAIT;
    private int nameTextColor;
    private int usernameTextColor;
    private OnViewReadyEvent onViewReadyEvent;

    @FragmentState
    @State
    int currentState = STATE_NONE;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        Icepick.restoreInstanceState(this, savedInstanceState);

        setupNavigationDrawer();

        slideInAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_in_right);
        slideOutAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_out_left);

        viewSwitcher.setInAnimation(slideInAnim);
        viewSwitcher.setOutAnimation(slideOutAnim);

        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager() != null && getCurrentFragment() != null)
                    updateCurrentTitle();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (currentState == STATE_NONE)
            currentState = STATE_NEWS;
        navigateToState(currentState, null, false);

        if (onViewReadyEvent != null) {
            handleOnViewReady(onViewReadyEvent);
            onViewReadyEvent = null;
        }
    }

    private void setupNavigationDrawer() {
        drawerWidth = getResources().getDimensionPixelSize(R.dimen.navigation_drawer_width);
        int leftMargin = (int) SystemUtils.getDimensAttr(
                getContext(), android.R.attr.listPreferredItemPaddingLeft);

        setupHeaderView();
        updateHeaderView();

        arrowDrawable = new DrawerArrowDrawable(getContext());
        toolbar.setNavigationIcon(arrowDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDetailShowing) {
                    hideDetailFragment();
                } else {
                    if (slidingPaneLayout.isOpen()) {
                        slidingPaneLayout.closePane();
                    } else {
                        slidingPaneLayout.openPane();
                    }
                }
            }
        });

        itemTextColor = navigationView.getItemTextColor();

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.drawer_news)
                .setIcon(new IconicsDrawable(getContext())
                        .icon(CommunityMaterial.Icon.cmd_rss_box)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));
        menu.findItem(R.id.drawer_health_info).
                setIcon(new IconicsDrawable(getContext())
                        .icon(CommunityMaterial.Icon.cmd_hospital)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));
        menu.findItem(R.id.drawer_emergency_info).
                setIcon(new IconicsDrawable(getContext())
                        .icon(CommunityMaterial.Icon.cmd_alert)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));
        menu.findItem(R.id.drawer_forum)
                .setIcon(new IconicsDrawable(getContext())
                        .icon(GoogleMaterial.Icon.gmd_forum)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));
        menu.findItem(R.id.drawer_settings)
                .setIcon(new IconicsDrawable(getContext())
                        .icon(GoogleMaterial.Icon.gmd_settings)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.drawer_news:
                        navigateToState(STATE_NEWS, null, false);
                        break;
                    case R.id.drawer_forum:
                        navigateToState(STATE_CATEGORIES, null, false);
                        break;
                    case R.id.drawer_health_info:
                        navigateToState(STATE_HEALTH_INFO, null, false);
                        break;
                    case R.id.drawer_emergency_info:
                        navigateToState(STATE_EMERGENCY_INFO, null, false);
                        break;
                }
                slidingPaneLayout.closePane();
                return false;
            }
        });

        // Set the drawer in its initial position
        updateDrawerState(0);

        slidingPaneLayout.setShadowResourceLeft(R.drawable.material_drawer_shadow_left);
        slidingPaneLayout.setSliderFadeColor(0);

        SlidingPaneLayout.LayoutParams containerLp = (SlidingPaneLayout.LayoutParams) container.getLayoutParams();
        drawerLeftMargin = leftMargin * 2 + Utils.convertDpToPx(getContext(), 24);
        containerLp.setMargins(drawerLeftMargin, 0, 0, 0);
        container.setLayoutParams(containerLp);

        slidingPaneLayout.setPanelSlideListener(
                new SlidingPaneLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        updateDrawerState(slideOffset);
                    }

                    @Override
                    public void onPanelOpened(View panel) {
                    }

                    @Override
                    public void onPanelClosed(View panel) {
                    }
                }
        );
    }

    private void updateDrawerMenu() {
        int id = -1;
        switch (currentState) {
            case STATE_NEWS:
                id = R.id.drawer_news;
                break;
            case STATE_HEALTH_INFO:
                id = R.id.drawer_health_info;
                break;
            case STATE_EMERGENCY_INFO:
                id = R.id.drawer_emergency_info;
                break;
            case STATE_CATEGORIES:
                id = R.id.drawer_forum;
                break;
        }
        if (id != -1) {
            navigationView.getMenu().findItem(id).setChecked(true);
            navigationView.setCheckedItem(id);
        } else {
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }

    }

    private void setupHeaderView() {
        headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        avatarView = (CircleImageView) headerView.findViewById(R.id.avatar_view);
        nameTextView = (TextView) headerView.findViewById(R.id.name_view);
        usernameTextView = (TextView) headerView.findViewById(R.id.username_view);
        profileView = headerView.findViewById(R.id.profile_view);

        startPx = SystemUtils.getActionBarHeight(getContext());
        endPx = getResources().getDimensionPixelSize(R.dimen.header_view_height);
        avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size);
        avatarMiniSize = getResources().getDimensionPixelSize(R.dimen.avatar_mini_size);
        avatarMarginLeft = ((LinearLayout.LayoutParams) avatarView.getLayoutParams()).leftMargin;

        nameTextColor = nameTextView.getCurrentTextColor();
        usernameTextColor = usernameTextView.getCurrentTextColor();

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserManager.isLoggedIn())
                    navigateToState(STATE_SIGNUP, null, true);
                else {
                    navigateToState(STATE_PROFILE, null, true);
                }
                slidingPaneLayout.closePane();
            }
        });
    }

    private boolean isLandscape() {
        return deviceOrientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void updateHeaderView() {
        if (UserManager.isLoggedIn()) {
            ParseUser currentUser = UserManager.getCurrentUser();
            String name = currentUser.getString(User.KEY_FIRST_NAME) + " " + currentUser.getString(User.KEY_LAST_NAME);
            String username = "@" + currentUser.getUsername();
            String avatarUrl = currentUser.getString(User.KEY_AVATAR_URL);
            nameTextView.setText(name);
            usernameTextView.setVisibility(View.VISIBLE);
            usernameTextView.setText(username);
            if (avatarUrl != null) {
                Picasso.with(getContext())
                        .load(avatarUrl)
                        .placeholder(R.drawable.avatar_placeholder)
                        .into(avatarView);
            }
        } else {
            usernameTextView.setVisibility(View.GONE);
            nameTextView.setText(getString(R.string.login_signup));
            Picasso.with(getContext())
                    .load(R.drawable.avatar_placeholder)
                    .into(avatarView);
        }
    }

    private BaseFragment getFragment(@FragmentState int state) {
        return (BaseFragment) getFragmentManager().findFragmentByTag("fragment_" + state);
    }

    private BaseFragment getCurrentFragment() {
        return getFragment(currentState);
    }

    @Subscribe
    public void handleNavigateToState(NavigationEvent event) {
        int state = event.state;
        Bundle bundle = event.bundle;
        boolean addToBackStack = event.addToBackStack;
        navigateToState(state, bundle, addToBackStack);
    }

    private boolean navigateToState(@FragmentState int state, Bundle bundle, boolean addToBackStack) {
        if (state == currentState && getFragment(state) != null && getFragment(state).isVisible())
            return false;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment f = getNewFragment(state, bundle);
        if (currentState == STATE_CATEGORIES && state == STATE_THREADS) {
            Fragment currrentFragment = getCurrentFragment();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Inflate transitions to apply
                Transition changeTransform = TransitionInflater.from(getContext()).
                        inflateTransition(android.R.transition.move);
                Transition explodeTransform = TransitionInflater.from(getContext()).
                        inflateTransition(android.R.transition.fade);

                // Setup exit transition on first fragment
                currrentFragment.setSharedElementReturnTransition(changeTransform);
//                currrentFragment.setExitTransition(explodeTransform);

                // Setup enter transition on second fragment
                f.setSharedElementEnterTransition(changeTransform);
                f.setEnterTransition(explodeTransform);

                // Find the shared element (in Fragment A)
                ImageView avatar = (ImageView) getView().findViewById(R.id.image);

                // Add second fragment by replacing first
                ft.addSharedElement(avatar, getString(R.string.avatar_transition));
            }
        }
        ft.replace(R.id.content, f, "fragment_" + String.valueOf(state));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (addToBackStack)
            ft.addToBackStack(null);
        else
            getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ft.commit();
        currentState = state;
        if (f != null) {
            BaseFragment fragment = ((BaseFragment) f);
            if (fragment.getTitleRes() != -1) {
                internalSetToolbarTitle(getString(fragment.getTitleRes()));
            } else {
                internalSetToolbarTitle("");
            }
        }
        updateDrawerMenu();
        return true;
    }

    private void updateCurrentTitle() {
        BaseFragment fragment = getCurrentFragment();
        if (fragment.getTitleRes() != -1) {
            internalSetToolbarTitle(getString(fragment.getTitleRes()));
        }
    }

    @Subscribe
    public void handleOnViewReady(OnViewReadyEvent event) {
        if (getView() == null) {
            onViewReadyEvent = event;
        } else {
            onViewReady(event.fragment);
        }
    }

    @Override
    public void onViewReady(BaseFragment fragment) {
        checkIfPaneLayout(fragment);
        refreshSearchView(fragment);
    }

    private void checkIfPaneLayout(BaseFragment fragment) {
        if (fragment != null && fragment.equals(getCurrentFragment())
                && fragment.getView() != null) {
            isPaneLayout = fragment.getView().findViewById(R.id.left_container) != null;
        } else {
            isPaneLayout = false;
        }
    }

    private void refreshSearchView(BaseFragment fragment) {
        if (fragment != null) {
            if (fragment.shouldShowSearchView()) {
                if (searchView.getVisibility() == View.VISIBLE)
                    closeSearchView();
                if (isPaneLayout) {
                    final View view = fragment.getView().findViewById(R.id.left_container);
                    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            leftContainerWidth = view.getWidth();
                            if (slidingPaneLayout.isOpen()) {
                                adjustTitleSearchView(1);
                            } else {
                                adjustTitleSearchView(0);
                            }
                            searchButton.setVisibility(View.VISIBLE);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        }
                    });
                    searchEditText.setText("");
                    if (searchSubscription != null)
                        searchSubscription.unsubscribe();
                    searchSubscription = RxTextView.textChanges(searchEditText)
                            .debounce(200, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(this.<CharSequence>bindToLifecycle())
                            .subscribe(fragment.getSearchObserver());
                }
            } else {
                if (searchView.getVisibility() == View.VISIBLE)
                    closeSearchView();
                searchButton.setVisibility(View.GONE);
            }
        }
    }

    private void adjustTitleSearchView(float offset) {
        if (leftContainerWidth != 0) {
            int offsetWidth = (int) ((drawerWidth - drawerLeftMargin) * offset);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(titleSearchView.getLayoutParams());
            lp.width = leftContainerWidth + offsetWidth;

            FrameLayout.LayoutParams oldLp2 =
                    (FrameLayout.LayoutParams) searchView.getLayoutParams();
            FrameLayout.LayoutParams lp2 =
                    new FrameLayout.LayoutParams(searchView.getLayoutParams());
            int rightMargin = isLandscape() ? 0 : oldLp2.leftMargin;
            lp2.setMargins(oldLp2.leftMargin, oldLp2.topMargin,
                    rightMargin, oldLp2.bottomMargin);
            lp2.width = leftContainerWidth + drawerLeftMargin + offsetWidth
                    - oldLp2.leftMargin;
            if (!isLandscape())
                lp2.width -= oldLp2.leftMargin;
            lp2.height = SystemUtils.getActionBarHeight(getContext())
                    - oldLp2.topMargin - oldLp2.bottomMargin;

            titleSearchView.setLayoutParams(lp);
            searchView.setLayoutParams(lp2);
        }
    }

    private BaseFragment getNewFragment(@FragmentState int state, Bundle bundle) {
        BaseFragment f = null;
        switch (state) {
            case STATE_SIGNUP:
                f = new SignupFragment();
                break;
            case STATE_NEWS:
                f = new NewsFragment();
                break;
            case STATE_HEALTH_INFO:
                f = new HealthInfoFragment();
                break;
            case STATE_EMERGENCY_INFO:
                f = new EmergencyInfoFragment();
                break;
            case STATE_CATEGORIES:
                f = new CategoriesFragment();
                break;
            case STATE_THREADS:
                f = new ThreadsFragment();
                break;
            case STATE_PROFILE:
                f = new ProfileFragment();
                break;
        }

        if (f != null) {
            f.setArguments(bundle);
        }

        return f;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        deviceOrientation = newConfig.orientation;
    }

    private void updateDrawerState(float slideOffset) {
        int height = evaluator.evaluate(slideOffset, startPx, endPx);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height
        ));

        int marginLeft = ((LinearLayout.LayoutParams) avatarView.getLayoutParams()).leftMargin;
        if (!isLandscape()) {
            marginLeft = evaluator.evaluate(slideOffset, avatarMarginLeft -
                    SystemUtils.dpToPx(2), avatarMarginLeft);
        }
        int currentAvatarSize = evaluator.evaluate(slideOffset, avatarMiniSize, avatarSize);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                currentAvatarSize, currentAvatarSize);
        lp.setMargins(marginLeft, 0, 0, 0);
        avatarView.setLayoutParams(lp);

        float mappedOffset = slideOffset >= 0.5 ? (slideOffset - 0.5f) / 0.5f : 0;
        int alpha = evaluator.evaluate(mappedOffset, 0, 255);
        nameTextView.setTextColor(ColorUtils.setAlphaComponent(nameTextColor, alpha));
        usernameTextView.setTextColor(ColorUtils.setAlphaComponent(usernameTextColor, alpha));
        profileView.setAlpha(alpha);
        ColorStateList newColor = itemTextColor.withAlpha(alpha);
        navigationView.setItemTextColor(newColor);
        if (isLandscape())
            adjustTitleSearchView(slideOffset);

//        arrowDrawable.setProgress(slideOffset);
    }

    public void showLogin() {
        if (getView() != null)
            navigateToState(STATE_SIGNUP, null, true);
        else
            currentState = STATE_SIGNUP;
    }

    public boolean onBackPressed() {
        if (isDetailShowing) {
            hideDetailFragment();
            return true;
        } else {
            return false;
        }
    }

    public void internalSetToolbarTitle(CharSequence title) {
        toolbarTitleView.setText(title);
    }

    @Subscribe
    public void handleSetToolbarTitle(SetToolbarTitleEvent event) {
        internalSetToolbarTitle(event.charSequence);
    }

    @OnClick({R.id.search_button, R.id.image_search_back})
    public void onSearchClicked() {
        if (searchView.getVisibility() == View.VISIBLE) {
            closeSearchView();
        } else {
            openSearchView();
        }
    }

    public void notifyLoginSuccess() {
        updateHeaderView();
    }

    public void notifyLogoutSuccess() {
        updateHeaderView();
        Toast.makeText(getContext(), "Sign out success!", Toast.LENGTH_SHORT).show();
        navigateToState(MainFragment.STATE_NEWS, null, false);
    }

    private void openSearchView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Animator animator = ViewAnimationUtils.createCircularReveal(searchView,
                    searchView.getWidth() - SystemUtils.getActionBarHeight(getContext()),
                    SystemUtils.dpToPx(23),
                    0,
                    (float) Math.hypot(searchView.getWidth(), searchView.getHeight()));
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    searchEditText.requestFocus();
                    SystemUtils.showKeyboard(getContext());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            searchView.setVisibility(View.VISIBLE);
            if (searchView.getVisibility() == View.VISIBLE) {
                animator.setDuration(300);
                animator.start();
                searchView.setEnabled(true);
            }

        } else {
            searchView.setVisibility(View.VISIBLE);
            searchView.setEnabled(true);
            searchEditText.requestFocus();
            SystemUtils.showKeyboard(getContext());
        }
    }

    private void closeSearchView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Animator animatorHide = ViewAnimationUtils.createCircularReveal(searchView,
                    searchView.getWidth() - SystemUtils.getActionBarHeight(getContext()),
                    SystemUtils.dpToPx(23),
                    (float) Math.hypot(searchView.getWidth(), searchView.getHeight()),
                    0);
            animatorHide.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    searchView.setVisibility(View.GONE);
                    SystemUtils.hideKeyboard(getActivity());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animatorHide.setDuration(300);
            animatorHide.start();
        } else {
            SystemUtils.hideKeyboard(getActivity());
            searchView.setVisibility(View.GONE);
        }
        searchView.setEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchSubscription != null)
            searchSubscription.unsubscribe();
    }

    @Subscribe
    public void handleSetDetailFragment(SetDetailFragmentEvent event) {
        setDetailFragment(event.fragment);
    }

    private void setDetailFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.detail_content, f);
        ft.commit();
    }

    @Subscribe
    public void handleShowDetailFragment(ShowDetailFragmentEvent event) {
        showDetailFragment();
    }

    private void showDetailFragment() {
        viewSwitcher.showNext();
        internalSetToolbarTitle("");
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                arrowDrawable.setProgress(slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        anim.start();
        searchButton.setVisibility(View.GONE);
        isDetailShowing = true;
    }

    public void hideDetailFragment() {
        viewSwitcher.showPrevious();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = 1 - (Float) valueAnimator.getAnimatedValue();
                arrowDrawable.setProgress(slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(500);
        anim.start();
        isDetailShowing = false;
        updateCurrentTitle();
        refreshSearchView(getCurrentFragment());
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            if (Build.VERSION.SDK_INT >= 21) {
                getActivity().getWindow().setStatusBarColor(SystemUtils.getColorAttr(getContext(),
                        android.R.attr.colorPrimaryDark));
            }
        }
    }

}
