package com.ngtiofack.go4lunch.controler.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.ngtiofack.go4lunch.R;
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
        swipeRefreshLayout.setOnRefreshListener(this::retrievingAllFirestoreUsers);
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
                .setOnItemClickListener((recyclerView, position, v) -> {
                    response = adapter.getGo4LunchUsersResult(position);
                    if (response.getYourLunch().getName() != null) {
                        Intent myIntent = new Intent(getActivity(), DetailedRestaurantActivity.class);
                        myIntent.putExtra(getString(R.string.name_restaurant), response.getYourLunch().getName());
                        myIntent.putExtra(getString(R.string.vicinity), response.getYourLunch().getVicinity());
                        String photoUrl;
                        if (response.getYourLunch().getPhotoUrlRef() == null) {
                            photoUrl = "";
                        } else {
                            photoUrl = response.getYourLunch().getPhotoUrlRef();
                            myIntent.putExtra(getString(R.string.photoHeight), response.getYourLunch().getPhotoHeight());
                            myIntent.putExtra(getString(R.string.photoWidth), response.getYourLunch().getPhotoWidth());
                        }
                        myIntent.putExtra(getString(R.string.photosReference), photoUrl);
                        myIntent.putExtra(getString(R.string.number_of_stars), response.getYourLunch().getRatingStars());
                        //myIntent.putExtra(getString(R.string.articleUrl), response.getWebUrl());
                        startActivity(myIntent);
                    } else {

                        Toast.makeText(getContext(), Objects.requireNonNull(getContext()).getString(R.string.no_restaurant_selected_yet), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void retrievingAllFirestoreUsers() {

        go4LunchUsersListFromFirebase = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("users").get()

                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Go4LunchUsers go4LunchUsers = document.toObject(Go4LunchUsers.class);
                            if (getContext() != null) {
                                if (!go4LunchUsers.getUid().equals(getUserId(getContext()))) {
                                    go4LunchUsersListFromFirebase.add(go4LunchUsers);
                                }
                            }
                        }
                        updateUI(go4LunchUsersListFromFirebase);
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


}