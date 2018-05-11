package com.adino.disasteraide.trending;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adino.disasteraide.R;
import com.adino.disasteraide.glide.GlideApp;
import com.adino.disasteraide.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;

import static com.adino.disasteraide.util.Constants.DIALOG_DETAILS;
import static com.adino.disasteraide.util.Constants.DIALOG_IMAGE_URL;
import static com.adino.disasteraide.util.Constants.DIALOG_TITLE;
import static com.adino.disasteraide.util.Constants.DIALOG_TOPIC;
import static com.adino.disasteraide.util.Constants.DIALOG_USER_SUBSCRIPTIONS;
import static com.adino.disasteraide.util.Constants.USERS;
import static com.adino.disasteraide.util.Constants.USER_FIELD_SUBSCRIPTIONS;

/**
 * Created by afadinsro on 3/23/18.
 */

public class TrendingDialogFragment extends DialogFragment {

    private static final String TAG = "TrendingDialogFragment";
    private Context context = getContext();
    private ImageView imgTrendingPic;
    private ImageView imgSubscribe;
    private TextView txtDetails;
    private TextView txtTitle;
    private TextView txtActionOK;
    private String userID = "";
    private String topic;
    private ArrayList<String> subscriptions = new ArrayList<>();
    private String userTopics = "";
    private boolean subscribedToTopic = false;
    private DatabaseReference reference;
    private ValueEventListener subscriptionsValListener;


    public static TrendingDialogFragment newInstance(String title, String details, String url, String topic){
        TrendingDialogFragment dialogFragment = new TrendingDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        args.putString(DIALOG_DETAILS, details);
        args.putString(DIALOG_TOPIC, topic);
        args.putString(DIALOG_IMAGE_URL, url);;
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topic = getArguments().getString(DIALOG_TOPIC);
//        userTopics = getArguments().getString(DIALOG_USER_SUBSCRIPTIONS);
//        addToArray(userTopics);

        try {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }catch (NullPointerException e){
            Log.d(TAG, "onCreateView: " + e.getMessage());
        }
        Log.d(TAG, "onCreateView: UID: " + userID);
        reference = FirebaseDatabase.getInstance().getReference(USERS).child(userID).child(USER_FIELD_SUBSCRIPTIONS);
        Log.d(TAG, "onCreateView: reference created.");
        subscriptionsValListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        try {
                            userTopics = dataSnapshot.getValue(String.class);
                            if(userTopics.contains(topic)){
                                subscribedToTopic = true;
                                imgSubscribe.setColorFilter(ContextCompat.getColor(context,
                                        R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
                            }
                            Log.d(TAG, "onDataChange: topics: " + userTopics);
                            addToArray(userTopics);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("TAG", " it's null.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
        reference.addValueEventListener(subscriptionsValListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_trending, container, false);
        txtDetails = (TextView)view.findViewById(R.id.txt_dialog_trending_details);
        txtTitle = (TextView)view.findViewById(R.id.txt_dialog_trending_title);
        imgTrendingPic = (ImageView)view.findViewById(R.id.dialog_img_trending_pic);
        txtActionOK = (TextView)view.findViewById(R.id.txt_dialog_trending_ok);
        imgSubscribe = (ImageView)view.findViewById(R.id.img_subscribe);

        // Populate fields
        txtTitle.setText(getArguments().getString(DIALOG_TITLE));
        txtDetails.setText(getArguments().getString(DIALOG_DETAILS));
        txtDetails.setMovementMethod(new ScrollingMovementMethod());

        Log.d(TAG, "onCreateView: Topic: " + topic);
        GlideApp.with(context)
                .load(getArguments().getString(DIALOG_IMAGE_URL))
                .placeholder(R.drawable.ic_sync_black_200dp)
                .error(R.drawable.ic_broken_image_black_200dp)
                .fallback(R.drawable.ic_image_black_200dp)
                .into(imgTrendingPic);

        // Get user id

        for (String subscription : subscriptions) {
            Log.d(TAG, "onCreateView: Content before check: " + subscription);
        }

        // Handle click event for Subscribe 'button'
        imgSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!subscribedToTopic) {
                    // User has not previously subscribed to this topic
                    subscribeToTopic(topic);
                }else {
                    // User has already subscribed to topic
                    // Therefore unsubscribe
                    unsubscribeFromTopic(topic);
                }
            }
        });

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

    private void addToArray(String userTopics) {
        Log.d(TAG, "addToArray: Adding to array...");
        // Empty array
        subscriptions.clear();
        if(userTopics.contains(";")) {
            // More than one subscription
            String[] arr = userTopics.split(";");
            for (String s : arr) {
                Log.d(TAG, "onDataChange: Content after split: " + s);
                subscriptions.add(s);
            }

        }else{
            subscriptions.add(userTopics);
            for (String subscription : subscriptions) {
                Log.d(TAG, "onDataChange: contents of array: " + subscription);
            }
        }
    }

    private void unsubscribeFromTopic(String topic) {
        imgSubscribe.setColorFilter(ContextCompat.getColor(context, R.color.grey),
                PorterDuff.Mode.SRC_IN);
        imgSubscribe.setImageResource(R.drawable.ic_notifications_off_black_24dp);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
        subscribedToTopic = false;
        if(subscriptions.contains(topic)) {
            subscriptions.remove(topic);
        }
        updateSubscriptions();
    }

    private void subscribeToTopic(String topic) {
        imgSubscribe.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark),
                PorterDuff.Mode.SRC_IN);
        imgSubscribe.setImageResource(R.drawable.ic_notifications_active_black_24dp);
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        subscribedToTopic = true;
        if(!subscriptions.contains(topic)) {
            subscriptions.add(topic);
        }
        updateSubscriptions();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        reference.removeEventListener(subscriptionsValListener);
    }

    private String getSubscriptionString(ArrayList<String> subscriptions){
        StringBuilder stringBuilder = new StringBuilder();
        int size = subscriptions.size();
        for (String s : subscriptions) {
            stringBuilder.append(s);
            if(size > 1 && s.length() > 1) {
                // Don't append ';' after last subscription
                stringBuilder.append(";");
            }
            size--;
        }
        Log.d(TAG, "getSubscriptionString: subscriptions: " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    private void updateSubscriptions(){
        String subscriptionString = getSubscriptionString(subscriptions);
        Log.d(TAG, "updateSubscriptions: " + subscriptionString);
        reference.setValue(subscriptionString);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        // User has already subscribed to topic
        if(subscriptions.contains(topic)){
            subscribedToTopic = true;
            Log.d(TAG, "onCreateView: Subscribed already?: " + "true");
            // User has already subscribed to topic
            imgSubscribe.setColorFilter(ContextCompat.getColor(context,
                    R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        }else {
            Log.d(TAG, "onCreateView: Subscribed already?: " + "false");
        }
    }
}
