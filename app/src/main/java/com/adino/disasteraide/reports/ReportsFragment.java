package com.adino.disasteraide.reports;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adino.disasteraide.R;
import com.adino.disasteraide.model.Report;
import com.adino.disasteraide.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

import static com.adino.disasteraide.util.Constants.IMAGE_BYTE_ARRAY;
import static com.adino.disasteraide.util.Constants.IMAGE_FILE_ABS_PATH;
import static com.adino.disasteraide.util.Constants.PHOTOS;
import static com.adino.disasteraide.util.Constants.PUSHED_REPORT_KEY;
import static com.adino.disasteraide.util.Constants.REPORTS;
import static com.adino.disasteraide.util.Constants.REPORT_FIELD_IMAGEURL;
import static com.adino.disasteraide.util.Constants.REPORT_FIELD_LATITUDE;
import static com.adino.disasteraide.util.Constants.REPORT_FIELD_LONGITUDE;
import static com.adino.disasteraide.util.Constants.USERS;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String pushKey;
    private String imageAbsPath;
    private byte[] photo;
    private static final String TAG = "ReportsFragment";
    private RecyclerView rv_reports;
    private Context context;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    /**
     * Firebase
     */
    private FirebaseRecyclerAdapter<Report, ReportViewHolder> adapter;
    private DatabaseReference reportsDatabaseRef = FirebaseDatabase.getInstance().getReference()
            .child(REPORTS).child(userID);
    private DatabaseReference usersDatabaseRef = FirebaseDatabase.getInstance().getReference(USERS)
            .child(userID);
    private UploadTask uploadTask;
    private StorageReference photosStorageRef = FirebaseStorage.getInstance().getReference()
            .child(PHOTOS);

    private OnFragmentInteractionListener mListener;

    public ReportsFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param key Parameter 1.
     * @param path Parameter 2.
     * @return A new instance of fragment ReportsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportsFragment newInstance(String key, String path,byte[] photo) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putString(PUSHED_REPORT_KEY, key);
        args.putString(IMAGE_FILE_ABS_PATH, path);
        args.putByteArray(IMAGE_BYTE_ARRAY, photo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: In onCreate");
        if (getArguments() != null) {
            pushKey = getArguments().getString(PUSHED_REPORT_KEY);
            imageAbsPath = getArguments().getString(IMAGE_FILE_ABS_PATH);
            photo = getArguments().getByteArray(IMAGE_BYTE_ARRAY);
            usersDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()){
                        User user = dataSnapshot.getValue(User.class);
                        FirebaseDatabase.getInstance().getReference().child(REPORTS).child(userID)
                                .child(pushKey).child(REPORT_FIELD_LONGITUDE).setValue(user.getLongitude());
                        FirebaseDatabase.getInstance().getReference().child(REPORTS).child(userID)
                                .child(pushKey).child(REPORT_FIELD_LATITUDE).setValue(user.getLatitude());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            uploadImage();
        }
        context = getContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        rv_reports = (RecyclerView)view.findViewById(R.id.rv_report);
        // Instantiate layout manager and add it to the RecyclerView
        rv_reports.setLayoutManager(new LinearLayoutManager(getContext()));
        /*
         * Firebase
         */
        reportsDatabaseRef.keepSynced(true);
        
        Query query = reportsDatabaseRef.limitToLast(100);
        FirebaseRecyclerOptions<Report> options = new FirebaseRecyclerOptions.Builder<Report>()
                .setQuery(query, Report.class)
                .build();
        Log.d(TAG, "onCreateView: before adapter is created");
        //Use FirebaseRecyclerAdapter
        adapter = new FirebaseRecyclerAdapter<Report, ReportViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReportViewHolder holder, int position, @NonNull Report model) {
                holder.bindViewHolder(model);

            }

            @Override
            public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
                return new ReportViewHolder(getContext(), view);
            }
        };
        rv_reports.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        rv_reports.addItemDecoration(dividerItemDecoration);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     *  Uploads the captured image to Firebase storage using an upload task
     *  On successful upload, a record of the report is uploaded to the database
     *  with all report details including a link to the image in storage
     */
    private void uploadImage(){

        File imageFile = new File(imageAbsPath);
        Uri file = Uri.fromFile(imageFile);

        StorageReference photoRef = photosStorageRef.child(userID).child(file.getLastPathSegment());

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        //uploadTask = photoRef.putBytes(photo, metadata);
        uploadTask = photoRef.putFile(file, metadata);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                assert downloadUrl != null;
                String imageURL = downloadUrl.toString();
                reportsDatabaseRef.child(pushKey).child(REPORT_FIELD_IMAGEURL).setValue(imageURL);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // TODO alert user
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
