package com.ngtiofack.go4lunch.controller.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ngtiofack.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedRestaurantActivity extends AppCompatActivity {

    @BindView(R.id.nameOfRestaurantDetail)
    TextView nameOfRestaurant;
    @BindView(R.id.addressOfRestaurantDetail)
    TextView adresseOfRestaurant;
    @BindView(R.id.phoneNumberRestaurant)
    RelativeLayout phoneNum;
    @BindView(R.id.websiteOfTheRestaurant)
    RelativeLayout website;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_restaurant);
        ButterKnife.bind(this);

        //String url = getIntent().getStringExtra(getString(R.string.articleUrl));
    }
}
