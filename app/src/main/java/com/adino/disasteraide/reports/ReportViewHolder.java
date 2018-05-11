package com.adino.disasteraide.reports;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adino.disasteraide.R;
import com.adino.disasteraide.glide.GlideApp;
import com.adino.disasteraide.model.Report;

/**
 * Created by afadinsro on 3/3/18.
 */

public class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView imgReportPic;
    private ImageView imgReportCategory;
    private ImageView imgReportStatus;
    private TextView txtDate;
    private TextView txtLocation;
    private TextView txtCategory;
    private TextView txtCaption;
    private Context context;

    private static final String TAG = "ReportViewHolder";


    ReportViewHolder(Context context, View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        setContext(context);
        /*
        For Reports (MainActivity)
        */
        CardView cvReport = (CardView) itemView.findViewById(R.id.item_cv_report);
        imgReportCategory = (ImageView)itemView.findViewById(R.id.item_img_report_category);
        imgReportPic = (ImageView)itemView.findViewById(R.id.img_report_pic);
        imgReportStatus = (ImageView)itemView.findViewById(R.id.item_img_report_status);
        txtCaption = (TextView)itemView.findViewById(R.id.item_report_caption);
        txtCategory = (TextView)itemView.findViewById(R.id.item_report_category);
        txtLocation = (TextView)itemView.findViewById(R.id.txt_report_location_words);
        txtDate = (TextView)itemView.findViewById(R.id.item_report_date);
    }

    /**
     *
     * @param model Report
     */
    void bindViewHolder(Report model){
        txtCaption.setText(model.getCaption());
        txtCategory.setText(model.getCategory());
        txtLocation.setText(model.getLocation());
        txtDate.setText(model.getDate());
        Log.d(TAG, "bindViewHolder: " + model.getImageURL());
        GlideApp.with(getContext())
                .load(model.getImageURL())
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .fallback(R.drawable.ic_image_black_24dp)
                .into(imgReportPic);

        loadCategoryIcon(model.getCategory());
        loadStatusIcon(model.getImageURL());
    }

    /**
     * Loads a drawable that represents the status of the report sent.
     * Statuses include
     * 1. Pending
     * 2. Sent
     * 3. Not sent (Handled in the Reports fragment)
     * @param imageURL Image URL
     */
    private void loadStatusIcon(String imageURL) {
        // Default status - waiting
        int drawable = R.drawable.ic_watch_later_black_24dp;
        imgReportStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.yellow), PorterDuff.Mode.SRC_IN);
        // Report loaded online when URL starts with 'https'
        if(imageURL.startsWith("https")){
            // Report loaded successfully
            drawable = R.drawable.ic_check_circle_black_24dp;
            imgReportStatus.setColorFilter(ContextCompat.getColor(getContext(), R.color.green), PorterDuff.Mode.SRC_IN);
        }
        // Load drawable with Glide
        GlideApp.with(getContext())
                .load(drawable)
                .placeholder(R.drawable.ic_sync_black_24dp)
                .error(R.drawable.ic_sync_problem_black_24dp)
                .fallback(R.drawable.ic_sync_black_24dp)
                .into(imgReportStatus);

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

    /**
     * Loads the icon corresponding to the category of the report
     * @param category Report Category
     */
    private void loadCategoryIcon(String category){
        int drawable = -1;
        switch (category){
            case "EARTHQUAKE":
                drawable = R.drawable.ic_earthquake;
                break;
            case "EPIDEMIC":
                drawable = R.drawable.ic_medicines;
                break;
            case "FLOOD":
                drawable = R.drawable.ic_flood;
                break;
            case "FIRE":
                drawable = R.drawable.ic_flame;
                break;
            case "METEOROLOGICAL":
                drawable = R.drawable.ic_storm;
                break;
            case "MOTOR ACCIDENT":
            case "MOTOR_ACCIDENT":
                drawable = R.drawable.ic_car_collision;
                break;
            default:
                drawable = R.drawable.ic_image_black_24dp;
        }
        GlideApp.with(getContext())
                .load(drawable)
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .fallback(R.drawable.ic_image_black_24dp)
                .into(imgReportCategory);
    }
}
