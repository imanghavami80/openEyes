package com.example.openeyes.utility;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.openeyes.model.User;

public class MySharedPreferences {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public MySharedPreferences(Context context) {
        pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    public void setUserInfo(User user) {
        // Decode the email.
        user.setEmail(user.getEmail().replace(",", ".").replace("-", "@"));

        editor.putString(Constants.PREF_KEY_USER_EMAIL, user.getEmail());
        editor.putString(Constants.PREF_KEY_USER_FULL_NAME, user.getFullName());
        editor.putString(Constants.PREF_KEY_USER_NICK_NAME, user.getNickName());
        editor.commit();
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(Constants.PREF_KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public String getUserEmail() {
        return pref.getString(Constants.PREF_KEY_USER_EMAIL, "");
    }

    public String getEncodedUserEmail() {
        return pref.getString(Constants.PREF_KEY_USER_EMAIL, "").replace(".", ",").replace("@", "-");
    }

    public String getUserFullName() {
        return pref.getString(Constants.PREF_KEY_USER_FULL_NAME, "");
    }

    public String getUserNickName() {
        return pref.getString(Constants.PREF_KEY_USER_NICK_NAME, "");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constants.PREF_KEY_IS_LOGGED_IN, false);
    }

    public void clearAllData() {
        editor.clear();
        editor.commit();
    }
}
