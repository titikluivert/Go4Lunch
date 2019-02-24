package com.ngtiofack.go4lunch.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.controller.activities.DetailedRestaurantActivity;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;
import com.ngtiofack.go4lunch.utils.ItemClickSupport;
import com.ngtiofack.go4lunch.view.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmatesFragment extends Fragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.list_view_recycler_view_workmates)
    RecyclerView recyclerView; // 1 - Declare RecyclerView
    // 1 - Declare the SwipeRefreshLayout
    @BindView(R.id.list_view__swipe_container_workmates)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<Go4LunchUsers> myResultsList;
    private WorkmatesAdapter adapter;
    private Go4LunchUsers response;


    public WorkmatesFragment() {
        // Required empty public constructor
    }

    public static WorkmatesFragment newInstance(String param1, String param2) {
        WorkmatesFragment fragment = new WorkmatesFragment();
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
            //mParam1 = getArguments().getString(ARG_PARAM1);
            // mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView(); // - 3 Call during UI creation
        // 4 - Configure the SwipeRefreshLayout
        this.configureOnClickRecyclerView();

        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
            //  mParam2 = getArguments().getString(ARG_PARAM2);
            this.configureSwipeRefreshLayout();
            //  this.executeHttpRequestWithRetrofitNews(mParam1, mParam2);
        }
        return view;
    }

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //  updateUI(mParam1, mParam2);
            }
        });
    }

    // -----------------
    // CONFIGURATION
    // -----------------
    // 3 - Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.1 - Reset list
        this.myResultsList = new ArrayList<>();
        // 3.2 - Create adapter passing the list of users
        this.adapter = new WorkmatesAdapter(this.myResultsList, Glide.with(this));
        // 3.3 - Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.adapter);
        // 3.4 - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // 1 - Configure item click on RecyclerView
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_item_list_workmates)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        response = adapter.getGo4LunchUsersResult(position);
                        //TODO
                        Intent myIntent = new Intent(getActivity(), DetailedRestaurantActivity.class);
                        //myIntent.putExtra(getString(R.string.articleUrl), response.getWebUrl());
                        startActivity(myIntent);
                    }
                });
    }


    private void updateUI(List<Go4LunchUsers> results) {
        // 3 - Stop refreshing and clear actual list of results
        swipeRefreshLayout.setRefreshing(false);
        myResultsList.clear();
        myResultsList.addAll(results);
        adapter.setGo4LunchUsersList(myResultsList);
    }


}
