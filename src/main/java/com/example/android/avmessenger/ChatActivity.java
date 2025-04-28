package com.example.android.avmessenger;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String receiverImage, receiverUID, receiverName, senderUID;
    ShapeableImageView profileImage;
    TextView receiverNameChat;
    CardView sendButton;
    EditText textMessage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public static String senderImage;
    public static String receiverIImage;

    String senderRoom, receiverRoom;
    RecyclerView messageAdapterRecyclerView;
    ArrayList<MessageModelClass> messagesArrayList;
    MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        receiverName = getIntent().getStringExtra("namee");
        receiverImage = getIntent().getStringExtra("receiverImage");
        receiverUID = getIntent().getStringExtra("uid");

        profileImage = findViewById(R.id.chatProfileImage);
        receiverNameChat = findViewById(R.id.receiverName);
        sendButton = findViewById(R.id.sendButton);
        textMessage = findViewById(R.id.textMessage);
        messageAdapterRecyclerView = findViewById(R.id.messageAdapterRecyclerView);

        messagesArrayList = new ArrayList<>();  // ✅ Must initialize BEFORE adapter

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapterRecyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(ChatActivity.this, messagesArrayList);
        messageAdapterRecyclerView.setAdapter(messagesAdapter);

        Picasso.get().load(receiverImage).into(profileImage);
        receiverNameChat.setText(receiverName);


        senderUID = auth.getUid();  // ✅ Correct assignment to global senderUID
        senderRoom = senderUID + receiverUID;
        receiverRoom = receiverUID + senderUID;

        DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModelClass messageModelClass = dataSnapshot.getValue(MessageModelClass.class);
                    messagesArrayList.add(messageModelClass);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImage = snapshot.child("profileImage").getValue(String.class);
                receiverIImage = receiverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = textMessage.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please Enter Message...", Toast.LENGTH_SHORT).show();
                    return;
                }
                textMessage.setText("");
                Date date = new Date();
                MessageModelClass messageModelClass = new MessageModelClass(message, senderUID, date.getTime());

                database.getReference().child("chats").child(senderRoom).child("messages")
                        .push().setValue(messageModelClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats").child(receiverRoom).child("messages")
                                        .push().setValue(messageModelClass);
                            }
                        });
            }
        });
    }
}
