package com.example.whackaword;

import java.util.HashSet;
import java.util.Set;

/**
 * The Selector class is responsible for selecting
 * which food items belong to which food cards,
 * and for selecting the correct food item,
 * during gameplay in the Whack-A-Word game.
 * It ensures that the chosen food items align with the game's rules
 * as set out in the WhackAWordActivity class
 *
 * The Selector class contains two class variables:
 *
 * correctFoodItem, which is the food item
 * whose image is on the card that the user is tasked to tap
 *
 * newFoodItemsAreToBeDisplayed, which records whether
 * new food items are to be displayed
 *
 * Note that food cards and food items are modelled as separate from each other,
 * even though the user is meant to understand that they are part of the same card.
 * For example, if a food item is displayed on a card,
 * and subsequently a different food item is displayed on the same card,
 * the user is meant to understand that
 * they are different cards emerging from the same hole,
 * whereas programmatically they are the same card with different food items,
 * one having replaced the other.
 * Currently, there are five cards (one for each hole) and nine food items,
 * and each card's food item can vary
 * (via the setImageResource method in the DisplayManager class)
 */
public class Selector
{
    public static FoodItem correctFoodItem;
    public static boolean thereAreNewFoodItems;

    /**
     * If new food items are to be displayed,
     * selects a number of food cards to be displayed
     * equal to numberOfCards,
     * and ensures that the food items to be displayed
     * are a different combination to the ones that were already displayed.
     * Otherwise, selects the same food items that were most recently displayed
     * to be displayed again on random food cards
     */
    public static void selectFoodCardsForDisplay()
    {
        if (Selector.thereAreNewFoodItems)
        {
            Selector.selectNewFoodCardsForDisplay();
        }
        else
        {
            Selector.selectSameFoodCardsForDisplay();
        }
    }

    /**
     * Sets the correct food item to one of the food items on display
     * that have not yet been correctly tapped
     */
    public static void setCorrectFoodItem()
    {
        Set<FoodItem> foodItemsOnDisplay = Collections.mapOfFoodItemsToTheirFoodCards.keySet();
        // The food items would be 'on' display (and not just set 'for' display)
        // by the end of the animations that were started in the cardsPopUp method

        for (FoodItem foodItem : foodItemsOnDisplay)
        {

            if (!Collections.correctlyTappedFoodItems.contains(foodItem))
            {
                Selector.correctFoodItem = foodItem;
                break;
            }

        }

    }

    /**
     * Helper method that selects
     * a number of food cards equal to numberOfCardsToDisplay
     * to be displayed,
     * and assigns each one an appropriate food item
     */
    private static void selectNewFoodCardsForDisplay()
    {
        for (int cardCount = 1; cardCount <= LevelProperties.numberOfCardsToDisplay; cardCount++)
        {
            FoodItem foodItemToBeDisplayed = Selector.selectAppropriateFoodItemForDisplay(cardCount);
            // Food items are appropriate for display when
            // they allow for
            // all of the food cards that are set for display
            // to display different food items,
            // including at least one that hasn't yet been correctly tapped,
            // as per the rules of the game

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
     * (an amount equal to numberOfCardsToDisplay)
     * that are set for display
     * to display different food items,
     * including at least one that hasn't yet been correctly tapped,
     * as per the rules of the game
     */
    private static FoodItem selectAppropriateFoodItemForDisplay(int cardCount)
    {
        FoodItem appropriateFoodItem = Collections.getAvailableFoodItem();

        if (cardCount == LevelProperties.numberOfCardsToDisplay)

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