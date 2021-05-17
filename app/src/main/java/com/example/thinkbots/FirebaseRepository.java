package com.example.thinkbots;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class FirebaseRepository {

    private  OnFirestoreTaskComplete onFirestoreTaskComplete;

    //new variable "firebase firestore"
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    // CollectionReference which will help us to query the data.....we are also passing the name of our collection which is "QuizList"
    private CollectionReference quizRef = firebaseFirestore.collection("QuizList");

    public FirebaseRepository(OnFirestoreTaskComplete onFirestoreTaskComplete){
        this.onFirestoreTaskComplete =  onFirestoreTaskComplete;
    }

    // now we can query the data in seperate method ...we gonna choose public method so that it is accessible for our QuizlistViewModel class.
    // "getQuizData" and this method will be of type void since this is not going to return anything.
    // but it will give us all result in Task<QuerySnapshot>. so we can check if task is successfull or not.

    public void getQuizData(){
        quizRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    onFirestoreTaskComplete.quizListDataAdded(task.getResult().toObjects(QuizListModel.class));
                }
                else{
                    onFirestoreTaskComplete.onError(task.getException());
                }

            }
        });
    }

    // Since we dont have any UI attached to our firebaserepository , we can't actually show anything to user.
    // so to sen data from our firebaserepository to ViewModel class we need to create an interface

    public interface OnFirestoreTaskComplete {
        void quizListDataAdded(List<QuizListModel> quizListModelList);
        void onError(Exception e);
    }
}
