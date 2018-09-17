package com.se319s18a9.util3d.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.se319s18a9.util3d.database.UserInfo;
import com.se319s18a9.util3d.backend.User;
import com.se319s18a9.util3d.R;

public class CreateAccountFragment extends Fragment implements View.OnClickListener {

    private EditText emailEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private EditText fullName;
    private EditText companyName;
    private EditText phoneNumber;
    private EditText occupation;

    Button createButton;
    Button cancelButton;

    private DatabaseReference databaseReference;

    public CreateAccountFragment() {
        // Empty constructor
    }

    public void saveUser() {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        String name = fullName.getText().toString().trim();
        String company = companyName.getText().toString().trim();
        String occ = occupation.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        UserInfo userInfo = new UserInfo(company, name, occ, phone, username);

        databaseReference.child(User.getInstance().getUserID()).child("User").setValue(userInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View v = inflater.inflate(R.layout.fragment_createaccount, container, false);

        // Set toolbar title

        getActivity().setTitle("Create Account");

        // Initialize EditTexts and Buttons

        fullName = v.findViewById(R.id.fragment_createAccount_editText_fullName);
        companyName = v.findViewById(R.id.fragment_createAccount_editText_companyName);
        occupation = v.findViewById(R.id.fragment_createAccount_editText_occupation);
        phoneNumber = v.findViewById(R.id.fragment_createAccount_editText_phoneNumber);
        emailEditText = v.findViewById(R.id.fragment_createAccount_editText_email);
        usernameEditText = v.findViewById(R.id.fragment_createAccount_editText_username);
        passwordEditText = v.findViewById(R.id.fragment_createAccount_editText_password);
        repeatPasswordEditText = v.findViewById(R.id.fragment_createAccount_editText_repeatPassword);

        createButton = v.findViewById(R.id.fragment_createAccount_button_create);
        createButton.setOnClickListener(this);

        cancelButton = v.findViewById(R.id.fragment_createAccount_button_cancel);
        cancelButton.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_createAccount_button_create:
                if(getEditTextValue(passwordEditText).equals(getEditTextValue(repeatPasswordEditText))) {
                    try {
                        User.getInstance().createAccount(getEditTextValue(emailEditText), getEditTextValue(passwordEditText));
                        saveUser();

                        if(!getEditTextValue(usernameEditText).isEmpty()) {
                            User.getInstance().changeDisplayName(getEditTextValue(usernameEditText));
                        }

                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    } catch(Exception e) {
                        Toast.makeText(this.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(v.getContext(), R.string.s_fragment_createAccount_errorMessage_PasswordsNotMatching, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.fragment_createAccount_button_cancel:
                // Return to LoginFragment
                getActivity().getSupportFragmentManager().popBackStackImmediate();
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