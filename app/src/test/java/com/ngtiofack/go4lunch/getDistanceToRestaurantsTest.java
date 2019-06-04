package com.ngtiofack.go4lunch;


import com.ngtiofack.go4lunch.utils.mainUtils;

import org.junit.Assert;
import org.junit.Test;


public class getDistanceToRestaurantsTest {

    @Test
    public void distanceToRestaurantsTEST() {
        //int getDistanceToRestaurants(double latA, double longA, double latB, double longB)
        //double temp = (Math.abs(Math.sqrt((longA - longB) * (longA - longB) + (latA - latB) * (latA - latB)) * a)) * 1000;
        Assert.assertEquals(59861, mainUtils.getDistanceToRestaurants(8.2, 2.5, 8, 3));
        Assert.assertEquals(70303, mainUtils.getDistanceToRestaurants(4.2, 0.8, 4.8, 1.0));
        Assert.assertEquals(55579, mainUtils.getDistanceToRestaurants(2.6, 0.9, 3.0, 1.2));
    }

    @Test
    public void checkNumOfStarsTEST() {

        Assert.assertEquals(3, mainUtils.getNumOfStars(5.0));
        Assert.assertEquals(1, mainUtils.getNumOfStars(4.1));
        Assert.assertEquals(2, mainUtils.getNumOfStars(4.7));
        Assert.assertEquals(0, mainUtils.getNumOfStars(3.5));

    }

}


