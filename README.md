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
<img src="https://lh3.google.com/u/0/d/1LHqYGk5MicOrnHotfW2p_fDA-UE_8JrB=w1920-h942-iv2" alt="Browse"/>
#### SearchRecipe
<img src="https://lh3.google.com/u/0/d/13X6SGtBiQnEbY_Vj8AM5kz1QJn3OdRYS=w1920-h942-iv3" alt="Search"/>
#### RecipeItem
<img src="https://lh3.google.com/u/0/d/1gAqxn_3fVbwmXhpJj7687PxRXJW_as9y=w1920-h942-iv2" alt="RecipeItem01"/>
<img src="https://lh3.google.com/u/0/d/18PEqqi0xQNVyE-_YPF_xEwMkSVSl2GTe=w1227-h942-iv4" alt="RecipeItem02"/>

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


### Appendices
