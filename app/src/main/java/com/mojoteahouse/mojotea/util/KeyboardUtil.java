package com.mojoteahouse.mojotea.util;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtil {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((activity.getCurrentFocus() == null)
                ? null
                : activity.getCurrentFocus().getWindowToken(), 0);
    }
}
