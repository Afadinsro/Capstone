package com.adino.disasteraide.capture;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import com.adino.disasteraide.R;
import com.adino.disasteraide.capture.PictureFragment;

import java.io.File;

public class CaptureActivity extends AppCompatActivity {

    private static final String TAG = "CaptureActivity";
    private TextureView textureView;

    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraID;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    private File file;
    private static int REQUEST_CAMERA_PERMISSION = 500;
    private boolean flashSupported;
    private Handler backgroundHandler;
    private HandlerThread backgroundHandlerThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        // Initialize content view to Picture fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.capture_placeholder, new PictureFragment()).commit();



    }

    private void takePicture() {
        if(cameraDevice != null){
            CameraManager cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                try {
                    assert cameraManager != null;
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraDevice.getId());
                    Size[] jpegSizes = null;
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }else{
                //TODO Open the default camera app

            }
        }
        Log.d(TAG, "takePicture: CameraDevice is null");
    }

    private void openCamera() {

    }
}
