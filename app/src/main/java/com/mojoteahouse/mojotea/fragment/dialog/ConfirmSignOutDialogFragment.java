package com.mojoteahouse.mojotea.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.mojoteahouse.mojotea.R;

public class ConfirmSignOutDialogFragment extends DialogFragment {

    private static final String TAG = ConfirmSignOutDialogFragment.class.getName();

    private SignOutListener signOutListener;

    public interface SignOutListener {

        void onSignOutConfirmed();
    }

    public static void show(FragmentManager fragmentManager) {
        ConfirmSignOutDialogFragment fragment = new ConfirmSignOutDialogFragment();
        fragment.setRetainInstance(true);
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            signOutListener = (SignOutListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + SignOutListener.class.getSimpleName());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.settings_activity_dialog_sign_out_title)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOutListener.onSignOutConfirmed();
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
