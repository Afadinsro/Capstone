package com.adino.capstone.trending;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adino.capstone.R;
import com.adino.capstone.model.Trending;
import com.adino.capstone.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.adino.capstone.util.Constants.GRID_COLUMN_COUNT;
import static com.adino.capstone.util.Constants.TRENDING;
import static com.adino.capstone.util.Constants.USER_ID;
import static com.adino.capstone.util.Constants.USER_SUBSCRIPTIONS;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrendingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrendingFragment extends Fragment {
    private static final String TAG = "TrendingFragment";

    private RecyclerView rvTrending;
    /**
     * Firebase
     */
    private FirebaseRecyclerAdapter<Trending, TrendingViewHolder> adapter;
    private DatabaseReference databaseReference;

    private OnFragmentInteractionListener mListener;

    public TrendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrendingFragment.
     */
    public static TrendingFragment newInstance() {
        TrendingFragment fragment = new TrendingFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: View about to be created");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        rvTrending = (RecyclerView)view.findViewById(R.id.rv_trending);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(GRID_COLUMN_COUNT, LinearLayoutManager.VERTICAL);
        rvTrending.setLayoutManager(layoutManager);
        /*
         * Firebase
         */
        databaseReference = FirebaseDatabase.getInstance().getReference().child(TRENDING);
        databaseReference.keepSynced(true);

        Query query = databaseReference.limitToFirst(10);
        FirebaseRecyclerOptions<Trending> options = new FirebaseRecyclerOptions.Builder<Trending>()
                .setQuery(query, Trending.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Trending, TrendingViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TrendingViewHolder holder, int position, @NonNull Trending model) {
                Log.d(TAG, "onBindViewHolder: Model: " + model.getTitle());
                holder.bindViewHolder(model);
            }

            @Override
            public TrendingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: ViewHolder created");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trending, parent, false);
                return new TrendingViewHolder(getContext(), view, getFragmentManager());
            }
        };
        Log.d(TAG, "onCreateView: Adapter created");
        rvTrending.setAdapter(adapter);

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
