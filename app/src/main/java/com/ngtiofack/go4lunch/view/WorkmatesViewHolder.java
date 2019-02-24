package com.ngtiofack.go4lunch.view;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NG-TIOFACK on 2/16/2019.
 */

public class WorkmatesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.menu_and_restaurant_name)
    TextView user_and_menuRestaurant;
    @BindView(R.id.user_item_image)
    ImageView imageView;

    public WorkmatesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    public void updateWithUsers(Go4LunchUsers result, RequestManager glide) {

        String openHours;
        if (!result.getRestaurantSelected().isEmpty()) {
            openHours = " is eating";
        } else {
            openHours = " hasn't decided yet";
        }

        this.user_and_menuRestaurant.setText(result.getUsername() + openHours + "(" + result.getRestaurantSelected() + ")");
        glide.load(result.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
