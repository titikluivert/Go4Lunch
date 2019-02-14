package com.ngtiofack.go4lunch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
public class NewAdapter extends RecyclerView.Adapter<NewViewHolder> {

// FOR DATA


    private List<RestaurantsModel.Result> myDocsList;
    // 1 - Declaring a Glide object
    private RequestManager glide;

    // 2 - Updating our constructor adding a Glide Object
    public NewAdapter(List<RestaurantsModel.Result> mostBusinessDocsList, RequestManager glide) {
        this.myDocsList = mostBusinessDocsList;
        this.glide = glide;
    }

    public void setDocList(List<RestaurantsModel.Result> list) {
        this.myDocsList = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_item_list_view, parent, false);

        return new NewViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A TOP STORIES
    @Override
    public void onBindViewHolder(@NonNull NewViewHolder viewHolder, int position) {
        // - 3 Passing the Glide object to each ViewHolder
        viewHolder.updateWithNews(this.myDocsList.get(position), this.glide);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {

        return this.myDocsList.size();
    }

    public RestaurantsModel.Result getBusinessDoc(int position) {
        return this.myDocsList.get(position);
    }
}
