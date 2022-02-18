package com.dinosoftlabs.chatbot.ChatBot_Chat.Chat;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.dinosoftlabs.chatbot.ChatBot_Chat.Suggestions.Question_Adapter;
import com.dinosoftlabs.chatbot.ChatBot_Chat.Suggestions.Question_Answer_Get_Set;
import com.dinosoftlabs.chatbot.Functions;
import com.dinosoftlabs.chatbot.R;
import com.dinosoftlabs.chatbot.Variables;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class Chat_Activity extends AppCompatActivity {


    DatabaseReference rootref;
    String senderid = Variables.user_id;
    String Receiverid = "Assistant";
    EditText message;

    private DatabaseReference mchatRef_reteriving;
    RecyclerView chatrecyclerview;
    private List<Chat_GetSet> mChats=new ArrayList<>();
    Chat_Adapter mAdapter;
    ProgressBar p_bar;
    LinearLayout mainlayout;
    Query query_getchat;

    IOSDialog lodding_view;
    ImageView querybtn,sendbtn;

    Map<String ,Question_Answer_Get_Set> q_a_map;
    List<Question_Answer_Get_Set> allQuestion_object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Variables.user_id= Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        senderid=Variables.user_id;

        // we get all the Question and answer from sharespreference and save it in an arraylist
        Gson gson = new Gson();
        String json = Variables.sharedPreferences.getString(Variables.q_and_a,"");
        q_a_map = gson.fromJson(json,new TypeToken<Map<String, Question_Answer_Get_Set>>() {}.getType());
        allQuestion_object = new ArrayList<Question_Answer_Get_Set>(q_a_map.values());


        // intialize the database refer
        rootref = FirebaseDatabase.getInstance().getReference();
        message = (EditText) findViewById(R.id.msgedittext);
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    querybtn.setVisibility(View.GONE);
                    sendbtn.setVisibility(View.VISIBLE);
                }else {

                    querybtn.setVisibility(View.VISIBLE);
                    sendbtn.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        p_bar=findViewById(R.id.progress_bar);

        // this is the black color loader that we see whan we click on save button
        lodding_view = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        //set layout manager to chat recycler view and get all the privous chat of th user which spacifc user
        chatrecyclerview = (RecyclerView) findViewById(R.id.chatlist);
        final LinearLayoutManager layout = new LinearLayoutManager(this);
        layout.setStackFromEnd(true);
        chatrecyclerview.setLayoutManager(layout);
        chatrecyclerview.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(chatrecyclerview, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        mAdapter = new Chat_Adapter(mChats, senderid, this, new Chat_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Chat_GetSet item, View view) {

            }
        } ,new Chat_Adapter.OnLongClickListener() {
            @Override
            public void onLongclick(Chat_GetSet item, View view) {

            }
        });

        chatrecyclerview.setAdapter(mAdapter);


        // when we scroll up then we will get the old message from firebase
        chatrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = layout.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutitems == 0 && mChats.size()>9)) {
                    userScrolled = false;
                    lodding_view.show();
                    rootref.child("AssistantChat").child(senderid + "-" + Receiverid).orderByChild("chat_id")
                            .endAt(mChats.get(0).getChat_id()).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ArrayList<Chat_GetSet> arrayList=new ArrayList<>();
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                        Chat_GetSet item=snapshot.getValue(Chat_GetSet.class);
                                        arrayList.add(item);
                                    }
                                    for (int i=arrayList.size()-2; i>=0; i-- ){
                                        mChats.add(0,arrayList.get(i));
                                    }

                                    mAdapter.notifyDataSetChanged();
                                    lodding_view.cancel();

                                    if(arrayList.size()>8){
                                        chatrecyclerview.scrollToPosition(arrayList.size());
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });



        // this the send btn action in that mehtod we will check message field is empty or not
        // if not then we call a method and pass the message
        sendbtn =findViewById(R.id.sendbtn);
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(message.getText().toString())){
                    SendMessage(message.getText().toString());
                    message.setText(null);

                }
            }
        });




        // the button will open the popular queries dialog
        querybtn =findViewById(R.id.query_btn);
        querybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Open_popular_list();
            }
        });





        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(Chat_Activity.this);
                finish();
            }
        });



        mainlayout=findViewById(R.id.typeindicator);

        // this method receiver the type indicator of second user to tell that his friend is typing or not


        SetQuestionHint("null");


    }


    // start we will get the chat form the firebase
    ValueEventListener valueEventListener;
    ChildEventListener eventListener;
    @Override
    public void onStart() {
        super.onStart();
        mChats.clear();
        mchatRef_reteriving = FirebaseDatabase.getInstance().getReference();
        query_getchat = mchatRef_reteriving.child("AssistantChat").child(senderid + "-" + Receiverid);

        // this will get all the messages between two users
        eventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Chat_GetSet model = dataSnapshot.getValue(Chat_GetSet.class);
                    mChats.add(model);
                    mAdapter.notifyDataSetChanged();
                    chatrecyclerview.scrollToPosition(mChats.size() - 1);
                }
                catch (Exception ex) {
                    Log.e("", ex.getMessage());
                }
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
                Log.d("", databaseError.getMessage());
            }
        };

        // this will check the two user are do chat before or not
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    p_bar.setVisibility(View.GONE);
                    query_getchat.removeEventListener(valueEventListener);
                }
                else {
                    Send_First_default_Message();
                    p_bar.setVisibility(View.GONE);
                    query_getchat.removeEventListener(valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query_getchat.limitToLast(20).addChildEventListener(eventListener);

        mchatRef_reteriving.child("AssistantChat").child(senderid + "-" + Receiverid).addValueEventListener(valueEventListener);
    }



    // this will add the new message in chat node
    public void SendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final String formattedDate = df.format(c);

        final String current_user_ref = "AssistantChat" + "/" + senderid + "-" + Receiverid;

        DatabaseReference reference = rootref.child("AssistantChat").child(senderid + "-" + Receiverid).push();
        final String pushid = reference.getKey();
        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", Receiverid);
        message_user_map.put("sender_id", senderid);
        message_user_map.put("chat_id",pushid);
        message_user_map.put("text", message);
        message_user_map.put("time", "");
        message_user_map.put("sender_name", Variables.user_name);
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                Check_Reply_Message(message);
            }
        });


    }


    // when user enter some query in chat then we will get its reply
    public void Check_Reply_Message(final String message){
        if (q_a_map.containsKey(message.toLowerCase())) {
            mainlayout.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Question_Answer_Get_Set item=(Question_Answer_Get_Set) q_a_map.get(message.toLowerCase());
                    ReplyMessage(item.answer);
                    SetQuestionHint(item.id);


                }
            },2000);
        }else {

            // if there is no reply match in database then we will send this message
            mainlayout.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    ReplyMessage("Sorry! I did't get you");

                }
            },2000);
        }
    }


    // when the user first ever open the chat then we will send the default two messages
    public void Send_First_default_Message(){
        mainlayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ReplyMessage("Hi I’m your Gdex Assistant");

                Send_second_default_Message();

            }
        },2000);
    }

    public void Send_second_default_Message(){
        mainlayout.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ReplyMessage("I can help you find what you need and get things done");

            }
        },2000);
    }



    // this will add the new message in chat node from our rebot
    public void ReplyMessage(final String message) {
        mainlayout.setVisibility(View.GONE);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        final String formattedDate = df.format(c);

        final String current_user_ref = "AssistantChat" + "/" + senderid + "-" + Receiverid;

        DatabaseReference reference = rootref.child("AssistantChat").child(senderid + "-" + Receiverid).push();
        final String pushid = reference.getKey();
        final HashMap message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", senderid );
        message_user_map.put("sender_id", Receiverid);
        message_user_map.put("chat_id",pushid);
        message_user_map.put("text", message);
        message_user_map.put("time", "");
        message_user_map.put("sender_name", "Assistant");
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);

        rootref.updateChildren(user_map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }



    // this will show the some query as a suggestion at the
    public void SetQuestionHint(String id){
        List<String> datalist =new ArrayList<String>();

        if(!id.equals("null")){
            for (int i=0;i<allQuestion_object.size();i++){
                if(allQuestion_object.get(i).level.equals(id))
                    datalist.add(allQuestion_object.get(i).question);
            }

            if(datalist.isEmpty()){
                if(allQuestion_object.size()>11){
                    List<Question_Answer_Get_Set> list=allQuestion_object
                            .subList(allQuestion_object.size()-10,allQuestion_object.size());
                    for (int i=0;i<list.size();i++){
                        datalist.add(list.get(i).question);
                    }
                }
                else {
                    for (int i=0;i<allQuestion_object.size();i++){
                        datalist.add(allQuestion_object.get(i).question);
                    }

                }




            }


        }else {

            if(allQuestion_object.size()>11){
                List<Question_Answer_Get_Set> list=allQuestion_object
                        .subList(allQuestion_object.size()-10,allQuestion_object.size());
                for (int i=0;i<list.size();i++){
                    datalist.add(list.get(i).question);
                }
            }
            else {
                for (int i=0;i<allQuestion_object.size();i++){
                    datalist.add(allQuestion_object.get(i).question);
                }

            }


        }
        RecyclerView question_recylerview=findViewById(R.id.question_recylerview);
        question_recylerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        question_recylerview.setAdapter(new Question_Adapter(this, datalist, new Question_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                SendMessage(item);
            }
        }));
    }


    // on destory delete the typing indicator
    @Override
    public void onDestroy() {
        super.onDestroy();
        query_getchat.removeEventListener(eventListener);
    }


    // on stop delete the typing indicator and remove the value event listener
    @Override
    public void onStop() {
        super.onStop();
        query_getchat.removeEventListener(eventListener);
    }





    // we will show some random querys as a papular question in dialog
    private Random randomGenerator;
    public void Open_popular_list() {
        final ArrayList<String> arrayList=new ArrayList<>();


        // get the random question
        randomGenerator = new Random();
        for (int i=0;i<10;i++){
            int index = randomGenerator.nextInt(allQuestion_object.size());
            String item = allQuestion_object.get(index).question;
            if(!arrayList.contains(item)){
                arrayList.add(item);
            }
        }


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_dialog_textview, arrayList){
        };

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_dialog_popular_question);

        ListView listView = dialog.findViewById(R.id.popular_list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                message.setText(arrayList.get(position));
                dialog.dismiss();
            }
        });

        ImageButton close_btn = (ImageButton) dialog.findViewById(R.id.crossbtn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displyheight=displayMetrics.heightPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        int dialogWindowWidth = (int) (displayWidth * 0.9f);
        int dialogWindowheight = (int) (displyheight * 0.8f);

        layoutParams.width = dialogWindowWidth;
        layoutParams.height =dialogWindowheight;
        dialog.getWindow().setAttributes(layoutParams);
    }
}
