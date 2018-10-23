package com.kid.william.finddoctor.Presenter;

import android.content.Context;

import com.kid.william.finddoctor.Interactor.ILoginInteractor;
import com.kid.william.finddoctor.Interactor.LoginInteractor;
import com.kid.william.finddoctor.View.ILoginView;

public class LoginPresenter implements ILoginPresenter,
        LoginInteractor.onLoginFinishedListener,
        LoginInteractor.onRegisterFinishedListener{

    private ILoginView iLoginView;
    private ILoginInteractor iLoginInteractor;

    public LoginPresenter(ILoginView iLoginView) {
        this.iLoginView = iLoginView;
        iLoginInteractor = new LoginInteractor();
    }

    @Override
    public void validateLogin(String email, String password,Context context) {
        if(iLoginView!=null){
            iLoginView.showProgress();
            iLoginInteractor.onLogin(email,password,this,context);
        }
    }

    @Override
    public void validateRegister(String email, String password, String name, String lastname, String phone, Context context) {
        if(iLoginView!=null){
            iLoginInteractor.onRegister(email,password,name,lastname,phone,this,context);
        }
    }

    @Override
    public void onDestroy() {
        if(iLoginView!=null){
            iLoginView = null;
        }
    }

    @Override
    public void onEmailLoginError() {
        if(iLoginView!=null){
            iLoginView.hideProgress();
            iLoginView.setEmailLoginError();
        }
    }

    @Override
    public void onPasswordLoginError() {
        if(iLoginView!=null){
            iLoginView.hideProgress();
            iLoginView.setPasswordLoginError();
        }
    }

    @Override
    public void onSuccessLogin() {
        if(iLoginView!=null){
            iLoginView.hideProgress();
            iLoginView.navigatetoMain();
        }
    }

    @Override
    public void onFailureLogin(String message) {
        if(iLoginView!=null){
            iLoginView.hideProgress();
            iLoginView.showAlertLogin(message);
        }
    }

    @Override
    public void onFailureRegister(String message) {
        if(iLoginView!=null){
            iLoginView.showAlertRegister(message);
        }
    }

    @Override
    public void onSuccessRegister(String message) {
        if(iLoginView!=null){
            iLoginView.showAlertRegister(message);
        }
    }
}
