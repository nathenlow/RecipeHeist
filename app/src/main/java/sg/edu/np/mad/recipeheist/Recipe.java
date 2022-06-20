package sg.edu.np.mad.recipeheist;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable, Parcelable {
    private String recipeID;
    private String title;
    private String description;
    private String duration;
    private int servings;
    private String imagePath;
    private String foodcategory;
    private ArrayList<String> ingridient;
    private ArrayList<String> instructions;
    private ArrayList<String> like;
    private String userID;

    public Recipe(){}

    public Recipe(String recipeID, String title, String description, String duration, int servings, String imagePath, String foodcategory, ArrayList<String> ingridient, ArrayList<String> instructions, ArrayList<String> like, String userID) {
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

    protected Recipe(Parcel in) {
        recipeID = in.readString();
        title = in.readString();
        description = in.readString();
        duration = in.readString();
        servings = in.readInt();
        imagePath = in.readString();
        foodcategory = in.readString();
        ingridient = in.createStringArrayList();
        instructions = in.createStringArrayList();
        like = in.createStringArrayList();
        userID = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
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

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipeID);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(duration);
        dest.writeInt(servings);
        dest.writeString(imagePath);
        dest.writeString(foodcategory);
        dest.writeStringList(ingridient);
        dest.writeStringList(instructions);
        dest.writeStringList(like);
        dest.writeString(userID);
    }
}



