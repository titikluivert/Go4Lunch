package com.ngtiofack.go4lunch.controler.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.api.Go4LunchUserHelper;
import com.ngtiofack.go4lunch.api.RestaurantHelper;
import com.ngtiofack.go4lunch.model.Go4LunchUsers;
import com.ngtiofack.go4lunch.model.RestaurantSelected;
import com.ngtiofack.go4lunch.model.YourLunch;
import com.ngtiofack.go4lunch.utils.RestaurantsUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ngtiofack.go4lunch.api.Go4LunchUserHelper.getUser;
import static com.ngtiofack.go4lunch.utils.RestaurantsUtils.saveYourLunch;
import static com.ngtiofack.go4lunch.utils.mainUtils.LIKE;
import static com.ngtiofack.go4lunch.utils.mainUtils.RESTAURANT_IS_NOT_SELECTED;
import static com.ngtiofack.go4lunch.utils.mainUtils.RESTAURANT_LIKE;

public class DetailedRestaurantActivity extends BaseActivity {

    @BindView(R.id.websiteOfTheRestaurant)
    Button website;
    @BindView(R.id.phoneNumberRestaurant)
    Button phoneNum;
    @BindView(R.id.likeButtonRestaurant)
    Button likeRestaurant;

    RequestManager glide;
    int numOfStars;
    static boolean fabIsClicked = true;
    static boolean dataChanged = true;
    static String uid;
    String nameRestaurant;
    String restaurantSelectedStoreOnFirebase = " ";
    String vicinity;
    String photoReferenceUrl;
    int photoHeight;
    int photoWidth;

    @BindView(R.id.relativeLayout)
    RelativeLayout relativLyt;

    @BindView(R.id.list_workmates_recycler_view)
    RecyclerView mResultList;

    @BindView(R.id.fabDetailRestaurant)
    FloatingActionButton fab;

    @BindView(R.id.nameOfRestaurantDetail)
    TextView nameOfRestaurant;

    @BindView(R.id.addressOfRestaurantDetail)
    TextView addressOfRestaurant;

    @BindView(R.id.ImgRestaurantDetails)
    ImageView imgRestaurantDetails;

    @BindView(R.id.numOfStarsDetails_0)
    ImageView stars0;

    @BindView(R.id.numOfStarsDetails_1)
    ImageView stars1;

