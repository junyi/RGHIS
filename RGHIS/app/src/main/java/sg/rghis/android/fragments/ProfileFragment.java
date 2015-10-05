package sg.rghis.android.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.parse.DeleteCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sg.rghis.android.R;
import sg.rghis.android.models.User;
import sg.rghis.android.utils.SystemUtils;
import sg.rghis.android.utils.UserManager;
import sg.rghis.android.views.MainActivity;
import timber.log.Timber;

public class ProfileFragment extends BaseFragment implements Validator.ValidationListener {

    @Bind(R.id.name_view)
    TextView nameView;
    @Bind(R.id.username)
    TextView usernameView;
    @Bind(R.id.first_name)
    TextView firstNameView;
    @Bind(R.id.last_name)
    TextView lastNameView;
    @Bind(R.id.email)
    TextView emailView;
    @Bind(R.id.password)
    TextView passwordView;
    @Bind(R.id.professional_label)
    TextView professionalLabel;

    private MaterialDialog dialogInstance;
    private ProgressDialog progressDialog;
    private Validator validator;
    private final PwDialogViewHolder dialogViewHolder = new PwDialogViewHolder();

    static class PwDialogViewHolder {
        @NotEmpty
        @Nullable
        @Bind(R.id.old_pw_edit_text)
        EditText oldPwEditText;

        @NotEmpty
        @Nullable
        @Bind(R.id.new_pw_edit_text)
        EditText newPwEditText;

        @Nullable
        @Bind(R.id.old_pw_text_input_layout)
        TextInputLayout oldPwTextInputLayout;

        @Nullable
        @Bind(R.id.new_pw_text_input_layout)
        TextInputLayout newPwTextInputLayout;
    }

    @Override
    public int getTitleRes() {
        return R.string.profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParseUser user = UserManager.getCurrentUser();
        nameView.setText(user.getString(User.KEY_FIRST_NAME) + " " + user.getString(User.KEY_LAST_NAME));
        firstNameView.setText(user.getString(User.KEY_FIRST_NAME));
        lastNameView.setText(user.getString(User.KEY_LAST_NAME));
        emailView.setText(user.getEmail());
        usernameView.setText(user.getUsername());
        if (UserManager.isUserProfessional()) {
            professionalLabel.setVisibility(View.VISIBLE);
        } else {
            professionalLabel.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.edit_first_name)
    public void editFirstName() {
        showEditDialog(firstNameView, User.KEY_FIRST_NAME, "first name");
    }

    @OnClick(R.id.edit_last_name)
    public void editLastName() {
        showEditDialog(lastNameView, User.KEY_LAST_NAME, "last name");
    }

    @OnClick(R.id.edit_email)
    public void editEmail() {
        showEditDialog(firstNameView, User.KEY_EMAIL, "email");
    }

    @OnClick(R.id.edit_password)
    public void editPassword() {
        showChangePasswordDialog();
    }

    @OnClick(R.id.sign_out_button)
    public void signOut() {
        ParseUser.logOut();
        ParseInstallation.getCurrentInstallation().deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Timber.d(Log.getStackTraceString(e));
            }
        });
        Toast.makeText(getContext(), "Sign out success!", Toast.LENGTH_SHORT).show();
        navigateToState(MainFragment.STATE_NEWS, null, false);
    }

    private String capitalize(String s) {
        if (TextUtils.isEmpty(s))
            return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void updateNameView() {
        ParseUser user = UserManager.getCurrentUser();
        nameView.setText(user.getString(User.KEY_FIRST_NAME) + " " + user.getString(User.KEY_LAST_NAME));
    }

    private void showEditDialog(final TextView view, final String field, String titlePostFix) {
        if (dialogInstance != null) {
            dialogInstance.cancel();
        }
        dialogInstance = new MaterialDialog.Builder(getContext())
                .title("Edit " + titlePostFix)
                .input(capitalize(titlePostFix), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        materialDialog.getActionButton(DialogAction.POSITIVE)
                                .setEnabled(!TextUtils.isEmpty(charSequence));
                    }
                })
                .positiveText("Save")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        ParseUser currentUser = UserManager.getCurrentUser();
                        final String value = dialog.getInputEditText().getText().toString();
                        if (field.equals(User.KEY_EMAIL)) {
                            currentUser.setEmail(value);
                        } else {
                            currentUser.put(field, value);
                        }
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    MainActivity activity = SystemUtils.getMainActivityFromContext(getContext());
                                    if (activity != null)
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                hideProgress();
                                                view.setText(value);
                                                updateNameView();
                                            }
                                        });

                                } else {
                                    Timber.e(Log.getStackTraceString(e));
                                }
                            }
                        });
                        dialogInstance.dismiss();
                        showProgress();
                    }
                })
                .build();

        dialogInstance.show();
    }

    private void showChangePasswordDialog() {
        if (dialogInstance != null) {
            dialogInstance.cancel();
        }
        dialogInstance = new MaterialDialog.Builder(getContext())
                .title("Change password ")
                .customView(R.layout.dialog_change_password, false)
                .positiveText("Save")
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        final ParseUser currentUser = UserManager.getCurrentUser();
                        String oldPw = dialogViewHolder.oldPwEditText.getText().toString();
                        final String newPw = dialogViewHolder.newPwEditText.getText().toString();
                        ParseUser.logInInBackground(currentUser.getUsername(), oldPw, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (parseUser != null) {
                                    currentUser.setPassword(newPw);
                                    currentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            hideProgress();
                                            if (e != null)
                                                runOnUi(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showToast("Password changed successfully.");
                                                    }
                                                });
                                        }
                                    });
                                } else {
                                    runOnUi(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideProgress();
                                            showToast("Incorrect password entered.");
                                        }
                                    });
                                }
                            }
                        });


                        dialogInstance.dismiss();
                        showProgress();
                    }
                })
                .build();
        bindDialogViews(dialogInstance, dialogViewHolder);

        dialogInstance.show();
    }

    private void runOnUi(Runnable r) {
        final MainActivity activity = SystemUtils.getMainActivityFromContext(getContext());
        if (activity != null)
            activity.runOnUiThread(r);
    }

    private void showToast(final String message) {
        final MainActivity activity = SystemUtils.getMainActivityFromContext(getContext());
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void bindDialogViews(MaterialDialog dialog, final PwDialogViewHolder dialogViewHolder) {
        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);

        ButterKnife.bind(dialogViewHolder, dialog);

        validator = new Validator(dialogViewHolder);
        validator.setValidationListener(this);

        if (dialogViewHolder.oldPwEditText == null || dialogViewHolder.newPwEditText == null) {
            return;
        }

        dialogViewHolder.oldPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                dialogViewHolder.oldPwTextInputLayout.setErrorEnabled(false);
                dialogViewHolder.newPwTextInputLayout.setErrorEnabled(false);
                validator.validate();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialogViewHolder.newPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialogViewHolder.oldPwTextInputLayout.setErrorEnabled(false);
                dialogViewHolder.newPwTextInputLayout.setErrorEnabled(false);
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

    private void showProgress() {
        progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Saving...");
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
