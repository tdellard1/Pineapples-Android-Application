package com.twilio.voice.quickstart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestDatabaseActivity extends AppCompatActivity {

    private EditText child;
    private EditText value;
    private Button addToDatabase;
    private Button searchDatabase;
    private FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);

        child = findViewById(R.id.et_child);
        value = findViewById(R.id.et_value);
        addToDatabase = findViewById(R.id.btn_database);
        searchDatabase = findViewById(R.id.btn_search);
        database = FirebaseDatabase.getInstance();


        searchDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = database.getReference("base");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String dvd = "dvd";
                        String ambig;
                        int WITHIN_DATABASE = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String inDatabase = ds.getValue().toString();
                            if (inDatabase == dvd) {
                                WITHIN_DATABASE += 1;
                            } else { WITHIN_DATABASE += 0;}
                        }
                        if (WITHIN_DATABASE < 1) {
                            ambig = dataSnapshot.child(dvd).getValue().toString();
                            Toast.makeText(TestDatabaseActivity.this, ambig + " Is in database", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        addToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String childString = child.getText().toString();
                String valueString = value.getText().toString();
                DatabaseReference myRef = database.getReference(childString);
                myRef.setValue(valueString);
            }
        });






    }
}
