package sg.edu.np.mad.recipeheist;

import java.util.ArrayList;

public class User
{
    private String userID;
    private String email;
    private String username;
    private String description;
    private ArrayList<String> following;

    public User(){}
    public User(String userID, String email, String username, String description, ArrayList<String> following) {
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.description = description;
        this.following = following;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }
}
