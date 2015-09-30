package sg.rghis.android.views;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import sg.rghis.android.R;
import sg.rghis.android.fragments.BaseFragment;
import sg.rghis.android.fragments.CategoriesFragment;
import sg.rghis.android.fragments.EmergencyInfoFragment;
import sg.rghis.android.fragments.HealthInfoFragment;
import sg.rghis.android.fragments.NewsFragment;
import sg.rghis.android.fragments.ProfileFragment;
import sg.rghis.android.fragments.SignupFragment;
import sg.rghis.android.fragments.ThreadsFragment;
import sg.rghis.android.models.User;
import sg.rghis.android.utils.SystemUtils;
import sg.rghis.android.utils.UserManager;
import sg.rghis.android.views.drawable.IconicsDrawable;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements BaseFragment.OnViewReadyListener {
    private static final int PROFILE_SETTING = 1;

    @IntDef({STATE_SIGNUP, STATE_NEWS, STATE_HEALTH_INFO,
            STATE_EMERGENCY_INFO, STATE_CATEGORIES, STATE_THREADS, STATE_PROFILE})
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
    private IntEvaluator evaluator = new IntEvaluator();
    private ColorStateList itemTextColor;
    private DrawerArrowDrawable arrowDrawable;
    private MaterialDialog loginDialog;
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


    @FragmentState
    private int currentState = STATE_HEALTH_INFO;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        navigateToState(STATE_NEWS, null, false);

        setupNavigationDrawer();

        slideInAnim = AnimationUtils.loadAnimation(this,
                R.anim.slide_in_right);
        slideOutAnim = AnimationUtils.loadAnimation(this,
                R.anim.slide_out_left);

        viewSwitcher.setInAnimation(slideInAnim);
        viewSwitcher.setOutAnimation(slideOutAnim);
    }

    private void setupNavigationDrawer() {
        drawerWidth = getResources().getDimensionPixelSize(R.dimen.navigation_drawer_width);
        int leftMargin = (int) SystemUtils.getDimensAttr(
                this, android.R.attr.listPreferredItemPaddingLeft);

        setupHeaderView();
        updateHeaderView();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        arrowDrawable = new DrawerArrowDrawable(this);
        toolbar.setNavigationIcon(arrowDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slidingPaneLayout.isOpen()) {
                    slidingPaneLayout.closePane();
                } else {
                    slidingPaneLayout.openPane();
                }
            }
        });

        itemTextColor = navigationView.getItemTextColor();

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.drawer_news)
                .setIcon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_rss_box)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));
        menu.findItem(R.id.drawer_health_info).
                setIcon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_hospital)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));
        menu.findItem(R.id.drawer_emergency_info).
                setIcon(new IconicsDrawable(this)
                        .icon(CommunityMaterial.Icon.cmd_alert)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));
        menu.findItem(R.id.drawer_forum)
                .setIcon(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_forum)
                        .sizeDp(24)
                        .paddingDp(2)
                        .colorRes(R.color.divider2));
        menu.findItem(R.id.drawer_settings)
                .setIcon(new IconicsDrawable(this)
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
                return false;
            }
        });

        // Set the drawer in its initial position
        updateDrawerState(0);

        slidingPaneLayout.setShadowResourceLeft(R.drawable.material_drawer_shadow_left);

        SlidingPaneLayout.LayoutParams containerLp = (SlidingPaneLayout.LayoutParams) container.getLayoutParams();
        drawerLeftMargin = leftMargin * 2 + Utils.convertDpToPx(this, 24);
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

    private void setupHeaderView() {
        headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        avatarView = (CircleImageView) headerView.findViewById(R.id.avatar_view);
        nameTextView = (TextView) headerView.findViewById(R.id.name_view);
        usernameTextView = (TextView) headerView.findViewById(R.id.username_view);
        profileView = headerView.findViewById(R.id.profile_view);

        startPx = SystemUtils.getActionBarHeight(MainActivity.this);
        endPx = getResources().getDimensionPixelSize(R.dimen.header_view_height);
        avatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size);
        avatarMiniSize = getResources().getDimensionPixelSize(R.dimen.avatar_mini_size);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UserManager.isLoggedIn())
                    navigateToState(STATE_SIGNUP, null, true);
                else {
                    navigateToState(STATE_PROFILE, null, true);
                }
            }
        });
    }

    private void updateHeaderView() {
        if (UserManager.isLoggedIn()) {
            User currentUser = UserManager.getCurrentUser();
            String name = currentUser.getFirstName() + " " + currentUser.getLastName();
            String username = "@" + currentUser.getUsername();
            nameTextView.setText(name);
            usernameTextView.setVisibility(View.VISIBLE);
            usernameTextView.setText(username);
        } else {
            usernameTextView.setVisibility(View.GONE);
            nameTextView.setText(getString(R.string.login_signup));
        }
    }

    public int getCurrentState() {
        return currentState;
    }

    private BaseFragment getCurrentFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentByTag("fragment_" + getCurrentState());
    }

    public boolean navigateToState(@FragmentState int state, Bundle bundle, boolean addToBackStack) {
        if (currentState == state)
            return false;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = getFragmentByState(state, bundle);
        if (currentState == STATE_CATEGORIES && state == STATE_THREADS) {
            Fragment currrentFragment = getCurrentFragment();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Inflate transitions to apply
                Transition changeTransform = TransitionInflater.from(this).
                        inflateTransition(android.R.transition.move);
                Transition explodeTransform = TransitionInflater.from(this).
                        inflateTransition(android.R.transition.fade);

                // Setup exit transition on first fragment
                currrentFragment.setSharedElementReturnTransition(changeTransform);
//                currrentFragment.setExitTransition(explodeTransform);

                // Setup enter transition on second fragment
                f.setSharedElementEnterTransition(changeTransform);
                f.setEnterTransition(explodeTransform);

                // Find the shared element (in Fragment A)
                ImageView avatar = (ImageView) findViewById(R.id.image);

                // Add second fragment by replacing first
                ft.addSharedElement(avatar, getString(R.string.avatar_transition));
            }
        }
        ft.replace(R.id.content, f, "fragment_" + String.valueOf(state));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (addToBackStack)
            ft.addToBackStack(null);
        else
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ft.commit();
        currentState = state;
        if (f != null) {
            BaseFragment fragment = ((BaseFragment) f);
            if (fragment.getTitleRes() != -1) {
                setToolbarTitle(getString(fragment.getTitleRes()));
            }
        }
        return true;
    }

    private void updateCurrentTitle() {
        BaseFragment fragment = getCurrentFragment();
        if (fragment.getTitleRes() != -1) {
            setToolbarTitle(getString(fragment.getTitleRes()));
        }
    }

    @Override
    public void onViewReady(BaseFragment fragment) {
        checkIfPaneLayout(fragment);
        refreshSearchView(fragment);
    }

    private boolean isPaneLayout() {
        return isPaneLayout;
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
        int offsetWidth = (int) ((drawerWidth - drawerLeftMargin) * offset);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(titleSearchView.getLayoutParams());
        lp.width = leftContainerWidth + offsetWidth;

        FrameLayout.LayoutParams oldLp2 =
                (FrameLayout.LayoutParams) searchView.getLayoutParams();
        FrameLayout.LayoutParams lp2 =
                new FrameLayout.LayoutParams(searchView.getLayoutParams());
        lp2.setMargins(oldLp2.leftMargin, oldLp2.topMargin,
                0, oldLp2.bottomMargin);
        lp2.width = leftContainerWidth + drawerLeftMargin + offsetWidth
                - oldLp2.leftMargin;
        lp2.height = SystemUtils.getActionBarHeight(MainActivity.this)
                - oldLp2.topMargin - oldLp2.bottomMargin;

        titleSearchView.setLayoutParams(lp);
        searchView.setLayoutParams(lp2);
    }

    private BaseFragment getFragmentByState(@FragmentState int state, Bundle bundle) {
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

    private void updateDrawerState(float slideOffset) {
        int height = evaluator.evaluate(slideOffset, startPx, endPx);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height
        ));

        int marginLeft = ((LinearLayout.LayoutParams) avatarView.getLayoutParams()).leftMargin;
        int currentAvatarSize = evaluator.evaluate(slideOffset, avatarMiniSize, avatarSize);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                currentAvatarSize, currentAvatarSize);
        lp.setMargins(marginLeft, 0, 0, 0);
        avatarView.setLayoutParams(lp);

        float mappedOffset = slideOffset >= 0.5 ? (slideOffset - 0.5f) / 0.5f : 0;
        int alpha = evaluator.evaluate(mappedOffset, 0, 255);
        profileView.setAlpha(alpha);
        ColorStateList newColor = itemTextColor.withAlpha(alpha);
        navigationView.setItemTextColor(newColor);
        adjustTitleSearchView(slideOffset);

