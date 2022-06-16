package sg.edu.np.mad.recipeheist;


import java.util.ArrayList;

public class Recipe {
    private int recipeID;
    private String title;
    private String description;
    private String duration;
    private String servings;
    private String imagePath;
    private String foodcategory;
    private ArrayList<String> ingridient;
    private ArrayList<String> instructions;
    private ArrayList<String> like;
    private String userID;

    public Recipe(){}

    public Recipe(int recipeID, String title, String description, String duration, String servings, String imagePath, String foodcategory, ArrayList<String> ingridient, ArrayList<String> instructions, ArrayList<String> like, String userID) {
        this.recipeID = recipeID;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.servings = servings;
        this.imagePath = imagePath;
        this.foodcategory = foodcategory;
        this.ingridient = ingridient;
        this.instructions = instructions;
        this.like = like;
        this.userID = userID;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFoodcategory() {
        return foodcategory;
    }

    public void setFoodcategory(String foodcategory) {
        this.foodcategory = foodcategory;
    }

    public ArrayList<String> getIngridient() {
        return ingridient;
    }

    public void setIngridient(ArrayList<String> ingridient) {
        this.ingridient = ingridient;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    public ArrayList<String> getLike() {
        return like;
    }

    public void setLike(ArrayList<String> like) {
        this.like = like;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}



