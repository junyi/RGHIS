package sg.rghis.android.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import sg.rghis.android.R;
import sg.rghis.android.events.PromptLoginEvent;
import sg.rghis.android.fragments.MainFragment;
import sg.rghis.android.utils.UserManager;

public class MainActivity extends AppCompatActivity {
    private final static String MAIN_FRAGMENT = "fragment_main";

    private MainFragment mainFragment;
    private MaterialDialog loginDialog;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainFragment = new MainFragment();
        showMainFragment();
    }

    private void showMainFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, mainFragment, MAIN_FRAGMENT)
                .commit();
    }

    public MainFragment getMainFragment() {
        return mainFragment;
    }

    @Subscribe
    public void handlePromptLogin(PromptLoginEvent event) {
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
                            Fragment fragment = getSupportFragmentManager()
                                    .findFragmentById(R.id.main_content);
                            if (!fragment.equals(mainFragment)) {
                                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                            mainFragment.showLogin();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
