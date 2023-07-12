package com.example.whackaword;

import androidx.annotation.NonNull;

/**
 * An object of the FoodCard class represents
 * a card with the image of a food item
 */
public class FoodCard extends Card
{
    private FoodItem foodItem;

    /**
     * Constructor for objects of the class FoodCard
     * with arguments for all of its superclass' and its own instance variables
     */
    public FoodCard(int anID, int anImageViewID, FoodItem aFoodItem)
    {
        super(anID, anImageViewID);
        this.foodItem = aFoodItem;
    }

    /**
     * Constructor for objects of the class FoodCard
     * without an argument for its foodItem instance variable
     */
    public FoodCard(int anID, int anImageViewID)
    {
        super(anID, anImageViewID);
        this.foodItem = null;
    }

    /**
     * Getter for the food item of the food card
     */
    public FoodItem getFoodItem()
    {
        return this.foodItem;
    }

    /**
     * Setter for the food item of the food card
     */
    public void setFoodItem(FoodItem aFoodItem)
    {
        this.foodItem = aFoodItem;
    }

    /**
     * Returns a string representation of the food card
     */
    @NonNull
    @Override
    public String toString()
    {
        return "An instance of class " + this.getClass().getName() +
                " representing a card with ID " + this.getID() +
                ", image view ID " + this.getImageViewID() +
                ", and food item " + this.getFoodItem();
    }
}
