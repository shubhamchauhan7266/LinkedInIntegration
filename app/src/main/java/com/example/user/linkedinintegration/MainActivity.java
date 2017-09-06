package com.example.user.linkedinintegration;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private Button buttonLoginWithLinkedIn;
    private ImageView imageViewProfile;
    private TextView textViewName;
    private TextView textViewEmailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLoginWithLinkedIn=(Button)findViewById(R.id.buttonLoginWithLinkedIn);
        imageViewProfile=(ImageView)findViewById(R.id.imageViewProfile);
        textViewName=(TextView)findViewById(R.id.textViewName);
        textViewEmailId=(TextView)findViewById(R.id.textViewEmailId);

        buttonLoginWithLinkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticationProcess();
            }
        });


    }

    void authenticationProcess(){
        final Activity thisActivity = this;

        LISessionManager.getInstance(getApplicationContext()).init(thisActivity, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                sendRequest();

            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
            }
        }, true);
    }

    void sendRequest(){
        String url = "https://api.linkedin.com/v1/people/~:(id,email-address,first-name,last-name)";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) throws JSONException {
                // Success!
                JSONObject jsonResponse = apiResponse.getResponseDataAsJson();
                textViewName.setText(jsonResponse.getString("firstName")+" "+jsonResponse.getString("lastName"));
                textViewEmailId.setText(""+jsonResponse.getString("emailAddress"));
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
            }
        });
    }
    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }
}
