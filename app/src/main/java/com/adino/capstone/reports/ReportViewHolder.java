package com.adino.capstone.reports;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.adino.capstone.model.Report;

/**
 * Created by afadinsro on 3/3/18.
 */

public class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    /**
     * For Flames (MainActivity)
     */
    private CardView cvReport;
    private ImageView imgReportPic;

    public ReportViewHolder(View itemView, Context context) {
        super(itemView);
    }

    public void bindViewHolder(Report model){

    }

    @Override
    public void onClick(View v) {

    }
}
