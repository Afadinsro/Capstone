package com.adino.capstone.contacts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adino.capstone.R;
import com.adino.capstone.model.Agency;
import com.adino.capstone.model.Report;
import com.adino.capstone.reports.ReportViewHolder;
import com.adino.capstone.util.Permissions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.adino.capstone.util.Constants.CONTACTS;
import static com.adino.capstone.util.Constants.REQUEST_PHONE_CALL_PERMISSION;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Context context;
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String TAG = "ReportsFragment";
    /**
     * Firebase
     */
    private FirebaseRecyclerAdapter<Agency, AgencyViewHolder> adapter;
    private DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference().child(CONTACTS);
    private RecyclerView rv_contacts;

    private boolean phonePermGranted = false;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
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
        context = getContext();
        String[] permissions = {Manifest.permission.CALL_PHONE};
        phonePermGranted = Permissions.checkPermission(context, permissions[0]);
        if(!phonePermGranted){
            requestPermissions(permissions, REQUEST_PHONE_CALL_PERMISSION);
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        rv_contacts = (RecyclerView)view.findViewById(R.id.rv_contacts);
        // Instantiate layout manager and add it to the RecyclerView
        rv_contacts.setLayoutManager(new LinearLayoutManager(context));

        Query query = contactsRef.limitToLast(100);
        FirebaseRecyclerOptions<Agency> options = new FirebaseRecyclerOptions.Builder<Agency>()
                .setQuery(query, Agency.class)
                .build();
        Log.d(TAG, "onCreateView: before adapter is created");
        //Use FirebaseRecyclerAdapter
        adapter = new FirebaseRecyclerAdapter<Agency, AgencyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AgencyViewHolder holder, int position, @NonNull Agency model) {
                holder.bindViewHolder(model);

            }

            @Override
            public AgencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
                return new AgencyViewHolder(getContext(), getActivity(), view);
            }
        };
        rv_contacts.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        rv_contacts.addItemDecoration(dividerItemDecoration);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PHONE_CALL_PERMISSION:
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(context, "You cannot make calls without permission.",
                            Toast.LENGTH_SHORT).show();
                }else {
                    phonePermGranted = true;
                }
                break;
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
