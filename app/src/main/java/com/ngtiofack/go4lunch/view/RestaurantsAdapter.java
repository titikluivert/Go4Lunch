package com.ngtiofack.go4lunch.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.RestaurantsModel;

import java.util.List;

/**
 * Created by NG-TIOFACK on 12/19/2018.
 */
public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsViewHolder> {

// FOR DATA
    private List<RestaurantsModel.Result> myRestaurantsList;
    // 1 - Declaring a Glide object
    private RequestManager glide;

   // private List<Go4LunchUsers> go4LunchUsers;

    // 2 - Updating our constructor adding a Glide Object
    public RestaurantsAdapter(List<RestaurantsModel.Result> mostBusinessDocsList, RequestManager glide) {
        this.myRestaurantsList = mostBusinessDocsList;
        this.glide = glide;
      //  this.go4LunchUsers = go4LunchUsers;
    }

    public void setRestaurantList(List<RestaurantsModel.Result> list) {
        this.myRestaurantsList = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_item_list_view, parent, false);

        return new RestaurantsViewHolder(view);
    }
    // UPDATE VIEW HOLDER WITH A TOP STORIES
    @Override
    public void onBindViewHolder(@NonNull RestaurantsViewHolder viewHolder, int position) {
        // - 3 Passing the Glide object to each ViewHolder
        viewHolder.updateWithRestaurants(this.myRestaurantsList.get(position), this.glide);
    }
    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {

        return this.myRestaurantsList.size();
    }

    public RestaurantsModel.Result getRestaurantsResults(int position) {
        return this.myRestaurantsList.get(position);
    }
}
