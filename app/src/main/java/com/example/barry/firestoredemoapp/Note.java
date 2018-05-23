package com.example.barry.firestoredemoapp;

import com.google.firebase.firestore.Exclude;

public class Note {

    // 8.10 add a new string
    private String documentId;

    //1. create 2 fields as in current codes
    private String title;
    private String description;
    private int priority; // 9.4

    // 4. add an empty constructor
    public Note() {

        // public no-arg constructor needed
    }

    //2. create a constructor for the 2 fields
    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    //  8.10.4 we dont want this "DocumentId" to appear in our document in firebase as it will
    // be redundant since we already have as the name of the data there
    // so add exclude , this way it saves the doc id but doesnt show
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }


    //3. add getters (we don't need the setters for this ex)


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
