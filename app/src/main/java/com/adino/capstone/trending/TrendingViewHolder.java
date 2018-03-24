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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.adino.capstone.util.Constants.TRENDING;

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
    private ArrayList<Trending> models;

    private static final String TAG = "TrendingViewHolder";

     TrendingViewHolder(final Context context, View itemView, FragmentManager fragmentManager) {
        super(itemView);
        itemView.setOnClickListener(this);
        setContext(context);
        CardView cardView = (CardView)itemView.findViewById(R.id.item_cv_trending);
        txtDescription = (TextView)itemView.findViewById(R.id.txt_trending_details);
        txtTitle = (TextView)itemView.findViewById(R.id.txt_trending_title);
        imgTrendingPic = (ImageView)itemView.findViewById(R.id.item_img_trending_pic);
        this.fragmentManager = fragmentManager;
        models = new ArrayList<>();
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(TRENDING);
         databaseReference.addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                 Trending temp = dataSnapshot.getValue(Trending.class);
                 Log.d(TAG, "onChildAdded: trending object added");
                 models.add(temp);
             }

             @Override
             public void onChildChanged(DataSnapshot dataSnapshot, String s) {
             }

             @Override
             public void onChildRemoved(DataSnapshot dataSnapshot) {

             }

             @Override
             public void onChildMoved(DataSnapshot dataSnapshot, String s) {

             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });
    }

    void bindViewHolder(Trending model){
        Log.d(TAG, "bindViewHolder: called");
        txtTitle.setText(model.getTitle());
        txtDescription.setText(model.getDetails());
        GlideApp.with(getContext())
                .load(model.getImageURL())
                .placeholder(R.drawable.ic_sync_black_200dp)
                .error(R.drawable.ic_broken_image_black_200dp)
                .fallback(R.drawable.ic_image_black_200dp)
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
        Log.d(TAG, "onClick: Item " + getAdapterPosition());
        Log.d(TAG, "onClick: Size " + models.size());
        Trending model = this.models.get(getAdapterPosition());
        TrendingDialogFragment.newInstance(model.getTitle(), model.getDetails(), model.getImageURL())
                .show(fragmentManager, DIALOG_TAG);
    }


}