    @BindView(R.id.numOfStarsDetails_2)
    ImageView stars2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_restaurant);
        ButterKnife.bind(this);

        glide = Glide.with(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        vicinity = getIntent().getStringExtra(getString(R.string.vicinity));
        nameRestaurant = getIntent().getStringExtra(getString(R.string.name_restaurant));
        photoReferenceUrl = getIntent().getStringExtra(getString(R.string.photosReference));
        numOfStars = getIntent().getIntExtra(getString(R.string.number_of_stars), 0);

        this.starsOfRestaurantsShow();
        fabIsClicked = true;
        nameOfRestaurant.setText(nameRestaurant);
        addressOfRestaurant.setText(vicinity);
        this.handelPhotoRef();
        uid = this.getCurrentUser().getUid();
        this.checkIfRestaurantsIsSelected();
        this.handelFloatingButton();
        this.firebaseUserSearch(nameRestaurant);
    }

    @OnClick(R.id.websiteOfTheRestaurant)
    public void onClickWebSite() {
        Snackbar.make(relativLyt, DetailedRestaurantActivity.this.getString(R.string.no_website_available), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @OnClick(R.id.phoneNumberRestaurant)
    public void onClickCall() {
        Snackbar.make(relativLyt, DetailedRestaurantActivity.this.getString(R.string.phone_number_not_available), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @OnClick(R.id.likeButtonRestaurant)
    public void onClickLikeRestaurant() {
        YourLunch likeRestaurant = new YourLunch(1);
        FirebaseFirestore.getInstance().collection(LIKE).document(RESTAURANT_LIKE).collection(nameRestaurant).document(uid).set(likeRestaurant);
    }

    private void firebaseUserSearch(String RestaurantName) {

        Query firebaseSearchQuery = RestaurantHelper.getRestaurantCollection().child(RestaurantName).orderByChild("userName");
        FirebaseRecyclerAdapter<RestaurantSelected, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RestaurantSelected, UsersViewHolder>(

                RestaurantSelected.class,
                R.layout.item_list_workmates,
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

        private void setDetails(Context ctx, String userName, String userImage) {

            TextView user_name = mView.findViewById(R.id.menu_and_restaurant_name);
            ImageView user_image = mView.findViewById(R.id.user_item_image);

            user_name.setText(String.format("%s%s", userName, ctx.getString(R.string.is_joining)));
            Glide.with(ctx).load(userImage).apply(RequestOptions.circleCropTransform()).into(user_image);

        }
    }

    private void writeNewUser(String name, String restaurantName, String urlPicture) {
        RestaurantSelected user = new RestaurantSelected(name, urlPicture);
        RestaurantHelper.getRestaurantCollection().child(restaurantName).child(uid).setValue(user);
    }

    private void deleteNewUser() {

        RestaurantHelper.getRestaurantCollection().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {
                    Iterable<DataSnapshot> keys2 = key.getChildren();
                    for (DataSnapshot key0 : keys2) {
                        if (!nameRestaurant.equals(key.getKey()) || dataChanged) {
                            if (Objects.requireNonNull(key0.getKey()).equals(uid))
                                RestaurantHelper.getRestaurantCollection().child(Objects.requireNonNull(key.getKey())).child(uid).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }


        });

    }

    private void updateRestaurantSelectedParams(YourLunch mYourLunch) {

        String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
        String username = this.getCurrentUser().getDisplayName();
        String uid = this.getCurrentUser().getUid();
        boolean isConnected = this.isCurrentUserLogged();

        Go4LunchUserHelper.createUser(uid, username, urlPicture, isConnected, mYourLunch).addOnFailureListener(this.onFailureListener());
    }

    private void starsOfRestaurantsShow() {
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
    }

    private void handelFloatingButton() {
        fab.setOnClickListener(view -> {

            if (fabIsClicked) {
                fab.setImageResource(R.drawable.check_restaurant);
                // createUser()
                updateRestaurantSelectedParams(new YourLunch(nameRestaurant, vicinity, photoHeight, photoWidth, photoReferenceUrl, numOfStars));
                dataChanged = false;
                deleteNewUser();
                writeNewUser(getCurrentUser().getDisplayName(), nameRestaurant, Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString());
                saveYourLunch(getApplicationContext(), nameRestaurant, vicinity, photoHeight, photoWidth, photoReferenceUrl, numOfStars);
                fabIsClicked = false;

            } else {
                fabIsClicked = true;
                dataChanged = true;
                updateRestaurantSelectedParams(new YourLunch());
                saveYourLunch(getApplicationContext(), RESTAURANT_IS_NOT_SELECTED, vicinity, photoHeight, photoWidth, photoReferenceUrl, numOfStars);
                fab.setImageResource(R.drawable.please_select_restaurant);
                deleteNewUser();
            }
        });
    }

    private void handelPhotoRef() {
        if (!photoReferenceUrl.isEmpty()) {
            photoHeight = getIntent().getIntExtra(getString(R.string.photoHeight), 1000);
            photoWidth = getIntent().getIntExtra(getString(R.string.photoWidth), 1000);

            glide.load(RestaurantsUtils.getPhotoUrl(photoReferenceUrl, photoHeight, photoWidth)).apply(RequestOptions.centerInsideTransform()).into(imgRestaurantDetails);
        } else {

            glide.load(R.drawable.restaurant_default_img).apply(RequestOptions.centerInsideTransform()).into(imgRestaurantDetails);
        }
    }

    private void checkIfRestaurantsIsSelected() {
        getUser(uid)
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot != null) {
                        Go4LunchUsers go4LunchUsers = documentSnapshot.toObject(Go4LunchUsers.class);
                        restaurantSelectedStoreOnFirebase = Objects.requireNonNull(go4LunchUsers).getYourLunch().getName();

                        if (restaurantSelectedStoreOnFirebase.equals(nameRestaurant)) {
                            fab.setImageResource(R.drawable.check_restaurant);
                            writeNewUser(getCurrentUser().getDisplayName(), nameRestaurant, Objects.requireNonNull(getCurrentUser().getPhotoUrl()).toString());
                            fabIsClicked = false;
                        }
                    }

                });
    }
}
