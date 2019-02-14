package com.ngtiofack.go4lunch.view;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.RestaurantsModel;
import com.ngtiofack.go4lunch.utils.CurrentLocation;
import com.ngtiofack.go4lunch.utils.SaveCurrentLocation;
import com.ngtiofack.go4lunch.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NG-TIOFACK on 12/19/2018.
 */
public class RestaurantsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.nameOfRestaurant)
    TextView Title;
    @BindView(R.id.distanceOfRestaurant)
    TextView distanceOfRestaurant;
    @BindView(R.id.fragment_item_image)
    ImageView imageView;
    @BindView(R.id.addressOfRestaurant)
    TextView addressRestaurant;
    @BindView(R.id.numOfInterested)
    TextView numOfInterested;
    @BindView(R.id.closeTimeOfRestaurant)
    TextView closeTime;

    CurrentLocation currentLocation = new CurrentLocation();
    SaveCurrentLocation LatLng;

    public RestaurantsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        LatLng = currentLocation.getSaveLatLng(itemView.getContext());
    }

    @SuppressLint("SetTextI18n")
    public void updateWithRestaurants(RestaurantsModel.Result result, RequestManager glide) {


        this.Title.setText(result.getName());
        this.distanceOfRestaurant.setText(String.valueOf(Utils.getDistanceToRestaurants(
                Double.parseDouble(result.getGeometry().getLocation().getLat().toString().replaceAll(" ", ".")),
                Double.parseDouble(result.getGeometry().getLocation().getLng().toString().replaceAll(" ", ".")),
                (double) LatLng.getLatitude(),
                (double) LatLng.getLongitude()
        )) + "m");
        this.addressRestaurant.setText(result.getVicinity());
        String openHours;
        if (result.getOpeningHours().getOpenNow()) {
            openHours = "is Open";
        } else {
            openHours = "is closed";
        }
        this.closeTime.setText(openHours);

        glide.load(R.drawable.restaurant_default_img).apply(RequestOptions.circleCropTransform()).into(imageView);
       /* if (result.getPhotos().size() == 0) {
            glide.load(R.drawable.restaurant_default_img).apply(RequestOptions.circleCropTransform()).into(imageView);
        } else {
            String[] photo = result.getPhotos().get(0).getHtmlAttributions().get(0).split("\"");
            glide.load(photo[1]).apply(RequestOptions.circleCropTransform()).into(imageView);
        }*/
    }
}
