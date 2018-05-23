package com.example.barry.firestoredemoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This app is a continuation of the FirestoreExample app
 *This app will allow more posts to the stored unlike the other app that takes only single post
 * and deletes earlier posts
 *
 * Part 8 Retrieve Multiple Notes
 * https://youtu.be/Bh0h_ZhX-Qg
 *
 * 1. Let's delete other btns leaving only the save and load btns from the xml file
 * 2. Change the text in save btn to Add and onClick to addNote
 *          change the onClick in load to loadNotes as we will be adding many notes
 * 3. Put the textView into a nested scrowllView so it scrowlls when we have many notes
 *
 * 4. Create a new reference "CollectionReference " in main.java
 * 5. Change saveNote to addNote to correspond with our xml on click
 *      delete the firestore ref part, we will not use that anymore
 * 6. Go ahead, delete the updateDescription, deleteDescription, and deleteNote which we removed from xml
 *
 * 7. Change loadNote to loadNotes and delete all content
 *      replace content
 *
 * 8. In onStart(), remove the ref part (all codes)
 *      RUN. it works showing a list
 *
 * 9. Let's get instant result in REALTIME
 *      - add snapshot listener to onStart()
 *
 * 10. tO SAVE THE ID so we can make ref to it later
 *      - go to Note class, add a new string documentId or any name of our choice
 *      - add it to the constructor
 *      - and do the getter and setter too
 *      - make sure to @Exclude to getDocumentId
 *
 * 11. When we retrieve our note we want the id as well so go to loadNotes()
 *      - add note.setDocid()
 *      - define a string var
 *      - add the string to data
 * 12. Do the same in our snapshot listener
 *      RUN
 *
 * -------------------------------------------------------------
 * Part 9 : SIMPLE QUERIES
 * https://youtu.be/X5AGMpMV7Ks
 *
 * 1. ADD AN edittext to our xml file, hint priorty, input type number
 * 2. create the editextPriority here in java and init it
 * 3. in addNotes, check if text was entered into the priority
 * 4. add priority field to Note.java
 *      - add it to the constructor
 *      - add getters and setters
 * 5. in addNote, add this to the Note note = --- as a param
 * 6. add this priority to loadNotes
 *      and copy this to snapshotListener
 *
 * RUN and see
 *
 * 7. Now we can fetch data depending on the priority
 *      In loadNotes,
 *      - let's add
 *      .whereEqualTo("priority", 2) to the notebookRef, RUN AND SEE
 *      .whereGreaterThanOrEqualTo("priority", 2)  RUN AND SEE
 *      There are many filters we can choose as we like
 *
 * 8. to have our list ordered in descending order,
 *      add .orderBy("priority", Query.Direction.DESCENDING)
 *
 * 9. To filter for the number of notes to show
 *      add .limit(5) to after orderBy ( limit(5) will display 5 notes), change as needed
 * NOTE: WE CAN ALSO ADD THIS PRIORITY TO OUR SNAPSHOT LISTENER THE SAME WAY
 * RUN.
 *
 * Part 10 : Compound query
 * 1. add on failure listener to our notebookRef() in loadNotes
 *
 * part 11: MERGE QUERIES
 * https://youtu.be/lneYmqe0qRI
 *
 * Firestore does not have an "OR" ,  "not equal to" operator, so we cannot create
 * a single query combining 2 operators where the priority is eg 2 or 5, 3 != 7 etc.
 * We have to handle them differently
 * For to handle a case where priority is not 2 that means "less than 2" or "greater than 2" so:
 * We will create a query "WhereLessThan()
 * Then create another WhereGreaterThan()
 * Let's try this out, go to loadNote,
 * 1. change the query to "WhereLessThan()
 * 2. delete orderBy("title")
 * 3. Cut out the whole content of our onSuccessListener
 * 4. delete the onSuccess and onFailureListeners
 * 5. put the ; after get()
 * 6. cursor over the get() and see that it returns the Task
 * 7. now lets give the whole set of values to a var task 1
 *
 * Part 12 PAGINATION
 *
 * 1. Remove KEY_TITLE, KEY_DESC , TAG, Notebook/My First Note ref
 * 2. Delete onStart(), we dont want to start anything when we start the app
 *    we only load when we click the loadBtn
 * 3. In the import section top, delete all the imports that show light gray
 *      they're not useful anymore (this was auto removed for me)
 *
 * 4. Now in loadNote() method,
 * we want to re-write the codes so delete the content
 *
 *
 *  */
