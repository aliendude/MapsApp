package com.example.pedro.endogen.Fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedro.endogen.Constants;
import com.example.pedro.endogen.Globals;
import com.example.pedro.endogen.LoggedUserActivity;
import com.example.pedro.endogen.Message;
import com.example.pedro.endogen.MessageAdapter;
import com.example.pedro.endogen.R;
import com.example.pedro.endogen.SessionManager;
import com.example.pedro.myapplication.backend1.chatmessages.Chatmessages;
import com.example.pedro.myapplication.backend1.chatmessages.model.ChatMessage;
import com.example.pedro.myapplication.backend1.chatmessages.model.ChatMessageCollection;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A chat fragment containing messages view and input form.
 */
public class ChatFragment extends Fragment {

    private static final int REQUEST_LOGIN = 0;

    private static final int TYPING_TIMER_LENGTH = 600;

    private RecyclerView mMessagesView;
    private EditText mInputMessageView;
    private List<Message> mMessages = new ArrayList<Message>();
    private RecyclerView.Adapter mAdapter;
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private String mUsername;
    private Socket mSocket;
    private int numUsers;

    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new MessageAdapter(activity, mMessages);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.on("login", onLogin);
        mSocket.connect();

        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        mUsername = sessionManager.getUserDetails().get("name");
        mSocket.emit("add user", mUsername);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        destroy();
    }

    public void destroy() {
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
        mSocket.off("login", onLogin);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessagesView = (RecyclerView) view.findViewById(R.id.messages);
        mMessagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMessagesView.setAdapter(mAdapter);

        mInputMessageView = (EditText) view.findViewById(R.id.message_input);
        mInputMessageView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        mInputMessageView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;

                if (!mTyping) {
                    mTyping = true;
                    mSocket.emit("typing");
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_leave) {
            leave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addLog(String message) {
        mMessages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }

    private void addMessage(String username, String message, String timeCreated) {
        mMessages.add(new Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).timecreated(timeCreated).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void addTyping(String username) {
        mMessages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        mAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void removeTyping(String username) {
        for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }
    }

    private void attemptSend() {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        mTyping = false;

        String message = mInputMessageView.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            mInputMessageView.requestFocus();
            return;
        }
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        mInputMessageView.setText("");
        addMessage(mUsername, message, currentDate);


        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        String userId = sessionManager.getUserDetails().get("id");

        // perform the sending message attempt.
        mSocket.emit("new message", message);
        ChatMessage mToSend = new ChatMessage().setMessage(message).setTimeCreated(currentDate);
        new CreateChatMessageAsyncTask(getActivity()).execute(new Pair(userId, mToSend));

    }

    // private void startSignIn() {
    //     mUsername = null;
    //     Intent intent = new Intent(getActivity(), ChatLoginActivity.class);
    //     startActivityForResult(intent, REQUEST_LOGIN);
    // }

    private void leave() {
        mUsername = null;
        mSocket.disconnect();
        mSocket.connect();
        //startSignIn();
    }

    private void scrollToBottom() {
        try {
            mMessagesView.scrollToPosition(mAdapter.getItemCount() - 1);
        } catch (Exception e) {
        }
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                    }
                }
            });
        }
    };
    private Emitter.Listener onLogin = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        numUsers = data.getInt("numUsers");

                    } catch (JSONException e) {
                        return;
                    }
                    //now that the user is logged into the chat server get the previous messages from the database
                    new ChatMessageAsyncRetriever().execute();

                }
            });
        }

    };
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");
                    } catch (JSONException e) {
                        return;
                    }

                    removeTyping(username);

                    addMessage(username, message, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    //only add a notification id the app is stopped
                    if(Globals.isStopped) {
                        Intent intent = new Intent(getActivity(), LoggedUserActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getActivity())
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("New message")
                                        .setContentText(username + " says: " + message)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);

                        NotificationManager mNotificationManager =
                                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                        int mId = 0;
                        mNotificationManager.notify(mId, mBuilder.build());
                    }
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                    removeTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    addTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        return;
                    }
                    removeTyping(username);
                }
            });
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };

    public interface OnFragmentInteractionListener {

        public void onFragmentInteraction(int position);
    }

    private Chatmessages chatMessagesService = null;

    public class CreateChatMessageAsyncTask extends AsyncTask<Pair<String, ChatMessage>, Void, String> {

        private GoogleCloudMessaging gcm;
        private Context context;

        public CreateChatMessageAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Pair<String, ChatMessage>... params) {
            if (chatMessagesService == null) {
                Chatmessages.Builder builder = new Chatmessages.Builder(AndroidHttp.newCompatibleTransport(),
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
                chatMessagesService = builder.build();
            }

            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                // msg = "You're now registered!";
                chatMessagesService.addChatMessage(Long.parseLong(params[0].first), params[0].second).execute();

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error: sending the message to GAE" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            if (msg.length() > 0) Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("chatMessage registered").log(Level.INFO, msg);
        }

    }

    private class ChatMessageAsyncRetriever extends AsyncTask<Void, Void, ChatMessageCollection> {

        public ChatMessageAsyncRetriever() {

        }

        @Override
        protected ChatMessageCollection doInBackground(Void... params) {
            if (chatMessagesService == null) {
                Chatmessages.Builder builder = new Chatmessages.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                        // otherwise they can be skipped
                        .setRootUrl(Constants.APPENGINE_URL)
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end of optional local run code

                chatMessagesService = builder.build();
            }
            try {
                return chatMessagesService.getChatMessages().execute();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ChatMessageCollection result) {

            try {

                for (ChatMessage element : result.getItems()) {
                    //double longitude = Double.parseDouble(element.getLocation().split(" ")[1]);
                    //double latitude = Double.parseDouble(element.getLocation().split(" ")[0]);
                    // create marker
                    //MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(element.getDescription());
                    // Changing marker icon
                    //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_fa_arrow_down));
                    // adding marker
                    // Marker marker= googleMap.addMarker(markerOptions);
                    addMessage(element.getCreator().getName(), element.getMessage(), element.getTimeCreated());
                }
                ;
            } catch (Exception e) {
                e.printStackTrace();
            }
            //finally
            addLog(getResources().getString(R.string.message_welcome));
            addParticipantsLog(numUsers);

        }
    }
}

