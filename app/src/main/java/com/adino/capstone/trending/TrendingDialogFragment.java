package com.adino.capstone.trending;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adino.capstone.R;

/**
 * Created by afadinsro on 3/23/18.
 */

public class TrendingDialogFragment extends DialogFragment {
    private static final String DIALOG_TITLE = "Title";
    private static final String DIALOG_DETAILS = "Details";
    private static final String DIALOG_IMAGE_URL = "Image";

    /*
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_trending, container, false);

        return view;
    }*/

    public static TrendingDialogFragment newInstance(String title, String details, String url){
        TrendingDialogFragment dialogFragment = new TrendingDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        args.putString(DIALOG_DETAILS, details);
        args.putString(DIALOG_IMAGE_URL, url);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        return new AlertDialog.Builder(activity)
                .setTitle(getArguments().getString(DIALOG_TITLE))
                .setMessage(getArguments().getString(DIALOG_DETAILS))
                .setIcon(R.drawable.ic_image_black_24dp)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                })
                .create();
    }
}
