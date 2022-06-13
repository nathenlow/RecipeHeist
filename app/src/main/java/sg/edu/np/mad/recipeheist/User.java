package sg.edu.np.mad.recipeheist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable
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

    protected User(Parcel in) {
        userID = in.readString();
        email = in.readString();
        username = in.readString();
        description = in.readString();
        following = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(description);
        dest.writeStringList(following);
    }
}
