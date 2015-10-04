package sg.rghis.android.fragments;

import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import sg.rghis.android.R;
import sg.rghis.android.models.User;
import sg.rghis.android.utils.SystemUtils;
import sg.rghis.android.views.MainActivity;
import timber.log.Timber;

public class SignupFragment extends BaseFragment implements Validator.ValidationListener {
    public final static int STATE_LOGIN = 0;
    public final static int STATE_SIGNUP = 1;

    @Order(3)
    @NotEmpty
    @Bind(R.id.first_name_edit_text)
    EditText firstNameEditText;

    @Bind(R.id.first_name_text_input_layout)
    TextInputLayout firstNameTextInputLayout;

    @Order(4)
    @NotEmpty
    @Bind(R.id.last_name_edit_text)
    EditText lastNameEditText;

    @Bind(R.id.last_name_text_input_layout)
    TextInputLayout lastNameTextInputLayout;

    @Order(5)
    @Email
    @Bind(R.id.email_edit_text)
    EditText emailEditText;

    @Bind(R.id.username_text_input_layout)
    TextInputLayout usernameTextInputLayout;

    @Order(1)
    @NotEmpty
    @Bind(R.id.username_edit_text)
    EditText usernameEditText;

    @Bind(R.id.email_text_input_layout)
    TextInputLayout emailTextInputLayout;

    @Order(2)
    @Password(min = 6, message = "Enter at least 6 characters")
    @Bind(R.id.password_edit_text)
    EditText passwordEditText;

    @Bind(R.id.password_text_input_layout)
    TextInputLayout passwordTextInputLayout;

    @Bind(R.id.primary_button)
    Button primaryButton;

    @Bind(R.id.link_login)
    TextView descTextView;

    @Bind(R.id.sign_up_fields)
    View signUpFields;

    @Bind(R.id.logo)
    ImageView logo;

    private Validator validator;
    private int currentState = STATE_LOGIN;

    private String signUpButtonString;
    private String loginButtonString;
    private String signUpDescString;
    private String loginDescString;

    private ProgressDialog progressDialog;

    public SignupFragment() {
    }

    public static SignupFragment newInstance() {

        Bundle args = new Bundle();

        SignupFragment fragment = new SignupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void switchState() {
        currentState = (currentState + 1) % 2;
        ensureCurrentState();
    }

    private void ensureCurrentState() {
        switch (currentState) {
            case STATE_LOGIN:
                ensureLoginState();
                break;
            case STATE_SIGNUP:
                ensureSignupState();
                break;
        }
    }

    private void ensureLoginState() {
        signUpFields.setVisibility(View.GONE);
        logo.setVisibility(View.VISIBLE);
        primaryButton.setText(loginButtonString);
        descTextView.setText(signUpDescString);
        setToolbarTitle("Login");
    }

    private void ensureSignupState() {
        signUpFields.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
        primaryButton.setText(signUpButtonString);
        descTextView.setText(loginDescString);
        setToolbarTitle("Sign up");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();

        View view = inflater.inflate(R.layout.sign_up_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= 16) {
            ViewGroup layout = (ViewGroup) view.findViewById(R.id.linear_layout);
            LayoutTransition layoutTransition = layout.getLayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        }

        validator = new Validator(this);
        validator.setValidationListener(this);

        firstNameEditText.setTag(firstNameTextInputLayout);
        lastNameEditText.setTag(lastNameTextInputLayout);
        emailEditText.setTag(emailTextInputLayout);
        usernameEditText.setTag(usernameTextInputLayout);
        passwordEditText.setTag(passwordTextInputLayout);

        loginButtonString = getResources().getString(R.string.login);
        signUpButtonString = getResources().getString(R.string.signup);
        loginDescString = getResources().getString(R.string.login_desc);
        signUpDescString = getResources().getString(R.string.signup_desc);

        ensureCurrentState();
    }

    @OnClick(R.id.link_login)
    public void onLinkClicked() {
        switchState();
    }

    @OnLongClick(R.id.primary_button)
    public boolean populateWithMockData() {
        firstNameEditText.setText("Jun Yi");
        lastNameEditText.setText("Hee");
        usernameEditText.setText("junyihjy");
        emailEditText.setText("junyi.hjy@gmail.com");
        passwordEditText.setText("heejunyi");

        return true;
    }

    @OnClick(R.id.primary_button)
    public void clientValidate() {
        primaryButton.setEnabled(false);

        firstNameTextInputLayout.setErrorEnabled(false);
        lastNameTextInputLayout.setErrorEnabled(false);
        usernameTextInputLayout.setErrorEnabled(false);
        emailTextInputLayout.setErrorEnabled(false);
        passwordTextInputLayout.setErrorEnabled(false);

        firstNameTextInputLayout.setError(null);
        lastNameTextInputLayout.setError(null);
        usernameTextInputLayout.setError(null);
        emailTextInputLayout.setError(null);
        passwordTextInputLayout.setError(null);

        SystemUtils.hideKeyboard(getActivity());
        if (currentState == STATE_LOGIN) {
            validator.validateTill(passwordEditText);
        } else {
            validator.validate();
        }
    }

    @Override
    public void onValidationSucceeded() {
        Timber.d("Validation succeeded");
        showProgress();
        serverValidate();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        primaryButton.setEnabled(true);

        int l = errors.size();

        for (int i = 0; i < l; i++) {
            ValidationError error = errors.get(i);
            TextInputLayout textInputLayout = (TextInputLayout) error.getView().getTag();
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(error.getCollatedErrorMessage(getActivity()));
        }
    }

    private void serverValidate() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();


        if (currentState == STATE_LOGIN) {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        loginSuccess(User.wrap(parseUser));
                    } else {
                        handleError(e);
                    }
                }
            });
        } else {
            final User user = User.newConsumer();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        signupSuccess();
                    } else {
                        handleError(e);
                    }
                }
            });
        }
    }

    private void loginSuccess(User user) {
        hideProgress();
        String name = user.getFirstName() + " " + user.getLastName();
        Snackbar.make(getView(), String.format("Welcome %s", name),
                Snackbar.LENGTH_SHORT).show();
        primaryButton.setEnabled(true);
        navigateToState(MainFragment.STATE_NEWS, null, false);
        MainActivity activity = SystemUtils.getMainActivityFromContext(getContext());
        activity.getMainFragment().notifyLoginSuccess();
    }

    private void signupSuccess() {
        hideProgress();
        Snackbar.make(getView(), "Account successfully created. Please sign in.",
                Snackbar.LENGTH_SHORT).show();
        primaryButton.setEnabled(true);
        switchState();
    }

    private void handleError(final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                primaryButton.setEnabled(true);
            }
        });

        if (e instanceof ParseException) {
            final ParseException error = (ParseException) e;
            int errorCode = error.getCode();

            Timber.e(e.getMessage());

            switch (errorCode) {
                case ParseException.USERNAME_TAKEN:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            emailTextInputLayout.setErrorEnabled(true);
                            emailTextInputLayout.setError("This e-mail has already been registered");
                        }
                    });
                    break;
                default:

            }
        } else {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(currentState == STATE_LOGIN ? "Authenticating..."
                : "Creating Account...");
        progressDialog.show();
    }


    private void hideProgress() {
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


}