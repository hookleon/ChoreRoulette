/*
  AddHouseMemberActivity.java
  ---------------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AddHouseMemberActivity runs when setting up household.
 * Allows users to add people to their 'household'.
 */
public class AddHouseMemberActivity extends AppCompatActivity{

    // Will display names of house members
    private List<Member> members = new ArrayList<>();;

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    //house id, this allows every member to store this id that is unique to their household
    private String houseID = UUID.randomUUID().toString();
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";  //Passes houseid to next activity so chores can be added to activity

    private MyAdapter adapter;

    /**
     * Displays member names when first opens
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house_member);

        // RecView stuff
        RecyclerView recView = (RecyclerView) findViewById(R.id.recView);
        LinearLayoutManager recLayout = new LinearLayoutManager(this);
        recView.setLayoutManager(recLayout);
        recView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyAdapter(members, this);
        recView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recView.setAdapter(adapter);
    }

    /**
     * Sets the name of a household member
     * @param view used for the event handling of the add member button.
     */
    public void addMember (View view) {
        // When clicked, the text will be taken and added as a name of a person in household
        // the newMember will take name from editMember and send it to Firebase
        EditText editMember = (EditText) findViewById(R.id.editMember);
        String name = editMember.getText().toString();
        String id = UUID.randomUUID().toString();

        members.add(new Member(name,id,houseID));
        members.get(members.size()-1).addChore("Nothing");
        adapter.notifyDataSetChanged();   //This updates the recyclerView
    }

    /**
     * Adds all members under the name of new household
     * @param view used for the event handling of the confirm button.
     */
    public void confirmMembers (View view) {
        //Adds all members under the name of new household
        EditText editHouse = (EditText) findViewById(R.id.editHouse);
        String house = editHouse.getText().toString();
        if (members.size() != 0) {
            mRef.child("groups").child(houseID).child("members").setValue(members);
            mRef.child("groups").child(houseID).child("name").setValue(house);

            for (int i = 0; i < members.size(); i++) {
                mRef.child("users").child(members.get(i).getID()).setValue(members.get(i));
            }

            // Moves to the next page where you pick chores
            Intent intent = new Intent(this, AddChoresActivity.class);
            intent.putExtra(HOUSE_ID, houseID);
            startActivity(intent);
        }
        else{
            Context context = getApplicationContext();
            CharSequence text = "No members please try again";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }
}