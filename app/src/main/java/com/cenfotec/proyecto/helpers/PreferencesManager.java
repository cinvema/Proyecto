package com.cenfotec.proyecto.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.cenfotec.proyecto.logic.Variables;

import static android.text.style.TtsSpan.ARG_USERNAME;

public class PreferencesManager {

    private static SharedPreferences mPreferences;
    private static final String PREF_FILE = "app.preferences";

    private static final String ARG_USERNAME = "arg.username";
    private static final String ARG_PASSWORD = "arg.password";
    private static final String ARG_REMEMBER = "arg.remember";

    public static void savePreferences(Context ctx, String username,
                                       String password, boolean remember){
        if(mPreferences == null){
            mPreferences = ctx.getSharedPreferences(PREF_FILE, ctx.MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putString(ARG_USERNAME, username);
        editor.putString(ARG_PASSWORD, password);
        editor.putBoolean(ARG_REMEMBER, remember);

        editor.commit();
    }

    public static String getUsernameFromPreferences(Context ctx){
        if(mPreferences == null){
            mPreferences = ctx.getSharedPreferences(PREF_FILE, ctx.MODE_PRIVATE);
        }

        return mPreferences.getString(ARG_USERNAME, "");
    }

    public static String getPasswordFromPreferences(Context ctx){
        if(mPreferences == null){
            mPreferences = ctx.getSharedPreferences(PREF_FILE, ctx.MODE_PRIVATE);
        }

        return mPreferences.getString(ARG_PASSWORD, "");
    }

    public static boolean getRememberFromPreferences(Context ctx){
        if(mPreferences == null){
            mPreferences = ctx.getSharedPreferences(PREF_FILE, ctx.MODE_PRIVATE);
        }

        return mPreferences.getBoolean(ARG_REMEMBER, false);
    }



}
