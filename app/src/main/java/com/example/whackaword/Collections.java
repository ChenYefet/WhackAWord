package com.example.whackaword;

import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * The Collections class is responsible for managing collections in the Whack-A-Word game
 *
 * It contains seven class variables:
 *
 * foodItems, which is a set of all the food items that exist within the game
 *
 * foodCards, which is a set of all the food cards that exist within the game,
 * one for each of the existing holes
 *
 * availableFoodItems, which is a list of food items
 * that are currently available to be played (it varies).
 * When a food item is set for display on a food card,
 * that food item becomes unavailable to other food cards
 * in order to avoid multiple cards bearing the same food item.
 * Since retrieving a random element of this collection requires indexing,
 * it is a list instead of a set
 *
 * availableFoodCards, which is a list of food cards
 * that are currently available to be played (it varies).
 * When a food card is set to display a food item,
 * that food card becomes unavailable to other food items
 * in order to avoid multiple food items being set to display on the same food card.
 * Since retrieving a random element of this collection requires indexing,
 * it is a list instead of a set
 *
 * (Note that the availableFoodCards list is filled within the hideCards method
 * (in addition to the onCreate method)
 * because food cards become available when cards are hidden.
 * The availableFoodItems list is not filled within the hideCards method
 * since those unavailable food items that are not on the list
 * may need to be displayed again,
 * e.g. when an incorrect card has been tapped;
 * it is filled after a correct card has been clicked instead
 * i.e. right before playing Whack-A-Word again)
 *
 * mapOfFoodItemsToTheirFoodCards, which is a map of food items that are set for display
 * to food cards upon which they are set to be displayed.
 * Note that there are currently no situations
 * in which two food cards contain the same food item.
 * If this were not the case,
 * i.e. if two different food cards could contain the same food item,
 * a map of food items to lists of food cards
 * rather than to single food cards
 * would be more appropriate
 *
 * correctlyTappedFoodItems, which is a set of food items
 * that have previously been correctly tapped.
 * A user should only need to tap
 * a food item that hasn't yet been correctly tapped
 *
 * foodCardFrameLayoutsWithClickListeners, which is a set of frame layouts,
 * each of which is effectively a card with a variable image on it.
 * This set is necessary to clear all click listeners at once
 * with the clearClickListeners() method
 */
public class Collections
{
    public static Set<FoodItem> foodItems;
    public static Set<FoodCard> foodCards;
    public static List<FoodItem> availableFoodItems;
    public static List<FoodCard> availableFoodCards;
    public static Map<FoodItem, FoodCard> mapOfFoodItemsToTheirFoodCards;
    public static Set<FoodItem> correctlyTappedFoodItems;
    public static Set<FrameLayout> foodCardFrameLayoutsWithClickListeners;

    /**
     * Initialises the collections
     */
    public static void initialiseCollections()
    {
        Collections.foodItems = new HashSet<>();
        Collections.foodCards = new HashSet<>();

        Collections.mapOfFoodItemsToTheirFoodCards = new HashMap<>();
        Collections.correctlyTappedFoodItems = new HashSet<>();
        Collections.foodCardFrameLayoutsWithClickListeners = new HashSet<>();

        Collections.fillFoodItemsSet();
        Collections.fillFoodCardsSet();

        Collections.availableFoodItems = new ArrayList<>(Collections.foodItems);
        // Fills availableFoodItems with all the food items in foodItems

        Collections.availableFoodCards = new ArrayList<>(Collections.foodCards);
        // Fills availableFoodCards with all the food cards in foodCards

    }

    /**
     * Returns an available food card,
     * selected at random from a list of available food cards
     */
    public static FoodCard getAvailableFoodCard()
    {
        Random random = new Random();
        int randomIndex = random.nextInt(Collections.availableFoodCards.size());
        return Collections.availableFoodCards.get(randomIndex);
    }

    /**
     * Returns an available food item,
     * selected at random from a list of available food items
     */
    public static FoodItem getAvailableFoodItem()
    {
        Random random = new Random();
        int randomIndex = random.nextInt(Collections.availableFoodItems.size());
        return Collections.availableFoodItems.get(randomIndex);
    }

    /**
     * Helper method that fills foodCards with all the food cards that exist within the game
     */
    private static void fillFoodCardsSet()
    {
        FoodCard foodCard1 = new FoodCard(R.id.card1, R.id.variable_food_item_for_card_1);
        FoodCard foodCard2 = new FoodCard(R.id.card2, R.id.variable_food_item_for_card_2);
        FoodCard foodCard3 = new FoodCard(R.id.card3, R.id.variable_food_item_for_card_3);
        FoodCard foodCard4 = new FoodCard(R.id.card4, R.id.variable_food_item_for_card_4);
        FoodCard foodCard5 = new FoodCard(R.id.card5, R.id.variable_food_item_for_card_5);

        Collections.foodCards.add(foodCard1);
        Collections.foodCards.add(foodCard2);
        Collections.foodCards.add(foodCard3);
        Collections.foodCards.add(foodCard4);
        Collections.foodCards.add(foodCard5);
    }

    /**
     * Helper method that fills foodItems with all the food items that exist within the game
     */
    private static void fillFoodItemsSet()
    {
        FoodItem apple = new FoodItem("Apple", R.raw.fc_apple, R.drawable.fc_apple);
        FoodItem banana = new FoodItem("Banana", R.raw.fc_banana, R.drawable.fc_banana);
        FoodItem bread = new FoodItem("Bread", R.raw.fc_bread, R.drawable.fc_bread);
        FoodItem cake = new FoodItem("Cake", R.raw.fc_cake, R.drawable.fc_cake);
        FoodItem carrot = new FoodItem("Carrot", R.raw.fc_carrot, R.drawable.fc_carrot);
        FoodItem egg = new FoodItem("Egg", R.raw.fc_egg, R.drawable.fc_egg);
        FoodItem orange = new FoodItem("Orange", R.raw.fc_orange, R.drawable.fc_orange);
        FoodItem potato = new FoodItem("Potato", R.raw.fc_potato, R.drawable.fc_potato);
        FoodItem tomato = new FoodItem("Tomato", R.raw.fc_tomato, R.drawable.fc_tomato);

        Collections.foodItems.add(apple);
        Collections.foodItems.add(banana);
        Collections.foodItems.add(bread);
        Collections.foodItems.add(cake);
        Collections.foodItems.add(carrot);
        Collections.foodItems.add(egg);
        Collections.foodItems.add(orange);
        Collections.foodItems.add(potato);
        Collections.foodItems.add(tomato);
    }

}