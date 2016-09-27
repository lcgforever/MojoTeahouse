package com.mojoteahouse.mojotea.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.mojoteahouse.mojotea.R;

public class ProgressDialogFragment extends DialogFragment {

    private static final String TAG = ProgressDialogFragment.class.getName();
    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    private String message;

    public static void showWithMessage(FragmentManager fragmentManager, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(arguments);
        fragment.setRetainInstance(true);
        fragment.show(fragmentManager, TAG);
    }

    public static void dismiss(FragmentManager fragmentManager) {
        ProgressDialogFragment fragment = (ProgressDialogFragment) fragmentManager.findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_progress_dialog, null);
        TextView textView = (TextView) view.findViewById(R.id.progress_dialog_text_view);
        textView.setText(getArguments().getString(EXTRA_MESSAGE, ""));
        builder.setView(view)
                .setCancelable(false);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
