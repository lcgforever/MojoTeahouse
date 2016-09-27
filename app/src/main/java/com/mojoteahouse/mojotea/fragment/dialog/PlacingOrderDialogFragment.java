package com.mojoteahouse.mojotea.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.mojoteahouse.mojotea.R;

public class PlacingOrderDialogFragment extends DialogFragment {

    private static final String TAG = PlacingOrderDialogFragment.class.getName();

    public static void show(FragmentManager fragmentManager) {
        PlacingOrderDialogFragment fragment = new PlacingOrderDialogFragment();
        fragment.setRetainInstance(true);
        fragment.show(fragmentManager, TAG);
    }

    public static void dismiss(FragmentManager fragmentManager) {
        PlacingOrderDialogFragment fragment = (PlacingOrderDialogFragment) fragmentManager.findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.fragment_dialog_placing_order)
                .setCancelable(false);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
