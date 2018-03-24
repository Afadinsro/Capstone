package com.adino.capstone.util;

import com.adino.capstone.model.DisasterCategory;

/**
 * Created by afadinsro on 1/4/18.
 */

public final class Constants {
    public static final int PRELOAD_AHEAD_ITEMS = 6;
    public static final int IMAGE_WIDTH_PIXELS = 608;
    public static final int IMAGE_HEIGHT_PIXELS = 608;

    /*
    CODES
     */
    public static final int ERROR_DIALOG_REQUEST = 900;
    public static final int REQUEST_LOCATION_PERMISSION = 587;
    public static final int REQUEST_CAMERA_PERMISSION = 200;
    public static final int REQUEST_IMAGE_INTENT = 500;
    public static final int REQUEST_VIDEO_INTENT = 200;

    /*
    TAGS
     */
    public static final String UPLOAD_MEDIA_TAG = "upload-report-media";

    /*
    Extra keys
     */
    public static final String IMAGE_FILE_ABS_PATH = "imageFileAbsPath";
    public static final String IMAGE_BYTE_ARRAY = "imageByteArray";
    public static final String PUSHED_REPORT_KEY = "pushedReportKey";
    public static final String PATH_TO_IMAGE_FILE = "pathToImageFile";

    /*
    Database Fields
     */
    public static final String REPORT_FIELD_CAPTION = "caption";
    public static final String REPORT_FIELD_CATEGORY = "category";
    public static final String REPORT_FIELD_DATE = "date";
    public static final String REPORT_FIELD_IMAGEURL = "imageURL";
    public static final String REPORT_FIELD_LOCATION = "location";
    public static final String REPORT_FIELD_VIDEOURL = "videoURL";

    /*
    Databse References
     */
    public static final String REPORTS = "reports";
    public static final String TRENDING = "trending";

    /*
    Grid Layout
     */
    public static final int GRID_COLUMN_COUNT = 2;
}
