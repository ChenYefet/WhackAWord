package com.example.whackaword;

import android.widget.ImageView;

/**
 * The DisplayManager class is responsible for displaying food items on food cards
 */
public class DisplayManager
{
    /**
     * Causes food cards to display food items
     */
    public static void displayFoodItemsOnCards(WhackAWordActivity aWhackAWordActivity)
    {
        for (FoodItem foodItem : Collections.mapOfFoodItemsToTheirFoodCards.keySet())
        {
            FoodCard foodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(foodItem);
            ImageView foodCardImageView = aWhackAWordActivity.findViewById(foodCard.getImageViewID());
            foodCardImageView.setImageResource(foodItem.getImageID());
        }
    }

}