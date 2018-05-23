package com.example.barry.firestoreexample;

public class Note {

    //1. create 2 fields as in current codes
    private String title;
    private String description;

    // 4. add an empty constructor
    public Note() {

        // pulic no-arg constructor needed
    }

    //2. create a constructor for the 2 fields
    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    //3. add getters (we don't need the setters for this ex)


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
