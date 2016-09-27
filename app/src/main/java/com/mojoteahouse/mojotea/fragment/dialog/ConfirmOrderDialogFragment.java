package com.mojoteahouse.mojotea.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.mojoteahouse.mojotea.R;

public class ConfirmOrderDialogFragment extends DialogFragment {

    private static final String TAG = ConfirmOrderDialogFragment.class.getName();

    private PlaceOrderListener placeOrderListener;

    public interface PlaceOrderListener {

        void onPlaceOrderConfirmed();
    }

    public static void show(FragmentManager fragmentManager) {
        ConfirmOrderDialogFragment fragment = new ConfirmOrderDialogFragment();
        fragment.setRetainInstance(true);
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            placeOrderListener = (PlaceOrderListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + PlaceOrderListener.class.getSimpleName());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.place_order_dialog_message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        placeOrderListener.onPlaceOrderConfirmed();
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

    @Override
    public void onDetach() {
        placeOrderListener = null;
        super.onDetach();
    }
}
