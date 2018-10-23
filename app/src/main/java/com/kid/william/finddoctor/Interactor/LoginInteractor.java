package com.kid.william.finddoctor.Interactor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kid.william.finddoctor.Model.entidad.UserInfo;
import com.kid.william.finddoctor.R;
import com.kid.william.finddoctor.utilidades.Common;
import com.kid.william.finddoctor.utilidades.Preferences;


public class LoginInteractor implements ILoginInteractor {

    private FirebaseAuth firebaseAuth;
    private Context contexto;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference users;

    @Override
    public void onLogin(String email, String password, onLoginFinishedListener listener,Context context) {

        firebaseAuth = FirebaseAuth.getInstance();
        contexto = context;

        if(validationLogin(email,password,listener))
        loginFirebase(email,password,listener);

    }

    private boolean validationLogin(String email,String password,onLoginFinishedListener listener) {
        if (TextUtils.isEmpty(email) && !isEmailValid(email)){
            listener.onEmailLoginError();
            return false;
        } else if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            listener.onPasswordLoginError();
            return false;
        }
        return true;
    }

    @Override
    public void onRegister(String email, String password, String name, String lastname, String phone, onRegisterFinishedListener listener, Context context) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        users = firebaseDatabase.getReference(Common.TABLE_USER_INFO);
        contexto = context;
        if(validationRegister(email,password,name,lastname,phone,listener))
            registerFirebase(email,password,name,lastname,phone,listener);
    }

    private boolean validationRegister(String email, String password, String name, String lastname, String phone, onRegisterFinishedListener listener) {
        if (TextUtils.isEmpty(email) && !isEmailValid(email)){
            return false;
        } else if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            return false;
        } else if (TextUtils.isEmpty(name)) {
            return false;
        } else if (TextUtils.isEmpty(lastname)) {
            return false;
        } else if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void loginFirebase(String correo,String password, final onLoginFinishedListener listener){

        firebaseAuth.signInWithEmailAndPassword(correo,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String valor = task.getResult().getUser().getUid();
                            Preferences.SavedPreferences(contexto,valor);
                            listener.onSuccessLogin();
                        }else{
                            listener.onFailureLogin("Invalidate Credentials");
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailureLogin(e.getMessage());
            }
        });
    }

    private void registerFirebase(final String email, final String password, final String name, final String lastname, final String phone, final onRegisterFinishedListener listener){

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        UserInfo user = new UserInfo(
                                email,
                                password,
                                name,
                                lastname,
                                Integer.parseInt(phone));

                        users.child(authResult.getUser().getUid())
                                .setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        listener.onSuccessRegister(contexto.getString(R.string.register_succesful));

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        listener.onFailureRegister(contexto.getString(R.string.register_failed)+ e.getMessage());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailureRegister(contexto.getString(R.string.register_failed)+ e.getMessage());
                    }
                });
    }
}
