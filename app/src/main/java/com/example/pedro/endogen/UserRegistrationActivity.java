package com.example.pedro.endogen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.pedro.myapplication.backend1.users.model.User;
import com.example.pedro.myapplication.backend1.users.Users;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserRegistrationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_registration, menu);
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


    public void onButtonRegisterPressed(View v)
    {
        User current= new User();
        EditText editText= (EditText)findViewById(R.id.name_text);
        String value= editText.getText()+"";
        current.setName(value);

        editText= (EditText)findViewById(R.id.email_text);
        value= editText.getText()+"";
        current.setEmail(value);

        editText= (EditText)findViewById(R.id.username_text);
        value= editText.getText()+"";
        current.setUsername(value);

        editText= (EditText)findViewById(R.id.password_text);
        value= editText.getText()+"";
        current.setPassword(value);

        new UserAsyncTask(this).execute(current);
        finish();
    }


    public class UserAsyncTask extends AsyncTask<User, Void, String> {
        private Users usersService = null;
        private GoogleCloudMessaging gcm;
        private Context context;
        public UserAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(User... params) {
            if (usersService == null) {
                Users.Builder builder = new Users.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
                        //.setRootUrl("http://10.0.3.2:8080/_ah/api/")
                        .setRootUrl("https://endogen-966.appspot.com/_ah/api/")
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

            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                msg = "You're now registered!";
                usersService.addUser(params[0]).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: " + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("user registered").log(Level.INFO, msg);
        }

    }




}
