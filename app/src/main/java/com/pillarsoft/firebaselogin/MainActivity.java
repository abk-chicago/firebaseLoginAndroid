package com.pillarsoft.firebaselogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;



public class MainActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static String LOG_TAG = ACTIVITY_SERVICE.getClass().getName();
    private Button anon, email_create, email_signin, logout;
    private SignInButton google;

    private static final int RC_SIGN_IN = 9001;
    private TextView log_status, mProfileDisplayName, mProfileEmail;
    private EditText email, password;
    private String getEmail, getPassword;
    private GoogleApiClient mGoogleApiClient;
    private ImageView mProfileImage;


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        log_status = (TextView) findViewById(R.id.mStatus);
        email = (EditText) findViewById(R.id.etEmail);
        password = (EditText) findViewById(R.id.etPassword);
        mProfileDisplayName = (TextView) findViewById(R.id.tvName);
        mProfileEmail = (TextView) findViewById(R.id.tvEmail);
        mAuth = FirebaseAuth.getInstance();
        mProfileImage = (ImageView) findViewById(R.id.ivIcon);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(), "onAuthStateChanged:signed_in:"+ user.getUid(), Toast.LENGTH_LONG).show();
                    displayUser();
                    log_status.setText("Logged in as: "+user.getUid());
                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                    log_status.setText("Signed out");
                }

            }
        };

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                //.requestIdToken("951654170887-ssk2eia0pchnohv9d05287d8l7bfc97q.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        anon = (Button) findViewById(R.id.mAnon);
        anon.setOnClickListener(this);
        email_create = (Button) findViewById(R.id.mEmail);
        email_create.setOnClickListener(this);
        email_signin = (Button) findViewById(R.id.mEmailSignIn);
        email_signin.setOnClickListener(this);
        logout = (Button) findViewById(R.id.mLogout);
        logout.setOnClickListener(this);
        google = (SignInButton) findViewById(R.id.mGoogle);
        google.setSize(SignInButton.SIZE_STANDARD);
        google.setScopes(gso.getScopeArray());
        google.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mAnon:
                mAuth.signInAnonymously()
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(LOG_TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(LOG_TAG, "signInAnonymously", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                break;
            case R.id.mEmail:
                getEmail = email.getText().toString();
                getPassword = password.getText().toString();
                Log.d(LOG_TAG, "email: "+getEmail+" password: "+getPassword);
                mAuth.createUserWithEmailAndPassword(getEmail, getPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());


                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(LOG_TAG, "signInWithEmail:failed", task.getException());
                                    Log.w(LOG_TAG, "email: "+email.toString()+" password: "+password.toString());
                                    Toast.makeText(MainActivity.this, R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                break;
            case R.id.mEmailSignIn:
                getEmail = email.getText().toString();
                getPassword = password.getText().toString();
                Log.d(LOG_TAG, "email: "+getEmail+" password: "+getPassword);
                mAuth.signInWithEmailAndPassword(getEmail, getPassword)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(LOG_TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w(LOG_TAG, "signInWithEmail:failed", task.getException());
                                    Log.w(LOG_TAG, "email: "+email.toString()+" password: "+password.toString());
                                    Toast.makeText(MainActivity.this, R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.mLogout:
                    try{
                        signOut();
                    } catch (Error e){
                        Log.w(LOG_TAG, "Signout error:"+e.toString());
                    }
                    FirebaseAuth.getInstance().signOut();
                    displayUser();
                break;
            case R.id.mGoogle:
                    signIn();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(LOG_TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult ="+requestCode+" resultCode ="+resultCode+" data="+data.toString());

            if (resultCode == RESULT_OK) {
                Log.d(LOG_TAG, "result Ok");

                Toast.makeText(this, "User signed in!", Toast.LENGTH_SHORT).show();
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    //handleSignInResult(result);
                    firebaseAuthWithGoogle(account);
                    Log.d(LOG_TAG, "GoogleSignIn complete");

                } else {
                    // Something didn't work out. Let the user know and wait for them to sign in again
                    Log.w(LOG_TAG, "error: " + resultCode);
                }

            }
    }


    private void displayUser() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            FirebaseUser user = auth.getCurrentUser();
            try{
                Glide.with(this).load(user.getPhotoUrl().toString()).into(mProfileImage);
            } catch (Exception e){
                Glide.with(this).load(R.mipmap.ic_launcher).into(mProfileImage);
            }
            try {
                mProfileDisplayName.setText(user.getDisplayName());
            } catch (Exception e){
                mProfileDisplayName.setText("");
            }
            try {
                mProfileEmail.setText(user.getEmail());
            } catch (Error e){
                mProfileEmail.setText("");
            }
        }

    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            log_status.setText("Logged in:"+ acct.getDisplayName());
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }
}
