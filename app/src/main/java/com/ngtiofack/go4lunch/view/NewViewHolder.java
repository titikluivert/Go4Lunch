package com.ngtiofack.go4lunch.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ngtiofack.go4lunch.R;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.ngtiofack.go4lunch.model.RestaurantsModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by NG-TIOFACK on 12/19/2018.
 */
public class NewViewHolder extends RecyclerView.ViewHolder {
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

    public NewViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithNews(RestaurantsModel.Result result, RequestManager glide) {

        this.Title.setText(result.getName());
        this.distanceOfRestaurant.setText(result.getGeometry().getLocation().getLat().toString());
        this.addressRestaurant.setText(result.getVicinity());
        this.closeTime.setText(result.getOpeningHours().getOpenNow().toString());


        if (result.getPhotos().size() == 0) {
            //glide.load(LogoIfMissing).apply(RequestOptions.circleCropTransform()).into(imageView);
        } else {
            glide.load(result.getPhotos().get(0).getHtmlAttributions()).apply(RequestOptions.circleCropTransform()).into(imageView);
        }
    }
}
