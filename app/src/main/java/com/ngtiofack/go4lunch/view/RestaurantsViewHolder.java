package com.ngtiofack.go4lunch.view;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.RestaurantsModel;
import com.ngtiofack.go4lunch.utils.CurrentLocation;
import com.ngtiofack.go4lunch.utils.SaveCurrentLocation;
import com.ngtiofack.go4lunch.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ngtiofack.go4lunch.utils.Utils.getNumOfStars;

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

    @BindView(R.id.numOfInterestedImg)
    ImageView imgOfInterested;

    @BindView(R.id.numOfInterested)
    TextView numOfInterested;

    @BindView(R.id.closeTimeOfRestaurant)
    TextView closeTime;

    @BindView(R.id.numOfStars_0)
    ImageView imgStars0;

    @BindView(R.id.numOfStars_1)
    ImageView imgStars1;

    @BindView(R.id.numOfStars_2)
    ImageView imgStars2;


    private SaveCurrentLocation LatLng;

    public RestaurantsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        CurrentLocation currentLocation = new CurrentLocation();
        LatLng = currentLocation.getSaveLatLng(itemView.getContext());
    }

    @SuppressLint("SetTextI18n")
    public void updateWithRestaurants(RestaurantsModel.Result result, RequestManager glide) {

        imgOfInterested.setVisibility(View.GONE);
        numOfInterested.setVisibility(View.GONE);
        imgStars0.setVisibility(View.GONE);
        imgStars1.setVisibility(View.GONE);
        imgStars2.setVisibility(View.GONE);

        firebaseUserSearch(result);

        if (!(result.getRating() == null)) {

            switch (getNumOfStars(result.getRating())) {
                case 0:
                    break;
                case 1:
                    imgStars2.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    imgStars1.setVisibility(View.VISIBLE);
                    imgStars2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    imgStars0.setVisibility(View.VISIBLE);
                    imgStars1.setVisibility(View.VISIBLE);
                    imgStars2.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;

            }
        }

        this.Title.setText(result.getName());
        this.distanceOfRestaurant.setText(String.valueOf(Utils.getDistanceToRestaurants(
                Double.parseDouble(result.getGeometry().getLocation().getLat().toString().replaceAll(" ", ".")),
                Double.parseDouble(result.getGeometry().getLocation().getLng().toString().replaceAll(" ", ".")),
                (double) LatLng.getLatitude(),
                (double) LatLng.getLongitude()
        )) + "m");

        this.addressRestaurant.setText(result.getVicinity());
        String openHours = "Not available";
        if (result.getOpeningHours() != null) {
            if (result.getOpeningHours().getOpenNow()) {
                openHours = "is Open";
            } else {
                openHours = "is closed";
            }
        }
        this.closeTime.setText(openHours);

        if (result.getPhotos() != null) {
            String photoUrl = Utils.getPhotoUrl(this.closeTime.getContext(), result.getPhotos().get(0).getPhotoReference(), result.getPhotos().get(0).getHeight(), result.getPhotos().get(0).getWidth());
            glide.load(photoUrl).apply(RequestOptions.circleCropTransform()).into(imageView);

        } else {
            glide.load(R.drawable.restaurant_default_img).apply(RequestOptions.circleCropTransform()).into(imageView);
        }
    }


    private void firebaseUserSearch(final RestaurantsModel.Result result) {

        final int[] users = {0};
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("restaurantSelected");
        //You must remember to remove the listener when you finish using it, also to keep track of changes you can use the ChildChange
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {

                    if (result.getName().equals(key.getKey())) {
                        imgOfInterested.setVisibility(View.VISIBLE);
                        numOfInterested.setVisibility(View.VISIBLE);

                        Iterable<DataSnapshot> keys2 = key.getChildren();
                        for (DataSnapshot key0 : keys2) {
                            users[0] = users[0] + 1;
                        }
                        numOfInterested.setText("(" + users[0] + ")");
                        if (users[0] == 0) {
                            imgOfInterested.setVisibility(View.GONE);
                            numOfInterested.setVisibility(View.GONE);
                        }
                    }
                    users[0] = 0;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
