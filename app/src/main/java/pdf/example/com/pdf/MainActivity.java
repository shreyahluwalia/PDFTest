package pdf.example.com.pdf;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private ListView FileView;
    public static final String PROFILE_USER_ID = "USER_ID";
    public static final String PROFILE_DISPLAY_NAME = "PROFILE_DISPLAY_NAME";
    public static final String PROFILE_USER_EMAIL = "USER_PROFILE_EMAIL";
    public static final String PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gso = ((MyApplication) getApplication()).getGoogleSignInOptions();
        mGoogleApiClient = ((MyApplication) getApplication()).getGoogleApiClient(MainActivity.this, this);
        SignInButton mSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
        assert mSignInButton != null;
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setScopes(gso.getScopeArray());
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);


                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE },
                            0);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            Toast.makeText(getApplicationContext(), "Signed In!", Toast.LENGTH_LONG).show();
            GoogleSignInAccount userAccount = result.getSignInAccount();
            String userId = userAccount.getId();
            String displayedUsername = userAccount.getDisplayName();
            String userEmail = userAccount.getEmail();
            String userProfilePhoto = userAccount.getPhotoUrl().toString();
            Intent googleSignInIntent = new Intent(MainActivity.this, ProfileActivity.class);
            googleSignInIntent.putExtra(PROFILE_USER_ID, userId);
            googleSignInIntent.putExtra(PROFILE_DISPLAY_NAME, displayedUsername);
            googleSignInIntent.putExtra(PROFILE_USER_EMAIL, userEmail);
            googleSignInIntent.putExtra(PROFILE_IMAGE_URL, userProfilePhoto);
            startActivity(googleSignInIntent);
//            setContentView(R.layout.activity_profile);


        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleSignInResult(googleSignInResult);


                }
            });
        }


    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}