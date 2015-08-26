package com.example.pedro.endogen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pedro.myapplication.backend1.users.Users;
import com.example.pedro.myapplication.backend1.users.model.User;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginActivity extends ActionBarActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //result after login
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //part of facebook SDK:
        // Logs 'install' and 'app activate' App Events.
    }

    @Override
    protected void onPause() {
        super.onPause();
        //part of facebook SDK:
        // Logs 'app deactivate' App Event.

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public User loggedUser;
    public void onLogInButtonPressed(View v)
    {
        EditText editText= (EditText)findViewById(R.id.login_username_text);
        String username= editText.getText()+"";
        editText= (EditText)findViewById(R.id.login_password_text);


        String password= editText.getText()+"";

        String [] params= new String [2];
        params[0]=username;
        params[1]=password;
        new LoginUserAsyncTask(this).execute(params);

    }
    public void onSignUpButtonPressed(View v)
    {
        Intent intent = new Intent(this, UserRegistrationActivity.class);
        startActivity(intent);
    }


    public class LoginUserAsyncTask extends AsyncTask<String [], Void, User> {
        private Users usersService = null;
        private GoogleCloudMessaging gcm;
        private Context context;
        public LoginUserAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected User doInBackground(String[]... params) {
            if (usersService == null) {
                Users.Builder builder = new Users.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
                        //.setRootUrl("http://10.0.3.2:8080/_ah/api/")
                        .setRootUrl(Constants.APPENGINE_URL)
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end of optional local run code
                usersService = builder.build();
            }

            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }

            try {
                return usersService.logIn(params[0][0],params[0][1]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(User usr) {

            String msg;
            if(usr==null){
                msg="User not found";
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
            else{

                loggedUser=usr;
                msg="Welcome "+loggedUser.getName()+"!";
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Globals.loggedUser=usr;
                Intent intent = new Intent();
                setResult(0, intent);
                finish();
            }


        }

    }




}
