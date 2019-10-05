package com.project.pontusgoaltracker;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class SignIn extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "Facebook Login";

    private CallbackManager mCallbackManager;

    EditText EmailET, PwdET;
    CheckBox remember;
    Button button;
    TextView signUP;
    ImageView google, linked, facebook;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.fb);
        loginButton.setPermissions(Arrays.asList("email", "public profile"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

                if (user != null){
                    Intent i = new Intent(SignIn.this, GoalListActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(SignIn.this, SignUp.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel:");
                updateUI(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError:", error);
                updateUI(null);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        //user = mAuth.getCurrentUser();
        //if(user != null ){
          //  Intent intent = new Intent(SignIn.this, GoalListActivity.class);
            //startActivity(intent);
        //}

        //Initialize all views by ID
        EmailET = findViewById(R.id.email);
        PwdET = findViewById(R.id.password);
        remember = findViewById(R.id.checkBox);
        button = findViewById(R.id.button);
        signUP = findViewById(R.id.signup);
        google = findViewById(R.id.google);
        linked = findViewById(R.id.ln);
        progressBar = findViewById(R.id.progressBar2);


        progressBar.setVisibility(View.GONE);


        //set onclick listeners where necessary

        button.setOnClickListener(this);
        signUP.setOnClickListener(this);
        google.setOnClickListener(this);
        linked.setOnClickListener(this);

        signUP = findViewById(R.id.signup);
        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //If the sign in button is clicked
            case R.id.button:
//Get the texts from the edittexts
                String LoginEmail = EmailET.getText().toString();
                String LoginPassword = PwdET.getText().toString();

                //Validation
                if (LoginEmail.isEmpty()) {
                    EmailET.setError("Please enter Email or Password");
                    EmailET.requestFocus();
                } else if (LoginPassword.isEmpty()) {
                    PwdET.setError("Enter your Password Please");
                    PwdET.requestFocus();
//Check if email matches normal pattern
                } if (Patterns.EMAIL_ADDRESS.matcher(LoginEmail).matches()) {

                //if the texts boxes are empty
                if (TextUtils.isEmpty(LoginEmail) && TextUtils.isEmpty(LoginPassword)) {

                    Toast.makeText(SignIn.this, "Please fill the Boxes", Toast.LENGTH_SHORT).show();

                    //if they are not empty, Login
                } else if (!(TextUtils.isEmpty(LoginEmail) && TextUtils.isEmpty(LoginPassword))) {

                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.signInWithEmailAndPassword(LoginEmail, LoginPassword).addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //if login is successful
                                if (!task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    progressBar.setVisibility(View.GONE);

                                    //Proceed to next activity
                                    if (mAuth.getCurrentUser().isEmailVerified()) {
                                        Toast.makeText(SignIn.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(SignIn.this, GoalListActivity.class);
                                        startActivity(i);
                                    }else {
                                        Toast.makeText(SignIn.this, "Verify email", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });


                }else{
                    Toast.makeText(SignIn.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                }
            }
                else {
                    Toast.makeText(SignIn.this, "Error Occured!! ", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.signup:
                Intent i = new Intent(SignIn.this, SignUp.class);
                startActivity(i);

                break;
            case R.id.google:
                Toast.makeText(SignIn.this, "Feature under construction", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ln:
                Toast.makeText(SignIn.this, "Feature under construction", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (user != null){
            Intent i = new Intent(SignIn.this, GoalListActivity.class);
            startActivity(i);
            updateUI(currentUser);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the facebook SDK //
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(SignIn.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user){
    }

}
