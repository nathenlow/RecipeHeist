package sg.edu.np.mad.recipeheist;

public class RecipePreview {
    private String id;
    private String title;
    private String imagePath;
    private String username;

    public RecipePreview(String id, String title, String imagePath, String username) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
