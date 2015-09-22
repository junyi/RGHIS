package sg.rghis.android.views.widgets;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import timber.log.Timber;

public class RGViewPager extends ViewPager {
    ViewConfiguration vc = ViewConfiguration.get(getContext());
    private int mSlop = vc.getScaledTouchSlop();
    private boolean mIsScrolling;
    private float mDownX;

    public RGViewPager(Context context) {
        super(context);
        setupTouchListener();
    }

    public RGViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupTouchListener();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Timber.d(ev.toString());
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */


        final int action = MotionEventCompat.getActionMasked(ev);

        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            mIsScrolling = false;
            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = mDownX - ev.getRawX();
                if (Math.abs(deltaX) > mSlop) {
                    mIsScrolling = true;
                    if (deltaX < 0) {
                        return true;
                    }
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void setupTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsScrolling && v instanceof ViewGroup && getCurrentItem() > 0) {
                    ((ViewGroup) v).requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
    }
}
