package com.mojoteahouse.mojotea.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mojoteahouse.mojotea.R;
import com.mojoteahouse.mojotea.fragment.dialog.ProgressDialogFragment;
import com.mojoteahouse.mojotea.util.KeyboardUtil;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private View confirmPasswordContainer;
    private Button signInButton;
    private Button facebookSignInButton;
    private TextView forgetPasswordTextView;
    private TextView signUpTextView;

    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuthStateListener firebaseAuthStateListener;
    private CallbackManager callbackManager;
    private Pattern emailPattern;
    private boolean isSignInState = true;

    public static void start(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuthStateListener();
        callbackManager = CallbackManager.Factory.create();
        emailPattern = Pattern.compile(getString(R.string.email_regex));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookLoginCallback());

        emailEditText = (EditText) findViewById(R.id.login_email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        confirmPasswordEditText = (EditText) findViewById(R.id.login_confirm_password_edit_text);
        confirmPasswordContainer = findViewById(R.id.login_confirm_password_container);
        signInButton = (Button) findViewById(R.id.login_sign_in_button);
        facebookSignInButton = (Button) findViewById(R.id.login_facebook_sign_in_button);
        forgetPasswordTextView = (TextView) findViewById(R.id.login_forget_password_button);
        signUpTextView = (TextView) findViewById(R.id.login_sign_up_button);
        final ImageButton emailClearButton = (ImageButton) findViewById(R.id.login_email_clear_button);
        final ImageButton passwordClearButton = (ImageButton) findViewById(R.id.login_password_clear_button);
        final ImageButton confirmPasswordClearButton = (ImageButton) findViewById(R.id.login_confirm_password_clear_button);

        signInButton.setOnClickListener(this);
        facebookSignInButton.setOnClickListener(this);
        forgetPasswordTextView.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);
        emailClearButton.setOnClickListener(this);
        passwordClearButton.setOnClickListener(this);
        confirmPasswordClearButton.setOnClickListener(this);

        emailEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailClearButton.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }
        });
        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    passwordEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordClearButton.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }
        });
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    if (isSignInState) {
                        signInOrSignUp();
                    } else {
                        confirmPasswordEditText.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                confirmPasswordClearButton.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }
        });
        confirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    signInOrSignUp();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthStateListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthStateListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_sign_in_button:
                signInOrSignUp();
                break;

            case R.id.login_facebook_sign_in_button:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
                break;

            case R.id.login_forget_password_button:
                String emailAddress = emailEditText.getText().toString();
                Matcher emailMatcher = emailPattern.matcher(emailAddress);
                if (!emailMatcher.matches()) {
                    Toast.makeText(this, R.string.login_email_format_incorrect_message, Toast.LENGTH_LONG).show();
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(emailAddress);
                    Toast.makeText(this, R.string.login_reset_password_email_sent_message, Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.login_sign_up_button:
                isSignInState = !isSignInState;
                if (isSignInState) {
                    signInButton.setText(R.string.login_sign_in_text);
                    signUpTextView.setText(R.string.login_sign_up_button_text);
                    confirmPasswordContainer.setVisibility(View.GONE);
                    confirmPasswordEditText.setText("");
                    forgetPasswordTextView.setVisibility(View.VISIBLE);
                    facebookSignInButton.setVisibility(View.VISIBLE);
                } else {
                    signInButton.setText(R.string.login_sign_up_text);
                    signUpTextView.setText(R.string.login_sign_in_button_text);
                    forgetPasswordTextView.setVisibility(View.GONE);
                    facebookSignInButton.setVisibility(View.GONE);
                    confirmPasswordContainer.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.login_email_clear_button:
                emailEditText.setText("");
                break;

            case R.id.login_password_clear_button:
                passwordEditText.setText("");
                break;

            case R.id.login_confirm_password_clear_button:
                confirmPasswordEditText.setText("");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void signInOrSignUp() {
        String emailAddress = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        Matcher emailMatcher = emailPattern.matcher(emailAddress);
        if (!emailMatcher.matches()) {
            Toast.makeText(this, R.string.login_email_format_incorrect_message, Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, R.string.login_password_length_invalid_message, Toast.LENGTH_LONG).show();
        } else if (!isSignInState && !confirmPassword.equalsIgnoreCase(password)) {
            Toast.makeText(this, R.string.login_confirm_password_incorrect_message, Toast.LENGTH_LONG).show();
        } else {
            KeyboardUtil.hideKeyboard(this);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (isSignInState) {
                ProgressDialogFragment.showWithMessage(getFragmentManager(), getString(R.string.login_signing_in_message));
                firebaseAuth.signInWithEmailAndPassword(emailAddress, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    ProgressDialogFragment.dismiss(getFragmentManager());
                                    String errorMessage = task.getException().getMessage();
                                    Log.e("findme", "Sign in failed: " + errorMessage);
                                    Toast.makeText(SignInActivity.this,
                                            String.format(getString(R.string.login_sign_in_failed_message), errorMessage),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                ProgressDialogFragment.showWithMessage(getFragmentManager(), getString(R.string.login_signing_up_message));
                firebaseAuth.createUserWithEmailAndPassword(emailAddress, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    ProgressDialogFragment.dismiss(getFragmentManager());
                                    String errorMessage = task.getException().getMessage();
                                    Log.e("findme", "Sign up failed: " + errorMessage);
                                    Toast.makeText(SignInActivity.this,
                                            String.format(getString(R.string.login_sign_up_failed_message), errorMessage),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }
    }


    private class FirebaseAuthStateListener implements FirebaseAuth.AuthStateListener {

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                sharedPreferences.edit()
                        .putBoolean(getString(R.string.pref_signed_in), true)
                        .putString(getString(R.string.pref_user_id), user.getUid())
                        .putString(getString(R.string.pref_user_email), user.getEmail())
                        .apply();
                ProgressDialogFragment.dismiss(getFragmentManager());
                MojoMenuActivity.start(SignInActivity.this);
                supportFinishAfterTransition();
            }
        }
    }

    private class FacebookLoginCallback implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("findme", "Facebook login successful!");
            signInWithFacebookToken(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {
            // No-op
        }

        @Override
        public void onError(FacebookException error) {
            String errorMessage = error.getMessage();
            Log.e("findme", "Sign in failed: " + errorMessage);
            Toast.makeText(SignInActivity.this,
                    String.format(getString(R.string.login_sign_in_failed_message), errorMessage),
                    Toast.LENGTH_LONG).show();
        }

        private void signInWithFacebookToken(AccessToken token) {
            Log.e("findme", "facebook token:" + token);
            ProgressDialogFragment.showWithMessage(getFragmentManager(), getString(R.string.login_signing_in_message));
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                ProgressDialogFragment.dismiss(getFragmentManager());
                                String errorMessage = task.getException().getMessage();
                                Log.e("findme", "Sign up failed: " + errorMessage);
                                Toast.makeText(SignInActivity.this,
                                        String.format(getString(R.string.login_sign_in_failed_message), errorMessage),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
