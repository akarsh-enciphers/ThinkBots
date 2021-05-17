package com.example.thinkbots;

import com.google.firebase.firestore.DocumentId;

public class QuizListModel {

    //annotation "document ID" so if u type in document id right before the quiz id string then the firebase is going to attach the document id to your string


    @DocumentId
    private String quiz_id;
    private String name,desc,image,level,visiblity;
    private long questions;
    //build getter and setter for all these fields
    //buid constructors for all the fields

    //we need empty constructor for firebase
    public QuizListModel(){}


    public QuizListModel(String quiz_id, String name, String desc, String image, String level, String visiblity, long questions) {
        this.quiz_id = quiz_id;
        this.name = name;
        this.desc = desc;
        this.image = image;
        this.level = level;
        this.visiblity = visiblity;
        this.questions = questions;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVisiblity() {
        return visiblity;
    }

    public void setVisiblity(String visiblity) {
        this.visiblity = visiblity;
    }

    public long getQuestions() {
        return questions;
    }

    public void setQuestions(long questions) {
        this.questions = questions;
    }
}
