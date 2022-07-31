package sg.edu.np.mad.recipeheist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import android.util.Patterns;


public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText editEmail, editUsername, editPassword;
    private Button btnSignUp;
    private ProgressBar progressBar;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.editTextEmail);
        editUsername = findViewById(R.id.editTextTUsername);
        editPassword = findViewById(R.id.editPassword);

        progressBar = findViewById(R.id.progressBar);

        btnSignUp = findViewById(R.id.btnSignUp);



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show progress bar
                progressBar.setVisibility(View.VISIBLE);
                try {
                    signUpUser();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Function to create authentication to firebase
    private void signUpUser() throws IOException {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String username = editUsername.getText().toString().trim();

        // validate user input

        // validate the email:
        if (email.isEmpty()){
            progressBar.setVisibility(View.GONE);
            editEmail.setError("Required!");
            editEmail.requestFocus();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            progressBar.setVisibility(View.GONE);
            editEmail.setError("Please enter a valid email!");
            editEmail.requestFocus();
            return;
        }

        // validate username
        else if (username.isEmpty()){
            progressBar.setVisibility(View.GONE);
            editUsername.setError("Required!");
            editUsername.requestFocus();
            return;
        }

        // validate the password:
        else if (password.isEmpty()){
            progressBar.setVisibility(View.GONE);
            editPassword.setError("Required!");
            editPassword.requestFocus();
            return;
        }
        else if (password.length() < 6){
            progressBar.setVisibility(View.GONE);
            editPassword.setError("Minimum password length is 6 characters!");
            editPassword.requestFocus();
            return;
        }
        else {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                boolean success0 = true;
                                Toast.makeText(SignUp.this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                                try {
                                    saveUserToDB(email, username);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    success0 = false;
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignUp.this, "User sign up failed, please try again!", Toast.LENGTH_SHORT).show();
                                }
                                if (success0) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(SignUp.this, "User has been registered", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUp.this, "User sign up failed, please try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void saveUserToDB(String email, String username) throws IOException {

        RestDB restDB = new RestDB();
        String json = restDB.createNewUser(email, FirebaseAuth.getInstance().getCurrentUser().getUid(), username);
        String response = restDB.post("https://recipeheist-567c.restdb.io/rest/users", json);
    }


    // function to check database for same username
    public String checkUserName(String username) throws IOException {
        RestDB restDB = new RestDB();
        return restDB.get("https://recipeheist-567c.restdb.io/rest/users?q={\"username\": " + username + "}");
    }

    private void deleteUser(String email, String password) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        // Prompt the user to re-provide their sign-in credentials
        if (user != null) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAG", "User account deleted.");
                                            }else {
                                                deleteUser(email, password);
                                            }
                                        }
                                    });
                        }
                    });
        }
    }
}