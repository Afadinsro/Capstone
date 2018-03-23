package com.adino.capstone.trending;

import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adino.capstone.R;
import com.adino.capstone.glide.GlideApp;
import com.adino.capstone.model.Trending;

/**
 * Created by afadinsro on 3/23/18.
 */

public class TrendingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String DIALOG_TAG = "Trending Dialog";
    private Context context;
    private ImageView imgTrendingPic;
    private TextView txtTitle;
    private TextView txtDescription;
    private FragmentManager fragmentManager;

    private static final String TAG = "TrendingViewHolder";

     TrendingViewHolder(Context context, View itemView, FragmentManager fragmentManager) {
        super(itemView);
        itemView.setOnClickListener(this);
        setContext(context);
        CardView cardView = (CardView)itemView.findViewById(R.id.item_cv_trending);
        txtDescription = (TextView)itemView.findViewById(R.id.txt_trending_details);
        txtTitle = (TextView)itemView.findViewById(R.id.txt_trending_title);
        imgTrendingPic = (ImageView)itemView.findViewById(R.id.item_img_trending_pic);
        this.fragmentManager = fragmentManager;
    }

    void bindViewHolder(Trending model){
        Log.d(TAG, "bindViewHolder: called");
        txtTitle.setText(model.getTitle());
        txtDescription.setText(model.getDetails());
        GlideApp.with(getContext())
                .load(model.getImageURL())
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .fallback(R.drawable.ic_image_black_24dp)
                .into(imgTrendingPic);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onClick(View v) {

        TrendingDialogFragment.newInstance("Title", "Details", "url").show(fragmentManager, DIALOG_TAG);
    }
}
