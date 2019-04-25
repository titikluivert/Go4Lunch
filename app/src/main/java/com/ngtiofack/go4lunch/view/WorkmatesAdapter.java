package com.ngtiofack.go4lunch.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;

import java.util.List;

/**
 * Created by NG-TIOFACK on 2/16/2019.
 */
public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesViewHolder> {


    private List<Go4LunchUsers> mGo4LunchUsersList;
    // 1 - Declaring a Glide object
    private RequestManager glide;

    // 2 - Updating our constructor adding a Glide Object
    public WorkmatesAdapter(List<Go4LunchUsers> go4LunchUsers, RequestManager glide) {
        this.mGo4LunchUsersList = go4LunchUsers;
        this.glide = glide;
    }

    public void setGo4LunchUsersList(List<Go4LunchUsers> list) {
        this.mGo4LunchUsersList = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_item_list_workmates, parent, false);
        return new WorkmatesViewHolder(view);
    }

    // UPDATE VIEW HOLDER WITH A TOP STORIES
    @Override
    public void onBindViewHolder(@NonNull WorkmatesViewHolder viewHolder, int position) {
        // - 3 Passing the Glide object to each ViewHolder
        viewHolder.updateWithUsers(this.mGo4LunchUsersList.get(position), this.glide);
    }

    // RETURN THE TOTAL COUNT OF ITEMS IN THE LIST
    @Override
    public int getItemCount() {

        return this.mGo4LunchUsersList.size();
    }

    public Go4LunchUsers getGo4LunchUsersResult(int position) {
        return this.mGo4LunchUsersList.get(position);
    }

}
