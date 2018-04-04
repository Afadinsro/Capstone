package com.adino.capstone.util;

import com.adino.capstone.model.DisasterCategory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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
    public static final int REQUEST_VIDEO_INTENT = 20890;
    public static final int REQUEST_SIGN_IN = 573;
    public static final int REQUEST_GPS_ENABLE = 389;
    public static final int REQUEST_PENDING_INTENT = 437;

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
    public static final String REPORT_FIELD_TOPIC = "topic";
    public static final String USER_FIELD_SUBSCRIPTIONS = "subscriptions";

    /*
    DatabaStoragese References
     */
    public static final String PHOTOS = "photos";

    /*
    Database References
     */
    public static final String REPORTS = "reports";
    public static final String TRENDING = "trending";
    public static final String USERS = "users";

    /*
    Grid Layout
     */
    public static final int GRID_COLUMN_COUNT = 2;

    /*
    Map constants
     */
    public static final float DEFAULT_ZOOM = 15f;
    public static final LatLngBounds WORLD_LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168),
            new LatLng(71, 136));
    public static final LatLng ghana_SW = new LatLng(5.082787, -2.878441);
    public static final LatLng ghana_NE = new LatLng(11.043421, 0.636050);
    public static final LatLngBounds GHANA = new LatLngBounds(ghana_SW, ghana_NE);
    public static final LatLng DEFAULT_LATLNG_GBAWE = new LatLng(5.582830, -0.307473);

    /*
    Trending Dialog Tags
     */
    public static final String DIALOG_TITLE = "Title";
    public static final String DIALOG_DETAILS = "Details";
    public static final String DIALOG_IMAGE_URL = "Image";
    public static final String DIALOG_TAG = "Trending Dialog";
    public static final String DIALOG_TOPIC = "Default";
    public static final String DIALOG_USER_SUBSCRIPTIONS = "Subscriptions";

    /*
    User Fields
     */
    public static final String USER_ID = "userID";
    public static final String USER_SUBSCRIPTIONS = "subscriptions";

    /*
    Notifications
     */
    public static final int NOTIFICATION_ID = 4989;

    /*
    Bottom Navigation IDs
     */
    public static final int MAP_NAV_ID = 0;
    public static final int TRENDING_NAV_ID = 1;
    public static final int CAPTURE_NAV_ID = 2;
    public static final int REPORTS_NAV_ID = 3;
    public static final int CONTACTS_NAV_ID = 4;
}