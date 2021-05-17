package com.example.thinkbots;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class QuizListViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {

    //mutablelivedata will help us to keep the data in real time
    private MutableLiveData<List<QuizListModel>> quizListModelData = new MutableLiveData<>();

    //mutuablelivedata class is a subclass of livedata but it contains extra functions like set value and post value which we will use in our quizListDataMethod

    public LiveData<List<QuizListModel>> getQuizListModelData() {
        return quizListModelData;
    }


    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);

    //once quizlistviewmodel is initialised it is going to call for getquizdata method which is in firebaserepo.java
    //as soon as getquizdata in firebaserepo.java will return any result it will call it own firestore task complete interface that means both the below methods will be called
    public  QuizListViewModel()
    {
        firebaseRepository.getQuizData();

    }

    @Override
    public void quizListDataAdded(List<QuizListModel> quizListModelList) {
        quizListModelData.setValue(quizListModelList);

    }

    @Override
    public void onError(Exception e) {

    }
}
