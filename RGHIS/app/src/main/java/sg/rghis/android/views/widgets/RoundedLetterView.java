package sg.rghis.android.views.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sg.rghis.android.R;

public class RoundedLetterView extends RelativeLayout {
    private int backgroundColor;
    private final int fallbackColor;
    private int initialBackgroundColor;
    private String initialInitials;
    private TextView initialsView;

    public RoundedLetterView(final Context context) {
        this(context, null);
    }

    public RoundedLetterView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }

    public RoundedLetterView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        LayoutInflater.from(context).inflate(R.layout.rounded_letter_view, (ViewGroup) this);
        setClipChildren(false);
        setClipToPadding(false);
        this.fallbackColor = ContextCompat.getColor(context, R.color.primary_dark);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.RoundedLetterView);
        this.initialsView = (TextView) this.findViewById(R.id.initials);
        int initialBackgroundColor;
        if (obtainStyledAttributes == null) {
            initialBackgroundColor = this.fallbackColor;
        } else {
            initialBackgroundColor = obtainStyledAttributes.getColor(R.styleable.RoundedLetterView_backgroundColor, this.fallbackColor);
        }
        this.initialBackgroundColor = initialBackgroundColor;
        String string;
        if (obtainStyledAttributes == null) {
            string = null;
        } else {
            string = obtainStyledAttributes.getString(R.styleable.RoundedLetterView_initials);
        }
        this.initialInitials = string;
        obtainStyledAttributes.recycle();
        this.setBackgroundColor(this.initialBackgroundColor);
        this.setInitials(this.initialInitials);
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(final int backgroundColor) {
        if (this.backgroundColor != backgroundColor) {
            this.backgroundColor = backgroundColor;
            this.updateInitialsBackground();
        }
    }

    public void setInitials(final CharSequence text) {
        this.initialsView.setText(text);
    }

    protected void updateInitialsBackground() {
        this.initialsView.setBackgroundResource(R.drawable.solid_circle_bg);
        ((GradientDrawable) ((LayerDrawable) this.initialsView.getBackground()).findDrawableByLayerId(R.id.solid_circle)).setColor(this.backgroundColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }
}