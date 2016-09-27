package com.mojoteahouse.mojotea.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.mojoteahouse.mojotea.R;

public class DeleteCartItemDialogFragment extends DialogFragment {

    private static final String TAG = DeleteCartItemDialogFragment.class.getName();
    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private DeleteCartItemListener deleteCartItemListener;

    public interface DeleteCartItemListener {

        void onDeleteConfirmed();
    }

    public static void show(FragmentManager fragmentManager) {
        DeleteCartItemDialogFragment fragment = new DeleteCartItemDialogFragment();
        fragment.setRetainInstance(true);
        fragment.show(fragmentManager, TAG);
    }

    public static void showWithMessage(FragmentManager fragmentManager, String message) {
        DeleteCartItemDialogFragment fragment = new DeleteCartItemDialogFragment();
        fragment.setRetainInstance(true);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(bundle);
        fragment.show(fragmentManager, TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            deleteCartItemListener = (DeleteCartItemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement "
                    + DeleteCartItemListener.class.getSimpleName());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            builder.setMessage(bundle.getString(EXTRA_MESSAGE));
        } else {
            builder.setMessage(R.string.delete_cart_item_dialog_message);
        }
        builder.setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCartItemListener.onDeleteConfirmed();
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onDetach() {
        deleteCartItemListener = null;
        super.onDetach();
    }
}
