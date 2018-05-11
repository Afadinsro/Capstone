package com.adino.disasteraide.contacts;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adino.disasteraide.R;
import com.adino.disasteraide.glide.GlideApp;
import com.adino.disasteraide.model.Agency;
import com.adino.disasteraide.model.Report;
import com.adino.disasteraide.util.Permissions;

import java.util.ArrayList;

import static com.adino.disasteraide.util.Constants.REQUEST_PHONE_CALL_PERMISSION;
import static com.adino.disasteraide.util.Constants.SMS;

/**
 * Created by afadinsro on 18/4/18.
 */

public class AgencyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView imgAgencyPic;
    private ImageView imgMessageIcon;
    private ImageView imgPhoneIcon;
    private TextView txtName;
    private TextView txtInfo;
    private Context context;
    private Activity activity;
    private ArrayList<Agency> agencies = new ArrayList<>();

    private static final String TAG = "AgencyViewHolder";


    AgencyViewHolder(Context context, Activity activity, View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        setContext(context);
        this.activity = activity;

        CardView cvContact = (CardView) itemView.findViewById(R.id.item_cv_contact);
        imgPhoneIcon = (ImageView)itemView.findViewById(R.id.img_phone_icon);
        imgAgencyPic = (ImageView)itemView.findViewById(R.id.img_agency_pic);
        imgMessageIcon = (ImageView)itemView.findViewById(R.id.img_message_icon);
        txtName = (TextView)itemView.findViewById(R.id.txt_agency_name);
        txtInfo = (TextView)itemView.findViewById(R.id.txt_agency_details);
    }

    /**
     *
     * @param model Report
     */
    void bindViewHolder(Agency model){
        agencies.add(model);
        final String number =  "" + model.getPhone();

        txtName.setText(model.getName());
        txtInfo.setText(model.getInfo());
        Log.d(TAG, "bindViewHolder: " + model.getImageURL());
        GlideApp.with(getContext())
                .load(model.getImageURL())
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .fallback(R.drawable.ic_image_black_24dp)
                .into(imgAgencyPic);

        imgMessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msgIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts(SMS, number, null));
                context.startActivity(msgIntent);
//                Toast.makeText(context, "Phone number: " + number, Toast.LENGTH_SHORT).show();
            }
        });

        imgPhoneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                String[] permissions = {Manifest.permission.CALL_PHONE};
                boolean phonePermGranted = Permissions.checkPermission(context, permissions[0]);
                if(!phonePermGranted){
                    ActivityCompat.requestPermissions(activity, permissions, REQUEST_PHONE_CALL_PERMISSION);
                }
                context.startActivity(callIntent);
//                Toast.makeText(context, "Phone number: " + number, Toast.LENGTH_SHORT).show();
            }
        });
    }





    @Override
    public void onClick(View v) {

    }

    public void setContext(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }



}
