package com.example.whackaword;

import android.widget.FrameLayout;

import java.util.HashSet;

/**
 * The TapManager class is responsible for handling user interactions
 * during gameplay in the Whack-A-Word game.
 * It allows the user to receive appropriate feedback
 * upon tapping a correct or incorrect food cards
 */
public class TapManager
{
    /**
     * Sets the click listeners for
     * all the food cards that are set for display
     */
    public static void setClickListeners(WhackAWordActivity aWhackAWordActivity, FoodCard correctFoodCard, FoodItem correctFoodItem)
    {
        TapManager.setClickListenerForFoodCard(aWhackAWordActivity, correctFoodCard, true);

        if (Collections.mapOfFoodItemsToTheirFoodCards.size() > 1)

        // I.e. if there are any incorrect food items that are set for display
        // in addition to the one correct food item ...

        {

            for (FoodItem foodItem : Collections.mapOfFoodItemsToTheirFoodCards.keySet())
            {

                if (foodItem != correctFoodItem)
                {
                    FoodCard incorrectFoodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(foodItem);

                    TapManager.setClickListenerForFoodCard(aWhackAWordActivity, incorrectFoodCard, false);
                    // ... set a click listener for each of their corresponding food cards
                }

            }

        }

    }

    /**
     * Clears click listeners from each food card
     */
    public static void clearClickListeners()
    {
        for (FrameLayout foodCardFrameLayout: Collections.foodCardFrameLayoutsWithClickListeners)
        {
            foodCardFrameLayout.setOnClickListener(null);
        }

        Collections.foodCardFrameLayoutsWithClickListeners = new HashSet<>();
    }

    /**
     * When the correct food card is tapped,
     * plays the tick sound,
     * displays an animated tick,
     * continuously changes the card colour for the correct food card,
     * and checks whether the user has reached the next level or has won.
     * If the user has won, plays the 'well done' audio.
     * If the user hasn't won, continues playing the game
     *
     * Hides cards when an incorrect food card is tapped,
     * then displays random cards again with the same food items,
     * setting click listeners for the correct and incorrect food cards
     */
    private static void setClickListenerForFoodCard(WhackAWordActivity aWhackAWordActivity, FoodCard aFoodCard, boolean isCorrectFoodCard)
    {
        FrameLayout foodCardFrameLayout = aWhackAWordActivity.findViewById(aFoodCard.getID());
        foodCardFrameLayout.setOnClickListener(v ->
        {
            Collections.mapOfPopUpTimesToWhetherACardHasBeenTappedOnTime.put(AnimationManager.numberOfPopUpTimes, true);

            if (isCorrectFoodCard)
            {
                LevelProperties.countOfSuccessfulTaps++;
                Collections.correctlyTappedFoodItems.add(aFoodCard.getFoodItem());

                PositiveFeedbackAnimationManager.conveyPositiveFeedback(aWhackAWordActivity, aFoodCard);
                WhackAWordActivity.continuePlaying(aWhackAWordActivity);
            }
            else
            {
                WhackAWordActivity.tryAgain(aWhackAWordActivity);
            }

        });

        Collections.foodCardFrameLayoutsWithClickListeners.add(foodCardFrameLayout);
    }

}