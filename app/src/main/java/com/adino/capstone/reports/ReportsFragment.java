package com.adino.capstone.reports;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adino.capstone.MainActivity;
import com.adino.capstone.R;
import com.adino.capstone.model.Report;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


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
    private String mParam1;
    private String mParam2;
    private static final String TAG = "ReportsFragment";
    private RecyclerView rv_reports;

    /**
     * Firebase
     */
    private FirebaseRecyclerAdapter<Report, ReportViewHolder> adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private OnFragmentInteractionListener mListener;

    public ReportsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportsFragment newInstance(String param1, String param2) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setContentView(R.layout.fragment_reports);

        Log.d(TAG, "onCreate: In onCreate");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        rv_reports = (RecyclerView)view.findViewById(R.id.rv_report);
        // Instantiate layout manager and add it to the RecyclerView
        rv_reports.setLayoutManager(new LinearLayoutManager(getContext()));
        /*
         * Firebase
         */
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("reports");
        databaseReference.keepSynced(true);
        
        Query query = databaseReference.limitToLast(100);
        FirebaseRecyclerOptions<Report> options = new FirebaseRecyclerOptions.Builder<Report>()
                .setQuery(query, Report.class)
                .build();
        Log.d(TAG, "onCreateView: before adapter is created");
        //Use FirebaseRecyclerAdapter
        adapter = new FirebaseRecyclerAdapter<Report, ReportViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ReportViewHolder holder, int position, Report model) {
                holder.bindViewHolder(model);
                Log.d(TAG, "onBindViewHolder: Report: " + model.getCaption());
            }

            @Override
            public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
                return new ReportViewHolder(view, getContext());
            }
        };
        rv_reports.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        rv_reports.addItemDecoration(dividerItemDecoration);

        return view;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
