package com.adino.disasteraide.capture;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.icu.util.Output;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adino.disasteraide.Manifest;
import com.adino.disasteraide.R;
import com.adino.disasteraide.capture.VideoFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class PictureFragment extends Fragment {

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
    private static final int REQUEST_CAMERA_PERMISSION = 500;
    private boolean flashSupported;
    private Handler backgroundHandler;
    private HandlerThread backgroundHandlerThread;

    private FloatingActionButton fabCapturePic;
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback()  {

        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                createCameraPreview();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private CameraManager manager;

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                openCamera();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public PictureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_capture_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fabSwitchToVideo = (FloatingActionButton) view.findViewById(R.id.fab_switch_to_video);
        fabSwitchToVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.capture_placeholder, new VideoFragment()).commit();

            }
        });

        textureView = (TextureView) view.findViewById(R.id.textureView_picture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);

        fabCapturePic = (FloatingActionButton)view.findViewById(R.id.fab_capture_pic);
        fabCapturePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void takePicture() {
        if(cameraDevice == null){
            return;
        }
        CameraManager cameraManager = (CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                assert cameraManager != null;
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraDevice.getId());

                Size[] jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);
                //Capture image with custom size
                int width = 640;
                int height = 480;
                assert jpegSizes != null;
                if(jpegSizes.length > 0){
                    //Assign width and height
                    width = jpegSizes[0].getWidth();
                    height = jpegSizes[0].getHeight();
                }
                imageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                List<Surface> outputSurface = new ArrayList<>(2);
                outputSurface.add(imageReader.getSurface());
                outputSurface.add(new Surface(textureView.getSurfaceTexture()));

                final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(imageReader.getSurface());
                captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                // Check device orientation
                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

                // File for image
                file = new File(Environment.getExternalStorageDirectory() + "/" + UUID.randomUUID().toString() + ".jpg");
                final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener()  {
                    @Override
                    public void onImageAvailable(ImageReader reader) {
                        Image image = null;
                        try{
                            image = imageReader.acquireLatestImage();
                            assert image.getPlanes() != null;
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.capacity()];
                            buffer.get(bytes);
                            save(bytes);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        } finally {
                            if(image != null){
                                image.close();
                            }
                        }

                    }
                    private void save(byte[] bytes)throws IOException {
                        OutputStream outputStream = null;
                        try{
                            outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);
                        }finally {
                            if(outputStream != null){
                                outputStream.close();
                            }
                        }
                    }


                };

                imageReader.setOnImageAvailableListener(onImageAvailableListener, backgroundHandler);
                final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {

                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);
                        Toast.makeText(getContext(),"Saved " + file, Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            createCameraPreview();
                        }
                    }
                };

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {

                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                captureSession.capture(captureBuilder.build(), captureListener, backgroundHandler);

                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, backgroundHandler);
                }

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }
        Log.d(TAG, "takePicture: CameraDevice is null");
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createCameraPreview()  {
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                     if(cameraDevice == null){
                         return;
                     }
                     captureSession = session;
                     updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(getContext(), "Changed", Toast.LENGTH_SHORT).show();

                }
            }, null);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updatePreview() {
        if(cameraDevice == null) {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        try{
            captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openCamera() {
        CameraManager manager = (CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);
        try{
            assert manager != null;
            cameraID = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map!= null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            // Check for permissions
            if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraID, stateCallback,null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA_PERMISSION:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getContext(), "You cannot use camera without permission.",
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        textureView.setVisibility(View.VISIBLE);
        startBackgroundThread();
        if(textureView.isAvailable()){
            openCamera();
        }else{
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    private void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("Camera Background");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }

    @Override
    public void onPause() {
        stopBackgroundThread();
        super.onPause();
        textureView.setVisibility(View.GONE);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundThread() {
        backgroundHandlerThread.quitSafely();
        try{
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
