package com.android.demoapplication;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.demoapplication.entity.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private static final String TAG = "DemoApp";
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private ArrayList<Message> mMessageArrayList;
    private Button mSendButton;
    private EditText mEditText;
    DatabaseReference mMessagesDBReference;
    private String mLastMessageFromDoctor;
    private TextToSpeech mTts;
    private boolean mShouldSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mTts = new TextToSpeech(this, this);
        mEditText = (EditText) findViewById(R.id.message_edit_text);
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    if (mRecyclerView != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.scrollToPosition(mMessageArrayList.size() - 1);
                            }
                        }, 500);
                    }
                }
                return false;
            }
        });
        mSendButton = (Button) findViewById(R.id.send_message_button);
        mSendButton.setOnClickListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mMessagesDBReference = database.getReference("messages");
        mMessageArrayList = new ArrayList<>();
        mMessagesDBReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message.isFromDoctor) {
                    mLastMessageFromDoctor = message.message;
                    mShouldSpeak = true;
                }
                mMessageArrayList.add(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mMessagesDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatAdapter.setMessages(mMessageArrayList);
                mChatAdapter.notifyDataSetChanged();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.scrollToPosition(mMessageArrayList.size() - 1);
                    }
                }, 500);
                if (mTts != null && mShouldSpeak) {
                    mTts.speak(mLastMessageFromDoctor,
                            TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                            null);
                    mShouldSpeak = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager recyclerLinearLayoutManager = new LinearLayoutManager(this);

        mChatAdapter = new ChatAdapter(mMessageArrayList);
        mRecyclerView.setLayoutManager(recyclerLinearLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);
        mChatAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_message_button:
                if (mEditText.getText() != null && mEditText.getText().length() > 0) {
                    DatabaseReference newDBReference = mMessagesDBReference.push();
                    Message message = new Message();
                    message.id = 1;
                    message.message = mEditText.getText().toString();
                    message.isFromDoctor = false;
                    newDBReference.setValue(message);
                    mEditText.setText("");
                } else {
                    mEditText.setError("Enter a message to send");
                }
                break;
        }
    }

    @Override
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Lanuage data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
                mTts = null;
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.

                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
                // Greet the user.
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
            mTts = null;
        }
    }
}
