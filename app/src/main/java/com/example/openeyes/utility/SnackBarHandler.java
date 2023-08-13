package com.example.openeyes.utility;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import com.example.openeyes.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackBarHandler {

    public static void snackBarHideAction(Context cnx, View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);

        snackbar.setAction(cnx.getString(R.string.hide), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();

                    }
                })
                .setActionTextColor(cnx.getColor(R.color.blue_light))
                .setTextColor(cnx.getColor(R.color.white_light))
                .setBackgroundTint(cnx.getColor(R.color.blue_dark))
                .show();

    }

    public static void snackBarEnableGpsAction(Context cnx, View rootView, String message) {
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);

        snackbar.setAction(cnx.getString(R.string.enable), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        cnx.startActivity(intent);

                    }
                })
                .setActionTextColor(cnx.getColor(R.color.blue_light))
                .setTextColor(cnx.getColor(R.color.white_light))
                .setBackgroundTint(cnx.getColor(R.color.blue_dark))
                .show();

    }

}
