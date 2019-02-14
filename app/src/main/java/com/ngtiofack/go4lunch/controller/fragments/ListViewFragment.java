package com.ngtiofack.go4lunch.controller.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.controller.activities.DetailedRestaurant;
import com.ngtiofack.go4lunch.model.RestaurantsModel;
import com.ngtiofack.go4lunch.utils.ItemClickSupport;
import com.ngtiofack.go4lunch.utils.RestaurantsServiceStreams;
import com.ngtiofack.go4lunch.view.RestaurantsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.observers.DisposableObserver;

import static com.ngtiofack.go4lunch.utils.Utils.PROXIMITY_RADIUS;
import static com.ngtiofack.go4lunch.utils.Utils.TYPE;

public class ListViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.list_view_recycler_view)
    RecyclerView recyclerView; // 1 - Declare RecyclerView
    // 1 - Declare the SwipeRefreshLayout
    @BindView(R.id.list_view__swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private String mParam1;
    private String mParam2;
    // 2 - Declare list of results (MostPopular) & Adapter
    private List<RestaurantsModel.Result> myResultsList;
    private RestaurantsAdapter adapter;
    private RestaurantsModel.Result response;

    public ListViewFragment() {
    }

    public static ListViewFragment newInstance(String param1, String param2) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView(); // - 3 Call during UI creation
        // 4 - Configure the SwipeRefreshLayout
        this.configureOnClickRecyclerView();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            this.configureSwipeRefreshLayout();
            this.executeHttpRequestWithRetrofitNews(mParam1, mParam2);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 2 - Configure the SwipeRefreshLayout
    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestWithRetrofitNews(mParam1, mParam2);
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
        this.adapter = new RestaurantsAdapter(this.myResultsList, Glide.with(this));
        // 3.3 - Attach the adapter to the recyclerview to populate items
        this.recyclerView.setAdapter(this.adapter);
        // 3.4 - Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    // 1 - Configure item click on RecyclerView
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.fragment_item_list_view)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        response = adapter.getRestaurantsResults(position);
                        //TODO
                        Intent myIntent = new Intent(getActivity(), DetailedRestaurant.class);
                        //myIntent.putExtra(getString(R.string.articleUrl), response.getWebUrl());
                        startActivity(myIntent);
                    }
                });
    }

    private void executeHttpRequestWithRetrofitNews(String latitude, String longitude) {

        DisposableObserver<RestaurantsModel> disposable = RestaurantsServiceStreams.streamFetchRestaurantsItems(TYPE, latitude + "," + longitude, PROXIMITY_RADIUS).subscribeWith(new DisposableObserver<RestaurantsModel>() {

            @Override
            public void onNext(RestaurantsModel results) {
                updateUI(results.getResults());

            }

            @Override
            public void onError(Throwable e) {
                Log.e("", "There is an error" + e);
            }

            @Override
            public void onComplete() {
                Log.e("", "on complete is running");
            }
        });

    }

    private void updateUI(List<RestaurantsModel.Result> results) {
        // 3 - Stop refreshing and clear actual list of results
        swipeRefreshLayout.setRefreshing(false);
        myResultsList.clear();
        myResultsList.addAll(results);
        adapter.setRestaurantList(myResultsList);
    }

}

