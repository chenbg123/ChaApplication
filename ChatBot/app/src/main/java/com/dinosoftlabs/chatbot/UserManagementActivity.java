package com.dinosoftlabs.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dinosoftlabs.chatbot.Adapter.ContactAdapter;
import com.dinosoftlabs.chatbot.Model.ContactModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserManagementActivity extends AppCompatActivity {


    RecyclerView recyleView;
    ContactAdapter contactAdapter;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        recyleView=(RecyclerView)findViewById(R.id.userManagement);
        recyleView.setLayoutManager(new LinearLayoutManager(this));


        FirebaseRecyclerOptions<ContactModel> options =
                new FirebaseRecyclerOptions.Builder<ContactModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("ChatUser"), ContactModel.class)
                        .build();

        contactAdapter=new ContactAdapter(options);
        recyleView.setAdapter(contactAdapter);


        floatingActionButton=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddActivity.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        contactAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        contactAdapter.stopListening();
    }
}