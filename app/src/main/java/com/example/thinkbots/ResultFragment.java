package com.example.thinkbots;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ResultFragment extends Fragment {

    private NavController navController;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String quizId;
    private String currentUserId;

    //UI
    private TextView resultCorrect;
    private TextView resultWrong;
    private TextView resultMissed;
    private TextView resultPercent;
    private ProgressBar resultProgress;
    private Button resultHomeBtn;


    public ResultFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        firebaseAuth = FirebaseAuth.getInstance();

        //get user id
        if(firebaseAuth.getCurrentUser() != null){
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }
        else {
            //go back to home page
        }

        //initializing firebasefirestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        quizId = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();

        //initializing UI elements
        resultCorrect = view.findViewById(R.id.results_correct_text);
        resultWrong = view.findViewById(R.id.results_wrong_text);
        resultMissed = view.findViewById(R.id.results_missed_text);
        resultHomeBtn = view.findViewById(R.id.results_home_btn);
        resultPercent = view.findViewById(R.id.results_percent);
        resultProgress = view.findViewById(R.id.results_progress);
        resultHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_resultFragment_to_listFragment);
            }
        });


        //Get result
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        firebaseFirestore.collection("QuizList").document(quizId).collection("Results")
                                .document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull  Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot result = task.getResult();

                                    Long correct = result.getLong("correct");
                                    Long wrong = result.getLong("wrong");
                                    Long missed = result.getLong("unanswered");

                                    resultCorrect.setText(correct.toString());
                                    resultWrong.setText(wrong.toString());
                                    resultMissed.setText(missed.toString());

                                    //calculate progress
                                    Long total = correct + wrong + missed;

                                    Long percent = (correct*100)/total;


                                    resultPercent.setText(percent + "%");
                                    resultProgress.setProgress(percent.intValue());
                                }
                                else{
                                    // Document doesn't exist and result should stay N/A
                                }

                            }
                        });

                    }
                }
        ).start();
    }
}