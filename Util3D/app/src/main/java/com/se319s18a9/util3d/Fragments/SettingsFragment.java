package com.se319s18a9.util3d.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.se319s18a9.util3d.R;
import com.se319s18a9.util3d.backend.User;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    Button changePasswordButton;
    Button changeEmailButton;
    Button changeUsernameButton;
    Button deleteAccountButton;

    private DatabaseReference database;

    public SettingsFragment() {
        // Empty constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        // Set toolbar title

        getActivity().setTitle(R.string.global_fragmentName_settings);

        // Initialize components and bind listeners

        changePasswordButton = v.findViewById(R.id.fragment_settings_button_changePassword);
        changePasswordButton.setOnClickListener(this);

        changeEmailButton = v.findViewById(R.id.fragment_settings_button_changeEmail);
        changeEmailButton.setOnClickListener(this);


        changeUsernameButton = v.findViewById(R.id.fragment_settings_button_changeUsername);
        changeUsernameButton.setOnClickListener(this);

        deleteAccountButton = v.findViewById(R.id.fragment_settings_button_deleteAccount);
        deleteAccountButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(final View v) {
        if(v.getId() == R.id.fragment_settings_button_changePassword) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());

            @SuppressLint("InflateParams")
            final View dialogView = layoutInflater.inflate(R.layout.dialog_changepassword, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
            alertDialogBuilder.setTitle(getString(R.string.s_dialog_changePassword_title));
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder
                    .setPositiveButton(R.string.s_dialog_changePassword_changePasswordButton,
                            (dialog, id) -> {
                                // Leave empty
                            })
                    .setNegativeButton(R.string.s_dialog_changePassword_cancelButton,
                            (dialog, id) -> dialog.cancel());

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                if(((EditText) dialogView.findViewById(R.id.dialog_changePassword_editText_newPassword)).getText().toString().equals(
                        ((EditText) dialogView.findViewById(R.id.dialog_changePassword_editText_repeatNewPassword)).getText().toString())){
                    try{
                        User.getInstance().reauthenticate(((EditText) dialogView.findViewById(R.id.dialog_changePassword_editText_currentPassword)).getText().toString());
                        User.getInstance().changePassword(((EditText) dialogView.findViewById(R.id.dialog_changePassword_editText_newPassword)).getText().toString());
                        Toast.makeText(v1.getContext(), R.string.s_dialog_changePassword_successMessage, Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } catch(Exception e){
                        Toast.makeText(v1.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v1.getContext(), R.string.s_dialog_changePassword_errorMessage_PasswordsNotMatching, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (v.getId() == R.id.fragment_settings_button_changeEmail) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());

            @SuppressLint("InflateParams")
            final View dialogView = layoutInflater.inflate(R.layout.dialog_changeemail, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
            alertDialogBuilder.setTitle(getString(R.string.s_dialog_changeEmail_title));
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder
                    .setPositiveButton(R.string.s_dialog_changeEmail_changeEmailButton,
                            (dialog, id) -> {})
                    .setNegativeButton(R.string.s_dialog_changeEmail_cancelButton,
                            (dialog, id) -> dialog.cancel());

            ((TextView) dialogView.findViewById(R.id.dialog_changeEmail_textView_currentEmail)).setText(User.getInstance().getEmail());

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v12 -> {
                if (((EditText) dialogView.findViewById(R.id.dialog_changeEmail_editText_newEmail)).getText().toString().equals(
                        ((EditText) dialogView.findViewById(R.id.dialog_changeEmail_editText_repeatNewEmail)).getText().toString())) {
                    try {
                        User.getInstance().reauthenticate(((EditText) dialogView.findViewById(R.id.dialog_changeEmail_editText_currentPassword)).getText().toString());
                        User.getInstance().changeEmail(((EditText) dialogView.findViewById(R.id.dialog_changeEmail_editText_repeatNewEmail)).getText().toString());
                        Toast.makeText(v12.getContext(), R.string.s_dialog_changeEmail_successMessage, Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(v12.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v12.getContext(), R.string.s_dialog_changeEmail_errorMessage_EmailsNotMatching, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (v.getId()==R.id.fragment_settings_button_changeUsername) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());

            @SuppressLint("InflateParams")
            final View dialogView = layoutInflater.inflate(R.layout.dialog_changeusername, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
            alertDialogBuilder.setTitle(getString(R.string.s_dialog_changeUsername_title));
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder
                    .setPositiveButton(R.string.s_dialog_changeUsername_changeUsernameButton,
                            (dialog, id) -> { })
                    .setNegativeButton(R.string.s_dialog_changeUsername_cancelButton,
                            (dialog, id) -> dialog.cancel());

            ((TextView) dialogView.findViewById(R.id.dialog_changeUsername_textView_currentUsername)).setText(User.getInstance().getDisplayName());

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v13 -> {
                try {
                    User.getInstance().changeDisplayName(((EditText) dialogView.findViewById(R.id.dialog_changeUsername_editText_newUsername)).getText().toString());
                    Toast.makeText(v13.getContext(), R.string.s_dialog_changeUsername_successMessage, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(v13.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (v.getId()==R.id.fragment_settings_button_deleteAccount){
            LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());

            @SuppressLint("InflateParams")
            final View dialogView = layoutInflater.inflate(R.layout.dialog_deleteaccount, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
            alertDialogBuilder.setTitle(getString(R.string.s_dialog_deleteAccount_title));
            alertDialogBuilder.setView(dialogView);
            alertDialogBuilder
                    .setPositiveButton(R.string.s_dialog_deleteAccount_deleteAccountButton,
                            (dialog, id) -> {})
                    .setNegativeButton(R.string.s_dialog_deleteAccount_cancelButton,
                            (dialog, id) -> dialog.cancel());

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v14 -> {
                try {
                    User.getInstance().reauthenticate(((EditText) dialogView.findViewById(R.id.dialog_deleteAccount_editText_currentPassword)).getText().toString());
                    User.getInstance().deleteAccount();
                    Toast.makeText(v14.getContext(), R.string.s_dialog_deleteAccount_successMessage, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    alertDialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(v14.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}