package com.adino.capstone.trending;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adino.capstone.R;
import com.adino.capstone.glide.GlideApp;

/**
 * Created by afadinsro on 3/23/18.
 */

public class TrendingDialogFragment extends DialogFragment {
    private static final String DIALOG_TITLE = "Title";
    private static final String DIALOG_DETAILS = "Details";
    private static final String DIALOG_IMAGE_URL = "Image";
    private static final String TAG = "TrendingDialogFragment";

    private ImageView imgTrendingPic;
    private TextView txtDetails;
    private TextView txtTitle;
    private TextView txtActionOK;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_trending, container, false);
        txtDetails = (TextView) view.findViewById(R.id.txt_dialog_trending_details);
        txtTitle = (TextView) view.findViewById(R.id.txt_dialog_trending_title);
        imgTrendingPic = (ImageView)view.findViewById(R.id.dialog_img_trending_pic);
        txtActionOK = (TextView)view.findViewById(R.id.txt_dialog_trending_ok);

        // Populate fields
        txtTitle.setText(getArguments().getString(DIALOG_TITLE));
        txtDetails.setText(getArguments().getString(DIALOG_DETAILS));
        txtDetails.setMovementMethod(new ScrollingMovementMethod());
        GlideApp.with(getContext())
                .load(getArguments().getString(DIALOG_IMAGE_URL))
                .placeholder(R.drawable.ic_sync_black_200dp)
                .error(R.drawable.ic_broken_image_black_200dp)
                .fallback(R.drawable.ic_image_black_200dp)
                .into(imgTrendingPic);

        // Handle click event for OK 'button'
        txtActionOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: OK button of dialog clicked");
                getDialog().dismiss();
            }
        });
        return view;
    }

    public static TrendingDialogFragment newInstance(String title, String details, String url){
        TrendingDialogFragment dialogFragment = new TrendingDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        args.putString(DIALOG_DETAILS, details);
        args.putString(DIALOG_IMAGE_URL, url);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }
    /*
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
    */
}