public class MainActivity extends AppCompatActivity {
    //1



    //3 declare the views
    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewdata; // 2.4.0
    private EditText  editTextPriority; // 9.2.1


    //4 get an instance of the firestore ref
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //8.4 create a new ref
    private CollectionReference notebookRef = db.collection("Notebook");

    //12.00
    private DocumentSnapshot lastResult; // for pagination


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //5 init the views
        editTextTitle = findViewById(R.id.et_text_title);
        editTextDescription = findViewById(R.id.et_text_desc);
        textViewdata = findViewById(R.id.tv_view_data); // 2.4.1
        editTextPriority = findViewById(R.id.edit_text_priority); // 9.2.2
    }


    //6 SAVE DATA TO THE STORE - the SAVE onClick listener
    public void addNote(View view) {

        //7 extract the values from the edit text field to save to db
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        //9.3 if text entered in the priority filed
        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0"); // if nothing entered set text to 0
        }
        // 9.3.1 get the text entered in the priority
        int priority = Integer.parseInt(editTextPriority.getText().toString());


        // part 6.6 Replace above hashMap with:
        Note note = new Note(title, description, priority); // 9.5 add priority here

        //8.5.2 replace with the new notebookref
        notebookRef.add(note); // for production apps we need to add on success / failure also for feedback
    }


    //RETRIEVE DATA FROM DB
    public void loadNotes(View view) {

        // FOR PAGINATION

        //12.01 create Query for pagination
        Query query; // select the firestore one

        // check if the query has data
        if (lastResult == null) { // if empty, add data
            query = notebookRef.orderBy("priority")
                    .limit(3);
        }
        else { // if it has data, tell where to sart
            query = notebookRef.orderBy("priority")
                    .startAfter(lastResult)  // tell it where to start
                    .limit(3);
        }

        // retrieve a range of doc
        //replace notebookRef.orderBy("priority").startAt(3)  with query (we already defined above)
        //notebookRef.orderBy("priority")
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // create the empty string for the data
                        String data = "";

                        // iterate thru the doc snapshot
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            // build the string, create the Note object
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());

                            // Retrieve the data
                            String documentId = note.getDocumentId();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();

                            // pass the fields to the empty string
                            // add the string to data
                            data += "ID: " + documentId         //8.11.3 add the ID to data
                                    + "\nTitle: " + title + "\nDescription: " + description
                                    + "\nPriority: " + priority + "\n\n";   //9.6.1 add priority to our data
                        }

                        // make sure there is at least one item in the snapsot before pagination
                        // otherwise if there is no snapshot, the lastResult line will
                        // throw exception and the app will crash
                        if (queryDocumentSnapshots.size() > 0) {
                            // put a line at the bottom of the data set
                            data += "_______________________\n\n";

                            // set the text to the textView
                            //textViewdata.setText(data);
                            // change settext() to append()
                            textViewdata.append(data);

                            // Build the string for pagination
                            lastResult = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }

                    }
                });

        // retrieve specific doc snapshot
        /*notebookRef.document("JvcJLOry9cHsTlXIeNO3") // copied the id of a doc to retrieve from firestore
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        // retrieve a range of doc
                        notebookRef.orderBy("priority")
                                .startAt(documentSnapshot)  //to get retrieve a doc snapshot, use startAt(documentSnapshot)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        // create the empty string for the data
                                        String data = "";

                                        // iterate thru the doc snapshot
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                                            // build the string, create the Note object
                                            Note note = documentSnapshot.toObject(Note.class);
                                            note.setDocumentId(documentSnapshot.getId());

                                            // Retrieve the data
                                            String documentId = note.getDocumentId();
                                            String title = note.getTitle();
                                            String description = note.getDescription();
                                            int priority = note.getPriority();

                                            // pass the fields to the empty string
                                            // add the string to data
                                            data += "ID: " + documentId         //8.11.3 add the ID to data
                                                    + "\nTitle: " + title + "\nDescription: " + description
                                                    + "\nPriority: " + priority + "\n\n";   //9.6.1 add priority to our data
                                        }

                                        // set the text to the textView
                                        textViewdata.setText(data);

                                    }
                                });

                    }
                });*/

       /* // retrieve a range of doc
        notebookRef.orderBy("priority")
                .orderBy("title") // can run multiple query, remember to pass this in startAt()
                .startAt(3, "Title2")  // startAt will include that num, startAfter() will exclude that num
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // create the empty string for the data
                        String data = "";

                        // iterate thru the doc snapshot
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            // build the string, create the Note object
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());

                            // Retrieve the data
                            String documentId = note.getDocumentId();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();

                            // pass the fields to the empty string
                            // add the string to data
                            data += "ID: " + documentId         //8.11.3 add the ID to data
                                    + "\nTitle: " + title + "\nDescription: " + description
                                    + "\nPriority: " + priority + "\n\n";   //9.6.1 add priority to our data
                        }

                        // set the text to the textView
                        textViewdata.setText(data);

                    }
                });*/
    }




   /* MERGE QUERIES

   // 3.1.0 onStart()
    @Override
    protected void onStart() {
        super.onStart();

        // 8.8 remove all codes and RUN. It works!

        // To get instant realtime data, add snapshot listener
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                // take care of firestore error e
                if (e != null) {
                    return;
                }

                // create empty string
                String data = "";

                // iterate thru the snapshot
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    // recreate the note
                    Note note = documentSnapshot.toObject(Note.class);

                    //8.12.1
                    note.setDocumentId(documentSnapshot.getId());

                    // 8.12.2 define a string var
                    String documentId = note.getDocumentId();

                    // get the title and desc
                    String title = note.getTitle();
                    String description = note.getDescription();

                    int priority = note.getPriority(); // 9.6.2

                    // pass the fields to the empty string
                    //8.12.3 add the string to data
                    data += "ID: " + documentId         //8.11.3 add the ID to data
                            + "\nTitle: " + title + "\nDescription: " + description
                            + "\nPriority: " + priority + "\n\n";   //9.6.1 add priority to our data

                    // we can now get any document simply by writing
                    *//*notebookRef.document(documentId)
                            .update()
                            .delete()
                            or whatever we want *//*
                }

                // pass the data to the textView to display
                textViewdata.setText(data);
                // RUN AND SEE The Instant data
            }
        });
    }

    //6 SAVE DATA TO THE STORE - the SAVE onClick listener
    public void addNote(View view) {

        //7 extract the values from the edit text field to save to db
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        //9.3 if text entered in the priority filed
        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0"); // if nothing entered set text to 0
        }
        // 9.3.1 get the text entered in the priority
        int priority = Integer.parseInt(editTextPriority.getText().toString());


        // part 6.6 Replace above hashMap with:
        Note note = new Note(title, description, priority); // 9.5 add priority here

        //8.5.2 replace with the new notebookref
        notebookRef.add(note); // for production apps we need to add on success / failure also for feedback



    }


    //RETRIEVE DATA FROM DB
    public void loadNotes(View view) {

        //11.0 change the query to WhereLessThan()
        // 11.7 give the value of our notebookRef to task1
        Task task1 = notebookRef.whereLessThan("priority", 2)  // 9.7.1 add priority
                .orderBy("priority") // 10.4
                .get(); //11.6 cursor over, see that it gets Task

        // 11.8 create task2
        Task task2 = notebookRef.whereGreaterThan("priority", 2)
                .orderBy("priority")
                .get();

        // 11.9 combine the 2 tasks in a new var Tasks(note the s, Tasks not Task)
        *//*Task allTasks  = Tasks.whenAllSuccess(task1, task2) // Tasks.. has many types that we can select
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() { // add onSuccessListener
            @Override
            public void onSuccess(List<Object> objects) {

            }
        });*//*
        //11.9 OR
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                // here we get returned a list of query snapshot in the order passed: task1, task2,....

                // paste the string we cut before here
                String data = ""; // the var data can be called anything

                // this query was for single snapshot, now we have more than one so
                // we put the old for loop into another for loop to handle the diff cases
                //give the var name as "queryDocumentSnapshots", the one showing red
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                        Note note = documentSnapshot
                                .toObject(Note.class);  // may be in a recycler for production apps

                        //8.11.1
                        note.setDocumentId(documentSnapshot.getId());

                        // 8.11.2 define a string var
                        String documentId = note.getDocumentId();

                        //8.7.5  assign the strings to get our params
                        String title = note.getTitle();
                        String description = note.getDescription();
                        // 9.6.0 add the priority to loadNotes
                        int priority = note.getPriority();

                        //8.7.6 add these 2 fields to the string data
                        data += "ID: " + documentId         //8.11.3 add the ID to data
                                + "\nTitle: " + title + "\nDescription: " + description
                                + "\nPriority: " + priority + "\n\n";   //9.6.1 add priority to our data

                    }
                }

                //8.7.7 put the data to the textView
                textViewdata.setText(data);

            }
        });
*/

          /*
                //1010101010101010101010 Part 10 101010101010101010

        //8.7.2 Delete all content and replace with
        // 10.0 this query work only on simple (single) query, but will not work for
        // compound (2 or more) queries, add another query see 10.1
        notebookRef.whereGreaterThanOrEqualTo("priority", 2)  // 9.7.1 add priority
                *//* .whereEqualTo("title", "Aa") // 10.0
                 .orderBy("priority", Query.Direction.DESCENDING) // 9.8*//*
                //.limit(3)    // how many notes to show // 9.9, // 10.2 delete limit
                .orderBy("priority") // 10.4
                .orderBy("title")  // 10.4 Run again, same problem, follow the link, create Index for ascending order
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //8.7.3 create an empty string, then assign the strings in the for loop
                        String data = ""; // the var data can be called anything

                        //8.7.4 to get our single doc from the QuerySnapshot we reiterate thru it
                        // we don't need to check for existence as a QueryDocumentsnapshot always exists
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            Note note = documentSnapshot
                                    .toObject(Note.class);  // may be in a recycler for production apps

                            //8.11.1
                            note.setDocumentId(documentSnapshot.getId());

                            // 8.11.2 define a string var
                            String documentId = note.getDocumentId();

                            //8.7.5  assign the strings to get our params
                            String title = note.getTitle();
                            String description = note.getDescription();
                            // 9.6.0 add the priority to loadNotes
                            int priority = note.getPriority();

                            //8.7.6 add these 2 fields to the string data
                            data += "ID: " + documentId         //8.11.3 add the ID to data
                                    + "\nTitle: " + title + "\nDescription: " + description
                                    + "\nPriority: " + priority + "\n\n";   //9.6.1 add priority to our data

                        }

                        //8.7.7 put the data to the textView
                        textViewdata.setText(data);

                    }
                })  //10.3 add on failure listener and run.
                //Add data work, but when we click load, As expected, didn't work,
                // in log there is a link, follow the link, Click Create Index, Run again
                // compound queries require Index, can be created manually in fb: Database > INDEX > ADD ANDEX
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });*/
        //1010101010101010101010 End Part 10 101010101010101010



    /* part 1 to part 9
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
    private EditText  editTextPriority; // 9.2.1


    //4 get an instance of the firestore ref
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //8.4 create a new ref
    private CollectionReference notebookRef = db.collection("Notebook");

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
        editTextPriority = findViewById(R.id.edit_text_priority); // 9.2.2
    }

    // 3.1.0 onStart()
    @Override
    protected void onStart() {
        super.onStart();

        // 8.8 remove all codes and RUN. It works!

        // To get instant realtime data, add snapshot listener
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                // take care of firestore error e
                if (e != null) {
                    return;
                }

                // create empty string
                String data = "";

                // iterate thru the snapshot
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    // recreate the note
                    Note note = documentSnapshot.toObject(Note.class);

                    //8.12.1
                    note.setDocumentId(documentSnapshot.getId());

                    // 8.12.2 define a string var
                    String documentId = note.getDocumentId();

                    // get the title and desc
                    String title = note.getTitle();
                    String description = note.getDescription();

                    int priority = note.getPriority(); // 9.6.2

                    // pass the fields to the empty string
                    //8.12.3 add the string to data
                    data += "ID: " + documentId         //8.11.3 add the ID to data
                            + "\nTitle: " + title + "\nDescription: " + description
                            + "\nPriority: " + priority + "\n\n";   //9.6.1 add priority to our data

                    // we can now get any document simply by writing
                    *//*notebookRef.document(documentId)
                            .update()
                            .delete()
                            or whatever we want *//*
                }

                // pass the data to the textView to display
                textViewdata.setText(data);
                // RUN AND SEE The Instant data
            }
        });
    }

    //6 SAVE DATA TO THE STORE - the SAVE onClick listener
    public void addNote(View view) {

        //7 extract the values from the edit text field to save to db
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        //9.3 if text entered in the priority filed
        if (editTextPriority.length() == 0) {
            editTextPriority.setText("0"); // if nothing entered set text to 0
        }
        // 9.3.1 get the text entered in the priority
        int priority = Integer.parseInt(editTextPriority.getText().toString());


        // part 6.6 Replace above hashMap with:
        Note note = new Note(title, description, priority); // 9.5 add priority here

        //8.5.2 replace with the new notebookref
        notebookRef.add(note); // for production apps we need to add on success / failure also for feedback



    }


    //RETRIEVE DATA FROM DB
    public void loadNotes(View view) {

        //8.7.2 Delete all content and replace with
        notebookRef.whereGreaterThanOrEqualTo("priority", 2)  // 9.7.1 add priority
                .orderBy("priority", Query.Direction.DESCENDING) // 9.8
                .limit(3)    // how many notes to show // 9.9
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //8.7.3 create an empty string, then assign the strings in the for loop
                        String data = ""; // the var data can be called anything

                        //8.7.4 to get our single doc from the QuerySnapshot we reiterate thru it
                        // we don't need to check for existence as a QueryDocumentsnapshot always exists
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            Note note = documentSnapshot
                                    .toObject(Note.class);  // may be in a recycler for production apps

                            //8.11.1
                            note.setDocumentId(documentSnapshot.getId());

                            // 8.11.2 define a string var
                            String documentId = note.getDocumentId();

                            //8.7.5  assign the strings to get our params
                            String title = note.getTitle();
                            String description = note.getDescription();
                            // 9.6.0 add the priority to loadNotes
                            int priority = note.getPriority();

                            //8.7.6 add these 2 fields to the string data
                            data += "ID: " + documentId         //8.11.3 add the ID to data
                                    + "\nTitle: " + title + "\nDescription: " + description
                                    + "\nPriority: " + priority + "\n\n";   //9.6.1 add priority to our data

                        }

                        //8.7.7 put the data to the textView
                        textViewdata.setText(data);

                    }
                });
    }*/
}
