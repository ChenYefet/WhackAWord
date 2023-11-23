package com.example.whackaword;

import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
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
    public static void setOnClickListenerForFoodCard(WhackAWordActivity aWhackAWordActivity, FoodCard aFoodCard, boolean isCorrectFoodCard)
    {
        FrameLayout foodCardFrameLayout = aWhackAWordActivity.findViewById(aFoodCard.getID());
        foodCardFrameLayout.setOnClickListener(v ->
        {
            if (isCorrectFoodCard)
            {
                LevelProperties.countOfSuccessfulTaps++;
                Collections.correctlyTappedFoodItems.add(aFoodCard.getFoodItem());

                WhackAWordActivity.conveyPositiveFeedback(aWhackAWordActivity, aFoodCard);
                LevelProperties.initialiseNextLevelPropertiesIfUserHasReachedTheNextLevel();

                if (LevelProperties.userWins())
                {
                    AudioManager.playAudioSequentially(aWhackAWordActivity, R.raw.well_done);
                }
                else
                {
                    TapManager.continuePlayingAfterSuccessfulTap(aWhackAWordActivity);
                }

            }
            else
            {
                AnimationManager.hideCards(aWhackAWordActivity);

                Collections.mapOfFoodItemsToTheirFoodCards.replaceAll(((foodItem, hiddenFoodCard) -> null));
                // Keeps all foodItem keys in the map while setting all their hiddenFoodCard values to null
                // since those food items need to be displayed again
                // in food cards which are not yet determined

                Selector.selectFoodCardsForDisplay(LevelProperties.numberOfCardsToDisplay, false);
                AnimationManager.cardsPopUp(aWhackAWordActivity, Collections.mapOfFoodItemsToTheirFoodCards);

                FoodItem correctFoodItem = aWhackAWordActivity.getCorrectFoodItem();
                FoodCard correctFoodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(correctFoodItem);

                TapManager.setOnClickListenerForFoodCard(aWhackAWordActivity, correctFoodCard, true);

                for (FoodItem foodItem : Collections.mapOfFoodItemsToTheirFoodCards.keySet())
                {

                    if (foodItem != correctFoodItem)
                    {
                        FoodCard incorrectFoodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(foodItem);

                        TapManager.setOnClickListenerForFoodCard(aWhackAWordActivity, incorrectFoodCard, false);
                    }

                }

            }

        });

        Collections.foodCardFrameLayoutsWithClickListeners.add(foodCardFrameLayout);
    }

    /**
     * Helper method that hides cards and plays Whack-A-Word again
     * if the user hasn't won
     */
    private static void continuePlayingAfterSuccessfulTap(WhackAWordActivity aWhackAWordActivity)
    {
        AnimationManager.hideCards(aWhackAWordActivity);
        Collections.availableFoodItems = new ArrayList<>(Collections.foodItems);
        Collections.mapOfFoodItemsToTheirFoodCards = new HashMap<>();
        aWhackAWordActivity.playWhackAWord();
    }

    /**
     * Clears click listeners from each card
     */
    public static void clearClickListeners()
    {
        for (FrameLayout foodCardFrameLayout: Collections.foodCardFrameLayoutsWithClickListeners)
        {
            foodCardFrameLayout.setOnClickListener(null);
        }

        Collections.foodCardFrameLayoutsWithClickListeners = new HashSet<>();
    }

}