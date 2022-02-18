package com.dinosoftlabs.chatbot.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dinosoftlabs.chatbot.Model.ContactModel;
import com.dinosoftlabs.chatbot.Model.User;
import com.dinosoftlabs.chatbot.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;


import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends FirebaseRecyclerAdapter<ContactModel,ContactAdapter.myViewHolder>{


    public ContactAdapter(@NonNull FirebaseRecyclerOptions <ContactModel> options){
        super(options);
    }

    @Override
    public myViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);
        return new myViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull  myViewHolder holder, final int position, @NonNull  ContactModel model) {
        holder.name.setText(model.getName());
        holder.email.setText(model.getEmail());

        Glide.with(holder.img.getContext())
                .load(model.getSurl())
                .placeholder(R.drawable.common_google_signin_btn_icon_dark)
                .circleCrop()
                .error(R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(holder.img.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true,1200)
                        .create();

                //dialogPlus.show();

                View view =dialogPlus.getHolderView();
                EditText name=view.findViewById(R.id.txtName);
                EditText email=view.findViewById(R.id.txtEmail);
                EditText surl=view.findViewById(R.id.txtImageUrl);

                Button btnUpdate=view.findViewById(R.id.btnUpdate);

                name.setText(model.getName());
                email.setText(model.getEmail());
                surl.setText(model.getSurl());
                dialogPlus.show();


                // enter new information of contact or customer , then update information to database
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object>map=new HashMap<>();
                        map.put("name",name.getText().toString());
                        map.put("email",email.getText().toString());
                        map.put("surl",surl.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("ChatUser")
                            .child(getRef(position).getKey()).updateChildren(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(holder.name.getContext(),"Data updated successfully.",Toast.LENGTH_SHORT).show();
                                    dialogPlus.dismiss();
                                }
                            })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull  Exception e) {
                                Toast.makeText(holder.name.getContext(),"Error when update.",Toast.LENGTH_SHORT).show();
                                  dialogPlus.dismiss();
                            }
                        });

                    }
                });
            }
        });


        // delete a customer
        holder.btnDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Are you sure to delete this customer");
                builder.setMessage("It will delete from Database");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("ChatUser")
                                .child(getRef(position).getKey()).removeValue();

                        Toast.makeText(holder.name.getContext(),"Cancel.",Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.name.getContext(),"Delete Cancelled.",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });

    }






    class myViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        TextView name,email;
        Button btnEdit,btnDelete;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img=(CircleImageView)itemView.findViewById(R.id.img1);
            name= (TextView) itemView.findViewById(R.id.nameText);
            email=(TextView)itemView.findViewById(R.id.emailText);

            btnEdit=(Button)itemView.findViewById(R.id.btnEdit);
            btnDelete=(Button)itemView.findViewById(R.id.btnDelete);

        }

    }

}
