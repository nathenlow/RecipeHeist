# RecipeHeist
RecipeHeist is an app that allows users to get inspirations and ideas for their next meal by allowing users to share their own recipes with others...

## Team Members
- Nathen Low (S10222101F)
- Joseph Wong (S10204123E)
- Denzel Lee (S10221799B)
- Dhanasyam (10226152?)
- Hsu Yuan (10223040?)

## Stage 1

### Roles and contributions
- Nathen Low 
    - created MainActivity
    - created the foundation of the app consisting of bottom navigation view which navigates User to different pages and the 4 main fragments (Browse,Updates,History,Profile)
    - created the sign up and sign in page
    - tried many different HTTP clients and decided on the most optimal HTTP client (OkHttp) for our online database (reastdb.io) and created a class (RestDB) that is filled with get, post, put, patch methods for my team to use.
    - created the RecipeItem activity which displays the recipe infomation (refer to appendix RecipeItem) and handles actions such as like and bookmark.
    - created the search function to search for recipes. It saves recent searches too. (refer to appendix SearchRecipe)
    - created the browse fragment. It contains features that enhance one's browsing experience such as pagination. (refer to appendix Browse)
    
- Joseph Wong
    - created the layout for profile page, displays the user details and recipe.
    - created the add recipe page, located at the user profile page once they have logged in.
    - created simple edit profile page (currently only has one button "logout"), to allow the user to logout of their account.
    - manage the publishing of app.
    - created app logo.

### Appendices
#### Browse
<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/Browse1.jpg" width="200" alt="Browse1"/>&emsp;<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/Browse2.jpg" width="200" alt="Browse2"/>

#### SearchRecipe
<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/Search.jpg" width="200" alt="Search"/>


#### RecipeItem
<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/RecipeItem1.jpg" width="200" alt="RecipeItem1"/>&emsp;<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/RecipeItem2.jpg" width="200" alt="RecipeItem2"/>&emsp;<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/RecipeItem3.jpg" width="200" alt="RecipeItem3"/>


#### Sign In & Sign Up
<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/SignIn.jpg" width="200" alt="Sign In"/>&emsp;<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/SignUp.jpg" width="200" alt="Sign Up"/>

## Stage 2

### Roles and contributions
- Nathen Low 
    - Improve app performance by removing the refresh of fragment when navigated to (i.e. use back old data). 
    - created SwipeRefreshLayout for browse, updates, history and profile (i.e. initialize page)
    - Improve the performance of RecipeItem activity by using multithreading and AsyncTask for downloading and uploading of data to restDB.
    - created the updates fragment. It displays recent updates. It also have an refresh button in the action bar which calls the UpdateService
    - created the UpdateService (a foreground service). It gets the recipies of all the users(chefs) that the User followed and saves it in a SQL Lite Database. It also display a progress notification.   
    - created the history fragment. Display history. It contains features that enhance one's browsing experience such as pagination.
    - created the settings activity. It allows User to change theme, set update frequency, set default update date, clear updates, clear history and logout  
    
- Joseph Wong
    - Improve the user interface for add recipe activity by updating the input box designs.
    - Improve user experience for add recipe activity by allowing users to edit the ingredients/instructions that they have entered before (making use of touch events).
    - Created edit profile activity, allows the user to edit their profile, such as their profile picture, name, and bio.
    - Created feature for users to access their camera when uploading a food image or when uploading an image as their profile picture in the edit profile activity.
    - Created a bottom slide in & out animation for the bottom dialog used for prompting users on whether they want to select an image from the camera or their gallery.
    - Created a countdown timer activity, as well as a TimeService (foreground service) to display timer in the notification.


### Appendices
#### Updates
<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/Updates.jpg" width="200" alt="Updates"/>

#### Updates Notification
<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/Update_progress.jpg" width="300" alt="Update Progress"/>&emsp;<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/Update_Completed.jpg" width="300" alt="Update Completed"/>
  
#### History
<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/History.jpg" width="200" alt="History"/>

#### Settings
<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/Settings_login.jpg" width="200" alt="Settings"/>&emsp;<img src="https://raw.githubusercontent.com/punchyface/Images/main/RecipeHeist/Settings_nologin.jpg" width="200" alt="Settings for User that are not logged in"/>
