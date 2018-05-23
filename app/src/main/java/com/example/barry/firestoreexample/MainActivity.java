package com.example.barry.firestoreexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * https://youtu.be/MILE4PVx1kE?list=PLrnPJCHvNZuBf5KH4XXOthtgo6E4Epjl8
 * For more infor
 * http://firebase.google.com/docs/android/setup
 * 1 create a new project
 * 2. Connect to Firebase Firestore:
 *      Tools > Firebase > Analytics > Log an Analytic event
 *      > Connect to Firebase
 *      > Add Analytics to your app
 * 3. Add firestore to gradle
 * 4. Open the project in Firestore > Database > Cloud Firestore get Started >
 *      Start in Test Mode for dev (Must Change to Locked Mode for Production)
 *
 * 5. Add the views in the xml file
 * 6. declare the views here and init in onCreate
 * RUN and see, it works
 *
 * // part 2 retrieving the data
 * https://youtu.be/di5qmolrFVs?list=PLrnPJCHvNZuBf5KH4XXOthtgo6E4Epjl8
 *
 * 2.1 Add a load btn to the xml file
 * 2.2. Let's define a ref to the note we stored so that we can always use it instead for typing everytime
 *      - define
 *          private DocumentReference noteRef = db.document("Notebook/My First Note");
 *          which will create a doc "My First Note" in a collection "Notebook"
 *
 *      -  and replace db.collection("Notebook").document("My First Note).
 *      with
 *          noteRef
 *2.3 def a void loadNote to listen for clicks
 *      - get the noteRef and add the success and failure listeners just like in save btn
 *
 * 2.4 declare the btn, init and define the click listener in java
 *      RUN and see, it works
 *
 * Part 3 GET DATA REALTIME
 * https://youtu.be/LfkhFCDnkS0?list=PLrnPJCHvNZuBf5KH4XXOthtgo6E4Epjl8
 * 3.1. we don't need to listen for this doc all the time, that will waste our data,
 *      - so override onStart() below onCreate()
 *      - add a snapshot listener on our noteRef
 *      - do the same if as in loadNote (just copy same if statement to the
 *      snapshotListener without the if part)
 *      - also check for error before loading, put this before the above code
 *          if (e != null) {
                 Toast.makeText(MainActivity.this,
                 "Error while loading", Toast.LENGTH_SHORT).show();
                 Log.d(TAG, e.toString());
 }
 * 3.2 For the app lifecircle we also need to detach(); add an onStop() and onDestroy() methods
 *      but to make things easy, just pass "this" to the snapShotListener as the 1st argument,
 *      while leaving the eventListener as the 2nd arg
 *      this will automatically detach the listener at the appropriate time
 *
 * Part 4 UPDATE DATA
 * https://youtu.be/TBr_5QH1EvQ?list=PLrnPJCHvNZuBf5KH4XXOthtgo6E4Epjl8
 *
 * 4.1 add an update btn to the xml file
 * 4.2 AND CREATE A METHOD to update in java, put it above the loadNote()
 *
 * part 5 DELETE DOC
 * https://youtu.be/1gerxvFAGio
 *
 * 1. add the delete btns to main.xml
 * 2. create the delete description method
 * 3. create the delete note method
 *
 * part 6 Video 7: using a Custom Java class (Instead of a Hashmap)
 * https://youtu.be/jJnm3YKfAUI
 *
 * 1. Create a new java class "Note"
 * 2. Add 2 fields "Title and Description" just as in our current code
 * 3. add a constructor passing in the 2 fields
 * 4. add the getters (setters not needed for this ex)
 * 5. add an empty constructor (required)
 * 6. come back here and replace the hashMaps
 *      In saveNote, we still need the 2 strings to retrieve our data, so keep them
 *      - replace the hashMap with the Note class we just created
 *          Note note = new Note(title, description);
 *          the arg pass must be the same as in the Note class, case sensitive
 *          notice that note is still the same that we pass to firestore, so our result will be same
 * 7. Go to loadNote()
 *      - Replace the 2 strings with a new Note def, pass the documentSnapshot to an obj of Note class
 *      - use the getter method to get the strings for the 2 fields
 *      - then copy the whole codes in the "if" to onStart() if ()
 * RUN and see that the app performs the same task as before
 *
 */


public class MainActivity extends AppCompatActivity {

    //1
    private static final String TAG = "MainActivity";  // tag for debugging messages

    //2 keys for saving thing to the db (db uses key value pairs for saving doc)
    // lets use strings instead of hardcoding them
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESC = "description";

    //3 declare the views
    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewdata; // 2.4.0


    //4 get an instance of the firestore ref
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 2.2.0 define a ref to the note we stored so that we can always use it
    private DocumentReference noteRef = db.document("Notebook/My First Note");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //5 init the views
        editTextTitle = findViewById(R.id.et_text_title);
        editTextDescription = findViewById(R.id.et_text_desc);
        textViewdata = findViewById(R.id.tv_view_data); // 2.4.1
    }

    // 3.1.0 onStart()
    @Override
    protected void onStart() {
        super.onStart();

        //3.1.1 add a listener for our noteRef. Same as in loadNote()
        // this will load the doc automatically
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                //3.1.3 check for error before loading
                if (e != null) {
                    Toast.makeText(MainActivity.this,
                            "Error while loading", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }

                //3.1.2 check if the doc exist
                if (documentSnapshot.exists()) {

                   /* // get the values from the doc. snapshot to string
                    String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESC);

                    // display the data to the text view
                    textViewdata.setText("Title: " + title + "\n" + "Description: " + description);*/
                   // part 6.7.3 Replace the above codes with the new one copied from loadNote()
                    Note note = documentSnapshot.toObject(Note.class);

                    String title = note.getTitle();
                    String description = note.getDescription();

                    textViewdata.setText("Title: " + title + "\n" + "Description: " + description);
                }
                else {
                    // 5.3.2 this part works with the delete note
                    // after we delete, we must do this, otherwise the app will try to get this data
                    // and when the data is not found could result to null pointer crash
                    textViewdata.setText("");
                }

            }
        });
    }

    //6 SAVE DATA TO THE STORE - the SAVE onClick listener
    public void saveNote(View view) {

        //7 extract the values from the edit text field to save to db
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        //8 put the doc into a container (Hashmap) to put it into the db
        // the hash map is a specific implimentation of the Map interface
        /*Map<String, Object> note = new HashMap<>();
        note.put(KEY_TITLE, title);
        note.put(KEY_DESC, description);*/

        // part 6.6 Replace above hashMap with:
        Note note = new Note(title, description);

        //9 pass the note map to the db . We can create as ff or let firestore create for us
        //db.document("Notebook/My First Note")..... a short hand can be used here
        /*db.collection("Notebook") // this creates collection called Notebook
                .document("My First Note") // this create a doc My First Note in the notebook*/

        // 2.2..1 Replace the above with the newly define noteRef
        noteRef
                .set(note)  // set the note map to the doc
                .addOnSuccessListener(new OnSuccessListener<Void>() { // check if every thing went well
                    @Override
                    public void onSuccess(Void aVoid) {

                        // toast
                        Toast.makeText(MainActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() { // or if things failed
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // toast
                        Toast.makeText(MainActivity.this, "Error! ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                }); // Now run and see that it works

    }

    //UPDATE DATA
    // 4.2
    public void updateDescription(View view) {

        // get the description snapshot to string
        String description = editTextDescription.getText().toString();

       /* update method 1 - this updates existing doc and creates a new one if it didn't exist
       uncomment this, comment out the one below, run and see
       // create a new hashmap for this
        Map<String, Object> note = new HashMap<>();
        note.put(KEY_DESC, description);
        // RUN , write a description and see that it updates

        //override our value
        noteRef.set(note, SetOptions.merge());
        // this will also create a new doc if it doesn't exist already
        // if the doc already exists, it will override the discription and leave the title untouched
        */

        //update method 2: Another method that does not create a new doc.
        // this does not need to HashMap so comment out the Map and noteRef.set(), then add the ff
        noteRef.update(KEY_DESC, description);
        // RUN AND SEE
    }

    //5.2 DELETE DESCRIPTION
    public void deleteDescription(View view) {

       /* Delete method 1: long version
        //5.2.1 create a hash map for the values
        Map<String, Object> note = new HashMap<>();
        note.put(KEY_DESC, FieldValue.delete());

        //5.2.2 now pass the note to firestore ref to delete
        noteRef.update(note); // this deletes the description*/

        //5.2.3 Delete method 2: short version,  easier, with less codes
        noteRef.update(KEY_DESC, FieldValue.delete());
        // we can also add onSuccess and onFailure listeners to get feedback
    }

    //5.3 DELETE NOTE
    public void deleteNote(View view) {

        //5.3.1 easy, just delete the noteRef
        // must then setText to "" in onStart our app doesn't crash when data cannot be seen again (see 5.3.2 in onstart)
        noteRef.delete();
        // we can also add onSuccess and onFailure listeners to get feedback
    }
    //RETRIEVE DATA FROM DB
    public void loadNote(View view) {

        //2.4.3 get the ref and add listeners
        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // the document snapShot above contains our data as long as the data exits

                //2.4.4 check if the doc exist
                if (documentSnapshot.exists()) {

                    //2.4.6 get the values from the doc. snapshot to string
                   /* String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESC);
                    // we can also get our doc using
                    //Map<String, Object> note = documentSnapshot.getData();*/

                   // part 6.7.1 Replace above 2 Strings with:
                    // this time we don't create new obj, we jus pass in the documentSnapshot
                    Note note = documentSnapshot.toObject(Note.class);

                    // part 6.7.2 use the getter method to get the strings for the 2 fields
                    // then copy the whole codes in this "if" to onStart()
                    String title = note.getTitle();
                    String description = note.getDescription();

                    //2.4.7 display the data to the text view
                    textViewdata.setText("Title: " + title + "\n" + "Description: " + description);
                }
                else { //2.4.5 if not
                    Toast.makeText(MainActivity.this,
                            "Document does not exist", Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // 2.4.8 if we failed
                Toast.makeText(MainActivity.this,
                        "Error ", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        }); // Now run and see
    }
}
