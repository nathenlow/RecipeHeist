package sg.edu.np.mad.recipeheist;

public class RecipePreview {
    private String id;
    private String title;
    private String imagePath;
    private String duration;

    public RecipePreview(String id, String title, String imagePath, String duration) {
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.duration = duration;
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

    public String getduration() {
        return duration;
    }

    public void setduration(String duration) {
        this.duration = duration;
    }
}
