package sg.rghis.android.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import sg.rghis.android.R;
import sg.rghis.android.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainFragment = new MainFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, mainFragment)
                .commit();

    }

    public MainFragment getMainFragment() {
        return mainFragment;
    }

}
