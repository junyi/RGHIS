package sg.rghis.android;

import android.animation.IntEvaluator;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import sg.rghis.android.utils.SystemUtils;

public class MainActivity extends AppCompatActivity {
    private static final int PROFILE_SETTING = 1;

    //    @Bind(R.id.toolbar)
//    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view)
    NavigationView navigationView;
    @Bind(R.id.sliding_panel_layout)
    SlidingPaneLayout slidingPaneLayout;

    private View headerView;
    private int startPx;
    private int endPx;
    private IntEvaluator evaluator = new IntEvaluator();
    private ColorStateList itemTextColor;
    private DrawerArrowDrawable arrowDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        setSupportActionBar(toolbar);

//        setupDrawer(toolbar, savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new HealthInfoFragment())
                .commit();

        headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        AppBarLayout appBarLayout = (AppBarLayout) headerView;
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                headerView.findViewById(R.id.collapsing_toolbar);
        Toolbar toolbar = (Toolbar) headerView.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        startPx = SystemUtils.getActionBarHeight(MainActivity.this);
        endPx = Utils.convertDpToPx(MainActivity.this, 192);

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.drawer_home).

                setIcon(
                        new IconicsDrawable(this)
                                .icon(GoogleMaterial.Icon.gmd_home)
                                .alpha(137)

                );
        menu.findItem(R.id.drawer_health_info).

                setIcon(
                        new IconicsDrawable(this)

                                .

                                        icon(CommunityMaterial.Icon.cmd_hospital)

                                .

                                        alpha(137)

                );
        menu.findItem(R.id.drawer_settings).

                setIcon(
                        new IconicsDrawable(this)

                                .

                                        icon(GoogleMaterial.Icon.gmd_settings)

                                .

                                        alpha(137)

                );

        updateDrawerState(0);

        slidingPaneLayout.setShadowResourceLeft(R.drawable.material_drawer_shadow_left);
        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener()

                                                {
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


    private void updateDrawerState(float slideOffset) {
        int height = evaluator.evaluate(slideOffset, startPx, endPx);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height
        ));
        int alpha = evaluator.evaluate(slideOffset, 0, 255);
        ColorStateList newColor = itemTextColor.withAlpha(alpha);
        navigationView.setItemTextColor(newColor);

        arrowDrawable.setProgress(slideOffset);
    }


}
