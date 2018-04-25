package com.adino.capstone.map;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.adino.capstone.R;
import com.adino.capstone.util.Permissions;
import com.adino.capstone.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.adino.capstone.util.Constants.DEFAULT_LATLNG_GBAWE;
import static com.adino.capstone.util.Constants.DEFAULT_ZOOM;
import static com.adino.capstone.util.Constants.ERROR_DIALOG_REQUEST;
import static com.adino.capstone.util.Constants.REQUEST_GPS_ENABLE;
import static com.adino.capstone.util.Constants.REQUEST_LOCATION_PERMISSION;
import static com.adino.capstone.util.Constants.USERS;
import static com.adino.capstone.util.Constants.USER_FIELD_LATITUDE;
import static com.adino.capstone.util.Constants.USER_FIELD_LONGITUDE;
import static com.adino.capstone.util.Constants.WORLD_LAT_LNG_BOUNDS;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /**
     * Parameters
     */
    private String mParam1;
    private String mParam2;
    private FusedLocationProviderClient locationProviderClient;
    private GoogleMap map;
    private MapView mapView;

    private boolean locationPermissionGranted;
    private Context context;
    private AutoCompleteTextView txtSearch;

    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient googleApiClient;
    private GeoDataClient geoDataClient;
    private LocationManager locationManager;
    private LatLng currentLocation;
    private static double latitude, longitude;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
            .child(USERS).child(userID);

    private static final String TAG = "MapFragment";
    private boolean isGPSOn = false;

    private OnFragmentInteractionListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Check if Google services is working
        if(!isGoogleServicesOK()){
            Toast.makeText(getContext(), "Please make sure a correct version of Google Play " +
                    "Services is installed", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        context = getContext();

        /*
        Location permissions
         */
        String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        // Check if location permissions have been granted
        if(Permissions.checkPermission(getContext(), permissions[0])){
            if(Permissions.checkPermission(getContext(), permissions[1])){
                locationPermissionGranted = true;
            }else {
                // Ask for location permission if not granted already
                Log.d(TAG, "onCreate: Requesting for permission");
                requestPermissions(permissions, REQUEST_LOCATION_PERMISSION);
            }
        }else {
            // Ask for location permission if not granted already
            requestPermissions(permissions, REQUEST_LOCATION_PERMISSION);
        }
        currentLocation = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        txtSearch = (AutoCompleteTextView) view.findViewById(R.id.txt_search_field);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        isGPSOn = Util.isGPSOn(context);
//        if(!isGPSOn){
//            Util.promptGPSOff(getActivity());
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isGPSOn = Util.isGPSOn(context);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double latitude = -1, longitude = -1;
                if (dataSnapshot.exists()) {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.getKey().equals(USER_FIELD_LATITUDE)){
                            latitude = snapshot.getValue(Double.class);
                        }else if(snapshot.getKey().equals(USER_FIELD_LONGITUDE)){
                            longitude = snapshot.getValue(Double.class);
                        }
                    }
                    Log.d(TAG, "onDataChange: Lat: " + latitude + ", Lng: " + longitude);
                    LatLng currentLocation = new LatLng(latitude, longitude);
                    moveCamera(currentLocation, DEFAULT_ZOOM);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void initSearchBar() {
        Log.d(TAG, "initSearchBar: Setting action listener for search bar...");
        // Initialize GeoDataClient
        geoDataClient = Places.getGeoDataClient(context, null);

        // Initialize Places autocomplete adapter
        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), geoDataClient,
                WORLD_LAT_LNG_BOUNDS, null);
        // Set autocomplete edit text view adapter
        txtSearch.setAdapter(placeAutocompleteAdapter);

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "onEditorAction: Called.");
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){

                    geoLocate();
                }
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: View created.");
        super.onViewCreated(view, savedInstanceState);
        if(locationPermissionGranted) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: Fragment attached to activity");
        super.onAttach(context);
        this.context = context;
        isGPSOn = Util.isGPSOn(context);
        if(!isGPSOn){
            Util.promptGPSOff(getActivity());
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        locationPermissionGranted = false;
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                for(int result: grantResults){
                    if(result != PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG, "onRequestPermissionsResult: Location permission denied!");
                        locationPermissionGranted = false;
                        // Permission was not granted
                        Toast.makeText(getContext(), "You cannot use map without location permission.",
                                Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        break;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: Location permission granted");
                locationPermissionGranted = true;
                mapView.onResume();
                mapView.getMapAsync(this);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        context = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        map = googleMap;
        moveCamera(DEFAULT_LATLNG_GBAWE, DEFAULT_ZOOM);
        map.setBuildingsEnabled(true);

        if(locationPermissionGranted){
            initSearchBar();
            getDeviceLocation();
            try {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }catch (SecurityException e){
                Log.d(TAG, "onMapReady: SecurityException" + e.getMessage());
            }

            // Pin trending disasters



        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        if (errorCode == ConnectionResult.API_UNAVAILABLE){
            Snackbar.make(txtSearch, "Google Places API unavailable. Autocompletion is unavailable.",
                    Snackbar.LENGTH_SHORT);
        }else if (errorCode == ConnectionResult.NETWORK_ERROR){
            Snackbar.make(txtSearch, "Network error. Check your internet connection. Autocompletion is unavailable.",
                    Snackbar.LENGTH_SHORT);
        }else if (errorCode == ConnectionResult.CANCELED){
            Snackbar.make(txtSearch, "Connection cancelled. Check your internet connection. Autocompletion is unavailable.",
                    Snackbar.LENGTH_SHORT);
        }else if (errorCode == ConnectionResult.TIMEOUT){
            Snackbar.make(txtSearch, "Connection timed out. Check your internet connection and try again. Autocompletion is unavailable.",
                    Snackbar.LENGTH_SHORT);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Check if Google Play services is available
     * @return True if Google Play Services is available, false otherwise.
     */
    private boolean isGoogleServicesOK(){
        Log.d(TAG, "isGoogleServicesOK: Checking if Google play services is working correctly.");
        // Check if Google play services is okay
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(available == ConnectionResult.SUCCESS){
            //Toast.makeText(this, "Google Play services is OK", Toast.LENGTH_SHORT).show();
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isGoogleServicesOK: An error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }

    private void getDeviceLocation(){
        locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try{
            if(locationPermissionGranted){
                if(Util.isGPSOn(context)) {
                    // GPS is on
                    Log.d(TAG, "getDeviceLocation: GPS is on");
                    Task location = locationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Found location.");
                                Location currentLocation = (Location) task.getResult();
                                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                userReference = FirebaseDatabase.getInstance().getReference().child(USERS).child(userID);
                                userReference.child(USER_FIELD_LATITUDE).setValue(latLng.latitude);
                                userReference.child(USER_FIELD_LONGITUDE).setValue(latLng.longitude);
                                moveCamera(latLng, DEFAULT_ZOOM);
                            } else {
                                Log.d(TAG, "onComplete: Couldn't find location");
                                Toast.makeText(getContext(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    // GPS is off
                    Log.d(TAG, "getDeviceLocation: GPS is off");
                }
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }


    private void geoLocate() {
        Log.d(TAG, "geoLocate: Geolocating...");
        String searchString = txtSearch.getText().toString();
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> addresses = new ArrayList<>();
        try{
            // Get 1 result from search
            addresses = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if(addresses.size() > 0){
            Address address = addresses.get(0);
            Log.d(TAG, "geoLocate: Location found: " + address.toString());
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            moveCamera(latLng, DEFAULT_ZOOM);
            // An address was found

        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: Moving the camera to lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


}
