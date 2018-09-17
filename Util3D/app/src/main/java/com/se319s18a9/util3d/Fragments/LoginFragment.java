package com.se319s18a9.util3d.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.se319s18a9.util3d.backend.User;
import com.se319s18a9.util3d.R;

/**
 * LoginFragment:
 * --------------
 *  LoginFragment handles user-entered credentials and communicates with Firebase authentication.
 *  A user can choose to log into their account, recover a password, and create a new user
 *  account from this view.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    OnSuccessfulLoginListener mOnSuccessfulLoginListener;

    private EditText usernameEditText;
    private EditText passwordEditText;

    Button loginButton;
    Button forgotPasswordButton;
    Button createAccountButton;

    public interface OnSuccessfulLoginListener {
        void onSuccessfulLogin();
    }

    public LoginFragment() {
        // Empty constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnSuccessfulLoginListener = (OnSuccessfulLoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnSuccessfulLoginListener.");
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        // Clear both EditText fields whenever the screen is returned to focus.
        usernameEditText.getText().clear();
        passwordEditText.getText().clear();

        // Check for previous login; if yes, trigger bypass callback method for the
        // LoginActivity/LoginFragment and go to the MainActivity/DashboardFragment.
        if(User.getInstance().isAlreadyLoggedIn()) {
            mOnSuccessfulLoginListener.onSuccessfulLogin();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // Set toolbar title
        getActivity().setTitle(R.string.global_fragmentName_login);

        // Initialize components and bind listeners
        usernameEditText = v.findViewById(R.id.fragment_login_editText_username);
        passwordEditText = v.findViewById(R.id.fragment_login_editText_password);

        loginButton = v.findViewById(R.id.fragment_login_button_login);
        loginButton.setOnClickListener(this);

        forgotPasswordButton = v.findViewById(R.id.fragment_login_button_forgotPassword);
        forgotPasswordButton.setOnClickListener(this);

        createAccountButton = v.findViewById(R.id.fragment_login_button_createAccount);
        createAccountButton.setOnClickListener(this);

        return v;
    }

    /**
     * onClick(View):
     * --------------
     *  Handle actions triggered by user clicks.
     *
     * @param v the clicked View element
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ----- Login Button -----
            case R.id.fragment_login_button_login:
                if (User.getInstance().validateAndLogin(this.getEditTextValue(usernameEditText),
                                                        this.getEditTextValue(passwordEditText))) {
                    mOnSuccessfulLoginListener.onSuccessfulLogin();
                } else {
                    Toast.makeText(this.getContext(),
                                   R.string.s_fragment_login_errorMessage_invalidCredentials,
                                   Toast.LENGTH_SHORT).show();
                }

                break;
            // ----- Forgot Password Button -----
            case R.id.fragment_login_button_forgotPassword:
                LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());

                @SuppressLint("InflateParams")
                final View dialogView = layoutInflater.inflate(R.layout.dialog_forgotpassword, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
                alertDialogBuilder.setTitle(getString(R.string.s_dialog_forgotPassword_title));
                alertDialogBuilder.setView(dialogView);

                // Bind actions to "Cancel" and "Reset Password" dialog buttons.
                alertDialogBuilder
                    .setPositiveButton(R.string.s_dialog_forgotPassword_button_sendEmail,
                        (dialog, id) -> {
                            User.getInstance().sendPasswordResetEmail(
                                    getEditTextValue(dialogView.findViewById(R.id.dialog_forgotPassword_editText_email)));
                            Toast.makeText(v.getContext(),
                                           R.string.s_dialog_forgotPassword_toast_successMessage,
                                           Toast.LENGTH_SHORT).show();
                        })
                    .setNegativeButton(R.string.s_dialog_forgotPassword_button_cancel,
                            (dialog, id) -> dialog.cancel());

                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                break;
            // ----- Create Account Button -----
            case R.id.fragment_login_button_createAccount:
                // Open a new CreateAccountFragment instance.
                Fragment createAccountFragment = new CreateAccountFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_login_frameLayout_root, createAccountFragment);
                fragmentTransaction.addToBackStack(null).commit();
                break;
        }
    }

    /**
     * getEditTextValue(EditText):
     * ---------------------------
     *
     * @param editText the view whose contents is being returned
     * @return the contents of editText (as a String)
     */
    private String getEditTextValue(EditText editText) {
        return editText.getText().toString();
    }
}
