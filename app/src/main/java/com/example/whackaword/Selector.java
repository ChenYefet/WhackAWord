package com.example.whackaword;

import java.util.HashSet;
import java.util.Set;

/**
 * The Selector class is responsible for selecting
 * which food items belong to which food cards
 * during gameplay in the Whack-A-Word game.
 * It ensures that the chosen food items align with the game's rules
 * as set out in the WhackAWordActivity class
 */
public class Selector
{
    /**
     * If new food items are to be displayed,
     * selects a number of food cards to be displayed
     * equal to numberOfCards,
     * and ensures that the food items to be displayed
     * are a different combination to the ones that were already displayed.
     * Otherwise, selects the same food items that were most recently displayed
     * to be displayed again on random food cards
     */
    public static void selectFoodCardsForDisplay(int numberOfCards, boolean newFoodItemsAreToBeDisplayed)
    {
        if (newFoodItemsAreToBeDisplayed)
        {
            Selector.selectNewFoodCardsForDisplay(numberOfCards);
        }
        else
        {
            Selector.selectSameFoodCardsForDisplay();
        }
    }

    /**
     * Helper method that selects
     * a number of food cards equal to numberOfCards
     * to be displayed,
     * and assigns each one an appropriate food item
     */
    private static void selectNewFoodCardsForDisplay(int numberOfCards)
    {
        for (int cardCount = 1; cardCount <= numberOfCards; cardCount++)
        {
            FoodItem foodItemToBeDisplayed = Selector.selectAppropriateFoodItemForDisplay(cardCount, numberOfCards);
            FoodCard foodCardToBeDisplayed = Collections.getAvailableFoodCard();

            foodCardToBeDisplayed.setFoodItem(foodItemToBeDisplayed);

            Collections.mapOfFoodItemsToTheirFoodCards.put(foodItemToBeDisplayed, foodCardToBeDisplayed);
            Collections.availableFoodItems.remove(foodItemToBeDisplayed);
            Collections.availableFoodCards.remove(foodCardToBeDisplayed);
        }
    }

    /**
     * Helper method that selects
     * the same food items that were most recently displayed on food cards
     * to be displayed again on random food cards
     */
    private static void selectSameFoodCardsForDisplay()
    {
        for (FoodItem foodItemToBeDisplayed : Collections.mapOfFoodItemsToTheirFoodCards.keySet())

        // Note that food items that are set for display again
        // would have been kept in mapOfFoodItemsToTheirFoodCards.
        // Their previous corresponding food cards
        // would have been removed from the map (i.e. set to null)
        // via the setOnClickListenerForFoodCard method
        // so that they could be replaced by random food cards

        {
            FoodCard foodCardToBeDisplayed = Collections.getAvailableFoodCard();
            foodCardToBeDisplayed.setFoodItem(foodItemToBeDisplayed);

            Collections.mapOfFoodItemsToTheirFoodCards.put(foodItemToBeDisplayed, foodCardToBeDisplayed);
            Collections.availableFoodCards.remove(foodCardToBeDisplayed);
        }

    }

    /**
     * Helper method that returns a food item that is appropriate to be displayed,
     * i.e. a food item that allows for all of the food cards
     * (an amount equal to numberOfCardsToBeDisplayed)
     * that are set for display
     * to display different food items,
     * including at least one that hasn't yet been correctly tapped
     */
    private static FoodItem selectAppropriateFoodItemForDisplay(int cardCount, int numberOfCardsToBeDisplayed)
    {
        FoodItem appropriateFoodItem = Collections.getAvailableFoodItem();

        if (cardCount == numberOfCardsToBeDisplayed)

        // I.e. if the current count of cards has reached the number of cards to be displayed ...

        {
            Set<FoodItem> foodItemsSelectedForDisplay = Collections.mapOfFoodItemsToTheirFoodCards.keySet();
            Set<FoodItem> foodItemsSelectedForDisplayThatHaveNotYetBeenCorrectlyTapped = new HashSet<>(foodItemsSelectedForDisplay);
            foodItemsSelectedForDisplayThatHaveNotYetBeenCorrectlyTapped.removeAll(Collections.correctlyTappedFoodItems);

            if (foodItemsSelectedForDisplayThatHaveNotYetBeenCorrectlyTapped.size() == 0)

            // ... and all the food items so far selected to be displayed
            // have previously been correctly tapped ...

            {

                while (Collections.correctlyTappedFoodItems.contains(appropriateFoodItem))
                {
                    appropriateFoodItem = Collections.getAvailableFoodItem();
                    // ... ensure that the last food item selected to be displayed
                    // is one which hasn't previously been correctly tapped
                    // (to ensure that the game is more challenging and fun)

                }

            }

            // Note that calling keySet() on an empty map outputs an empty set,
            // so no exception would be thrown since the map has been initialised
            // as an empty map

        }

        return appropriateFoodItem;

    }

}