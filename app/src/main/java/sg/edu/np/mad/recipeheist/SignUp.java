package sg.edu.np.mad.recipeheist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText editEmail, editUsername, editPassword;
    private Button btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up");

        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.editTextEmail);
        editUsername = findViewById(R.id.editTextTUsername);
        editPassword = findViewById(R.id.editPassword);

        btnSignUp = findViewById(R.id.btnSignUp);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editEmail.getText().toString().trim().isEmpty() || editPassword.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUp.this, "Empty fields are not allowed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                signUpUser();
            }
        });
    }

    //Function to create authentication to firebase
    private void signUpUser(){
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String username = editUsername.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(), email, username, "", new ArrayList<String>());

                            //To update realtime database
                            FirebaseDatabase.getInstance("https://recipeheist-ce646-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(SignUp.this, "User successfully signed up an account", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(SignUp.this, "User sign up failed, please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(SignUp.this, "User sign up failed, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    public boolean saveUserToDB() {
//        final boolean[] success = {};
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("User");
//        User newUser = new User(FirebaseAuth.getInstance().getCurrentUser().getUid() ,editEmail.getText().toString(), editUsername.getText().toString(), "", new ArrayList<String>());
////        myRef.setValue(newUser).addOnFailureListener(new OnFailureListener() {
////            @Override
////            public void onFailure(@NonNull Exception e) {
////                Toast.makeText(SignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
////                success[0] = false;
////            }
////        });
//
//        myRef.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    success[0] = true;
//                }else {
//                    Toast.makeText(SignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    success[0] = false;
//                }
//            }
//        });
//
//        return success[0];
//    }
//
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