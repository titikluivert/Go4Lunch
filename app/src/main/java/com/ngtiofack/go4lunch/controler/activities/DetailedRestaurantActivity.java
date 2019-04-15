package com.ngtiofack.go4lunch.controler.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.RestaurantSelected;
import com.ngtiofack.go4lunch.utils.Utils;

import java.util.Objects;

import static com.ngtiofack.go4lunch.utils.Go4LunchUserHelper.getUser;
import static com.ngtiofack.go4lunch.utils.Go4LunchUserHelper.updateRestaurantSelected;
import static com.ngtiofack.go4lunch.utils.Utils.RESTAURANTISNOTSELECTED;
import static com.ngtiofack.go4lunch.utils.Utils.saveYourLunch;

public class DetailedRestaurantActivity extends BaseActivity {

    Button website, phoneNum, likeRestaurant;
    RequestManager glide;

    int numOfStars;

    static boolean fabIsClicked = true;
    static boolean dataChanged = true;
    static String uid;
    String nameRestaurant;
    String restaurantSelectedStoreOnFirebase;
    String vicinity;
    String photoReferenceUrl;
    int photoHeight;
    int photoWidth;
    private RecyclerView mResultList;
    private DatabaseReference mUserDatabase, mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_restaurant);
        glide = Glide.with(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }*/
        mUserDatabase = FirebaseDatabase.getInstance().getReference("restaurantSelected");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mResultList = findViewById(R.id.list_workmates_recycler_view);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        final FloatingActionButton fab = findViewById(R.id.fabDetailRestaurant);
        TextView nameOfRestaurant = findViewById(R.id.nameOfRestaurantDetail);
        TextView addressOfRestaurant = findViewById(R.id.addressOfRestaurantDetail);
        ImageView imgRestaurantDetails = findViewById(R.id.ImgRestaurantDetails);

        website = findViewById(R.id.websiteOfTheRestaurant);
        phoneNum = findViewById(R.id.phoneNumberRestaurant);
        likeRestaurant = findViewById(R.id.likeButtonRestaurant);


        ImageView stars0 = findViewById(R.id.numOfStarsDetails_0);
        ImageView stars1 = findViewById(R.id.numOfStarsDetails_1);
        ImageView stars2 = findViewById(R.id.numOfStarsDetails_2);


        vicinity = getIntent().getStringExtra(getString(R.string.vicinity));
        nameRestaurant = getIntent().getStringExtra(getString(R.string.name_restaurant));
        photoReferenceUrl = getIntent().getStringExtra(getString(R.string.photosReference));
        numOfStars = getIntent().getIntExtra(getString(R.string.number_of_stars), 0);

        stars0.setVisibility(View.GONE);
        stars1.setVisibility(View.GONE);
        stars2.setVisibility(View.GONE);

        switch (numOfStars) {
            case 0:
                break;
            case 1:
                stars0.setVisibility(View.VISIBLE);
                break;
            case 2:
                stars0.setVisibility(View.VISIBLE);
                stars1.setVisibility(View.VISIBLE);
                break;
            case 3:
                stars0.setVisibility(View.VISIBLE);
                stars1.setVisibility(View.VISIBLE);
                stars2.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
        fabIsClicked = true;
        nameOfRestaurant.setText(nameRestaurant);
        addressOfRestaurant.setText(vicinity);

        if (!photoReferenceUrl.isEmpty()) {
            photoHeight = getIntent().getIntExtra(getString(R.string.photoHeight), 1000);
            photoWidth = getIntent().getIntExtra(getString(R.string.photoWidth), 1000);

            glide.load(Utils.getPhotoUrl(this, photoReferenceUrl, photoHeight, photoWidth)).apply(RequestOptions.centerInsideTransform()).into(imgRestaurantDetails);
        } else {

            glide.load(R.drawable.restaurant_default_img).apply(RequestOptions.centerInsideTransform()).into(imgRestaurantDetails);
        }

        uid = this.getCurrentUser().getUid();

        getUser(uid)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        restaurantSelectedStoreOnFirebase = documentSnapshot.getString("restaurantSelected");
                        assert restaurantSelectedStoreOnFirebase != null;
                        if (restaurantSelectedStoreOnFirebase.equals(nameRestaurant)) {
                            fab.setImageResource(R.drawable.check_restaurant);
                            writeNewUser(getCurrentUser().getDisplayName(), nameRestaurant, Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString());
                            fabIsClicked = false;
                        }
                    }
                });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fabIsClicked) {
                    fab.setImageResource(R.drawable.check_restaurant);
                    updateRestaurantSelected(uid, nameRestaurant);
                    dataChanged = false;
                    deleteNewUser();
                    writeNewUser(getCurrentUser().getDisplayName(), nameRestaurant, Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString());
                    saveYourLunch(getApplicationContext(), nameRestaurant, photoReferenceUrl, photoHeight, photoWidth, vicinity, numOfStars);
                    fabIsClicked = false;

                } else {
                    fabIsClicked = true;
                    dataChanged = true;
                    updateRestaurantSelected(uid, "");
                    saveYourLunch(getApplicationContext(), RESTAURANTISNOTSELECTED, photoReferenceUrl, photoHeight, photoWidth, vicinity, numOfStars);
                    fab.setImageResource(R.drawable.please_select_restaurant);
                    deleteNewUser();
                }
            }
        });

        website.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar.make(v, "No website available", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        phoneNum.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar.make(v, "No phone number available", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        likeRestaurant.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
            }
        });

        this.firebaseUserSearch(nameRestaurant);
    }

    private void firebaseUserSearch(String RestaurantName) {

        Query firebaseSearchQuery = mUserDatabase.child(RestaurantName).orderByChild("userName");
        FirebaseRecyclerAdapter<RestaurantSelected, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RestaurantSelected, UsersViewHolder>(

                RestaurantSelected.class,
                R.layout.fragment_item_list_workmates,
                UsersViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, RestaurantSelected model, int position) {

                viewHolder.setDetails(getApplicationContext(), model.getUserName(), model.getUrlPicture());
            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetails(Context ctx, String userName, String userImage) {

            TextView user_name = mView.findViewById(R.id.menu_and_restaurant_name);
            ImageView user_image = mView.findViewById(R.id.user_item_image);

            user_name.setText(userName + " is joining!");
            Glide.with(ctx).load(userImage).apply(RequestOptions.circleCropTransform()).into(user_image);

        }
    }

    private void writeNewUser(String name, String restaurantName, String urlPicture) {
        RestaurantSelected user = new RestaurantSelected(name, urlPicture);
        mDatabase.child("restaurantSelected").child(restaurantName).child(uid).setValue(user);
    }

    private void deleteNewUser() {

        mDatabase.child("restaurantSelected").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {
                    Iterable<DataSnapshot> keys2 = key.getChildren();
                    for (DataSnapshot key0 : keys2) {
                        // RestaurantSelected restaurantSelected = key0.getValue(RestaurantSelected.class);
                        if (!nameRestaurant.equals(key.getKey()) || dataChanged) {
                            if (key0.getKey().equals(uid))
                                mDatabase.child("restaurantSelected").child(key.getKey()).child(uid).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });

    }


}
