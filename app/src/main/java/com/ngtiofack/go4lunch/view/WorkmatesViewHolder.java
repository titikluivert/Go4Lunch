package com.ngtiofack.go4lunch.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.controler.activities.ChatsActivity;
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

    @BindView(R.id.BtnChat)
    ImageButton chatButton;

    public WorkmatesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @SuppressLint("SetTextI18n")
    public void updateWithUsers(Go4LunchUsers result, RequestManager glide) {
        boolean restaurantChoosed = false;


        String uid = result.getUid();
        String name = result.getUsername();

        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatsActivity.class);
            intent.putExtra(v.getContext().getString(R.string.receiverId), uid);
            intent.putExtra(v.getContext().getString(R.string.name_chat_person), name);
            v.getContext().startActivity(intent);
        });

        String openHours = " hasn't decided yet";
        ;

        if (result.getYourLunch() != null) {
            if (result.getYourLunch().getName() != null) {
                if (!result.getYourLunch().getName().isEmpty()) {
                    openHours = " is eating";
                    restaurantChoosed = true;
                }
            }
        }
        if (!restaurantChoosed) {
            this.user_and_menuRestaurant.setText(result.getUsername() + " " + openHours);
            this.user_and_menuRestaurant.setTextColor(ContextCompat.getColor(this.imageView.getContext(), R.color.colorGrey));
            this.user_and_menuRestaurant.setTypeface(null, Typeface.ITALIC);
        } else {

            this.user_and_menuRestaurant.setText(result.getUsername() + " " + openHours + " ( " + result.getYourLunch().getName() + " )");
        }
        glide.load(result.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(imageView);

    }
}
