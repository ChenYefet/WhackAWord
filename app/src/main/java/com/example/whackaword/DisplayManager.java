package com.example.whackaword;

import android.widget.ImageView;

import java.util.Map;

/**
 * The DisplayManager class is responsible for displaying food items on food cards
 */
public class DisplayManager
{
    /**
     * Causes food cards to display food items
     */
    public static void displayFoodItemsOnCards(WhackAWordActivity aWhackAWordActivity, Map<FoodItem, FoodCard> mapOfFoodItemsToTheirFoodCards)
    {
        for (FoodItem foodItem : mapOfFoodItemsToTheirFoodCards.keySet())
        {
            FoodCard foodCard = mapOfFoodItemsToTheirFoodCards.get(foodItem);
            ImageView foodCardImageView = aWhackAWordActivity.findViewById(foodCard.getImageViewID());
            foodCardImageView.setImageResource(foodItem.getImageID());
        }
    }

}