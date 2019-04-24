package com.ngtiofack.go4lunch.controler.fragments;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.controler.activities.ChatsActivity;
import com.ngtiofack.go4lunch.controler.activities.DetailedRestaurantActivity;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;
import com.ngtiofack.go4lunch.utils.ItemClickSupport;
import com.ngtiofack.go4lunch.view.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ngtiofack.go4lunch.utils.mainUtils.getUserId;

public class WorkmatesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.list_view_recycler_view_workmates)
    RecyclerView recyclerView; // 1 - Declare RecyclerView
    // 1 - Declare the SwipeRefreshLayout
    @BindView(R.id.list_view__swipe_container_workmates)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<Go4LunchUsers> go4LunchUsersList, go4LunchUsersListFromFirebase;
    private WorkmatesAdapter adapter;
    private Go4LunchUsers response;


    public WorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        this.retrievingAllFirestoreUsers();
        this.configureSwipeRefreshLayout();
        return view;
    }

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrievingAllFirestoreUsers();
            }
        });
    }
    // -----------------
    // CONFIGURATION
    // -----------------
    // 3 - Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        // 3.1 - Reset list
        this.go4LunchUsersList = new ArrayList<>();
        // 3.2 - Create adapter passing the list of users
        this.adapter = new WorkmatesAdapter(this.go4LunchUsersList, Glide.with(this));
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
                        // 2 - Check if user is connected before launching MentorActivity
                        if (response.getIsConnected()){
                            startChatsActivity();
                        } else {
                           // showSnackBar(this.coordinatorLayout, getString(R.string.error_not_connected));
                        }
                       /* Intent myIntent = new Intent(getActivity(), DetailedRestaurantActivity.class);

                        //myIntent.putExtra(getString(R.string.articleUrl), response.getWebUrl());
                        startActivity(myIntent);*/
                    }
                });
    }

    private void retrievingAllFirestoreUsers() {

        go4LunchUsersListFromFirebase = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("users").get()

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Go4LunchUsers go4LunchUsers = document.toObject(Go4LunchUsers.class);
                                if(!go4LunchUsers.getUid().equals(getUserId(getContext()))){
                                        go4LunchUsersListFromFirebase.add(go4LunchUsers);
                                }
                            }
                            updateUI(go4LunchUsersListFromFirebase);
                        }
                    }
                });

    }

    private void updateUI(List<Go4LunchUsers> results) {
        // 3 - Stop refreshing and clear actual list of results
        swipeRefreshLayout.setRefreshing(false);
        go4LunchUsersList.clear();
        go4LunchUsersList.addAll(results);
        adapter.setGo4LunchUsersList(go4LunchUsersList);
    }


    // 1 - Starting Mentor Activity
    private void startChatsActivity(){
        Intent intent = new Intent(getContext(), ChatsActivity.class);
        intent.putExtra(getString(R.string.receiverId), response.getUid());
        intent.putExtra(getString(R.string.name_chat_person), response.getUsername());
        startActivity(intent);
    }

}
