package com.kid.william.finddoctor.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.kid.william.finddoctor.Presenter.ILoginPresenter;
import com.kid.william.finddoctor.Presenter.LoginPresenter;
import com.kid.william.finddoctor.R;
import com.kid.william.finddoctor.View.ILoginView;



public class LoginActivity extends AppCompatActivity implements ILoginView {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private AutoCompleteTextView txtEmaillogin;
    private EditText edtPasswordlogin;
    private Button btnLogin,btnRegister;
    private View mProgressView;
    private View mLoginFormView;
    private ILoginPresenter loginPresenter;
    private EditText edtEmailregister,edtPasswordregister,edtNameregister,edtLastnameregister,edtPhoneregister;
    private RelativeLayout layout_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginFormView = findViewById(R.id.scrollv_login);
        mProgressView = findViewById(R.id.pgbar_login);
        btnLogin = (Button) findViewById(R.id.btnSignIn_login);
        btnRegister = (Button) findViewById(R.id.btnRegister_login);
        loginPresenter = new LoginPresenter(this);
        layout_login = findViewById(R.id.layout_login);

        /*edtPasswordlogin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });*/

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void showProgress() {
        showProgress(true);
    }

    @Override
    public void hideProgress() {
        showProgress(false);
    }

    @Override
    public void setEmailLoginError() {
        txtEmaillogin.setError(getString(R.string.error_invalid_email));
        txtEmaillogin.requestFocus();
    }

    @Override
    public void setPasswordLoginError() {
        edtPasswordlogin.setError(getString(R.string.error_invalid_password));
        edtPasswordlogin.requestFocus();
    }

    @Override
    public void navigatetoMain() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void showAlertLogin(String message) {
        Snackbar.make(layout_login,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAlertRegister(String message) {
        Snackbar.make(layout_login,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.onDestroy();
    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.login_title));
        dialog.setMessage(getString(R.string.login_message));
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View login_layout = layoutInflater.inflate(R.layout.user_login,null);

        txtEmaillogin = login_layout.findViewById(R.id.txtEmail_login);
        edtPasswordlogin = login_layout.findViewById(R.id.txtPaswword_login);
        dialog.setView(login_layout);

        dialog.setPositiveButton(getString(R.string.login_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                loginPresenter.validateLogin(txtEmaillogin.getText().toString(),edtPasswordlogin.getText().toString(),LoginActivity.this);
            }
        });
        dialog.setNegativeButton(getString(R.string.login_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.register_title));
        dialog.setMessage(getString(R.string.register_message));
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View register_layout = layoutInflater.inflate(R.layout.user_register,null);

        edtEmailregister = register_layout.findViewById(R.id.user_email);
        edtPasswordregister = register_layout.findViewById(R.id.user_password);
        edtNameregister = register_layout.findViewById(R.id.user_name);
        edtLastnameregister = register_layout.findViewById(R.id.user_lastname);
        edtPhoneregister = register_layout.findViewById(R.id.user_phone);

        dialog.setView(register_layout);
        dialog.setPositiveButton(getString(R.string.register_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                loginPresenter.validateRegister(
                        edtEmailregister.getText().toString(),
                        edtPasswordregister.getText().toString(),
                        edtNameregister.getText().toString(),
                        edtLastnameregister.getText().toString(),
                        edtPhoneregister.getText().toString(),
                        LoginActivity.this);

            }
        }).setNegativeButton(getString(R.string.register_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }
}

