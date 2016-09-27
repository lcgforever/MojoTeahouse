package com.mojoteahouse.mojotea.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.mojoteahouse.mojotea.R;

public class ClosedNowDialogFragment extends DialogFragment {

    private static final String TAG = ClosedNowDialogFragment.class.getName();

    public static void show(FragmentManager fragmentManager) {
        ClosedNowDialogFragment fragment = new ClosedNowDialogFragment();
        fragment.setRetainInstance(true);
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_NoTitle);
        builder.setView(R.layout.fragment_dialog_closed_now)
                .setCancelable(false)
                .setPositiveButton(R.string.got_it_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
