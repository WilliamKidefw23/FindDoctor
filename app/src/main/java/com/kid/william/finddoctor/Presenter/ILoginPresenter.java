package com.kid.william.finddoctor.Presenter;

import android.content.Context;

public interface ILoginPresenter {

    void validateLogin(String email, String passworrd, Context context);
    void validateRegister(String email,String password,String name,String lastname,String phone,Context context);
    void onDestroy();
}
