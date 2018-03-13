package com.adino.capstone.reports;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adino.capstone.R;
import com.adino.capstone.glide.GlideApp;
import com.adino.capstone.model.Report;

/**
 * Created by afadinsro on 3/3/18.
 */

public class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * For Reports (MainActivity)
     */
    private CardView cvReport;
    private ImageView imgReportPic;
    private ImageView imgReportCategory;
    private ImageView imgReportStatus;
    private TextView txtDate;
    private TextView txtLocation;
    private TextView txtCategory;
    private TextView txtCaption;
    private Context context;

    private static final String TAG = "ReportViewHolder";


    ReportViewHolder(View itemView, Context context) {
        super(itemView);
        itemView.setOnClickListener(this);
        setContext(context);
        cvReport = (CardView)itemView.findViewById(R.id.item_cv_report);
        imgReportCategory = (ImageView)itemView.findViewById(R.id.item_img_report_category);
        imgReportPic = (ImageView)itemView.findViewById(R.id.img_report_pic);
        imgReportStatus = (ImageView)itemView.findViewById(R.id.item_img_report_status);
        txtCaption = (TextView)itemView.findViewById(R.id.item_report_caption);
        txtCategory = (TextView)itemView.findViewById(R.id.item_report_category);
        txtLocation = (TextView)itemView.findViewById(R.id.txt_report_location_words);
        txtDate = (TextView)itemView.findViewById(R.id.item_report_date);
    }

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

    private void loadStatusIcon(String imageURL) {
        // TODO implement logic to know whether report has been uploaded online
        if(imageURL.startsWith("https")){

        }
        GlideApp.with(getContext())
                .load(R.drawable.ic_check_black_24dp)
                .placeholder(R.drawable.ic_autorenew_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .fallback(R.drawable.ic_image_black_24dp)
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