//        arrowDrawable.setProgress(slideOffset);
    }

    public boolean promptLogin() {
        if (!UserManager.isLoggedIn()) {
            if (loginDialog != null)
                loginDialog.cancel();
            loginDialog = new MaterialDialog.Builder(this)
                    .content("Please login or sign up to continue.")
                    .positiveText("OK")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            navigateToState(MainActivity.STATE_SIGNUP, null, true);
                        }
                    })
                    .show();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isDetailShowing) {
            hideDetailFragment();
        } else
            super.onBackPressed();
    }

    public void setToolbarTitle(CharSequence title) {
        toolbarTitleView.setText(title);
    }

    @OnClick({R.id.search_button, R.id.image_search_back})
    public void onSearchClicked() {
        if (searchView.getVisibility() == View.VISIBLE) {
            closeSearchView();
        } else {
            openSearchView();
        }
    }

    private void openSearchView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Animator animator = ViewAnimationUtils.createCircularReveal(searchView,
                    searchView.getWidth() - (int) SystemUtils.getActionBarHeight(this),
                    (int) SystemUtils.dpToPx(23),
                    0,
                    (float) Math.hypot(searchView.getWidth(), searchView.getHeight()));
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    searchEditText.requestFocus();
                    SystemUtils.showKeyboard(MainActivity.this);
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
            SystemUtils.showKeyboard(this);
        }
    }

    private void closeSearchView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Animator animatorHide = ViewAnimationUtils.createCircularReveal(searchView,
                    searchView.getWidth() - (int) SystemUtils.getActionBarHeight(this),
                    (int) SystemUtils.dpToPx(23),
                    (float) Math.hypot(searchView.getWidth(), searchView.getHeight()),
                    0);
            animatorHide.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    searchView.setVisibility(View.GONE);
                    SystemUtils.hideKeyboard(MainActivity.this);
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
            SystemUtils.hideKeyboard(MainActivity.this);
            searchView.setVisibility(View.GONE);
        }
        searchView.setEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchSubscription != null)
            searchSubscription.unsubscribe();
    }

    public void setDetailFragment(Fragment f) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.detail_content, f);
        ft.commit();
    }

    public void showDetailFragment() {
        viewSwitcher.showNext();
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
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

}
