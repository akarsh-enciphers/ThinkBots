package com.example.thinkbots;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class QuizFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "QUIZ_FRAGMENT_LOG";
    private FirebaseFirestore firebaseFirestore;
    private  String quizId;

    //initializing firebase auth to  get the user id from there to store it in a field
    private FirebaseAuth firebaseAuth;
    private  String currentUserId;

    //UI Elements
    private  TextView quizTitle;
    private Button optionOneBtn;
    private Button optionTwoBtn;
    private Button optionThreeBtn;
    private Button optionFourBtn;
    private Button nextBtn;
    private ImageButton closeBtn;
    private TextView questionFeedback;
    private TextView questionText;
    private TextView questionTime;
    private ProgressBar questionProgress;
    private TextView questionNumber;




    //Firebase data
    private List<QuestionsModel> allQuestionsList = new ArrayList<>();
    private long totalQuestionsToAnswer = 0L;
    private List<QuestionsModel> questionsToAnswer = new ArrayList<>();
    private CountDownTimer countDownTimer;

    //to answer the question we will create new field called canAnswer and it will be of type boolean and set to false by default to prevent user from answering the question
    private boolean canAnswer = false;
    private int currentQuestion = 0;

    private  int correctAnswers = 0;
    private int wrongAnswers =0;
    private int notAnswered =0;

    public QuizFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        //UI Initialize
        quizTitle = view.findViewById(R.id.quiz_title);
        optionOneBtn = view.findViewById(R.id.quiz_option_one);
        optionTwoBtn = view.findViewById(R.id.quiz_option_two);
        optionThreeBtn = view.findViewById(R.id.quiz_option_three);
        optionFourBtn = view.findViewById(R.id.quiz_option_four);
        nextBtn = view.findViewById(R.id.quiz_next_btn);
        questionFeedback = view.findViewById(R.id.quiz_question_feedback);
        questionText = view.findViewById(R.id.quiz_question);
        questionTime = view.findViewById(R.id.quiz_question_time);
        questionProgress = view.findViewById(R.id.quiz_question_progress);
        questionNumber = view.findViewById(R.id.quiz_question_number);


        //get quizId
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizid();
        totalQuestionsToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();

        //Get all questions from the quiz
        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Questions")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    //we can get all the list of questions as
                    allQuestionsList = task.getResult().toObjects(QuestionsModel.class);
                    //we want to pick all questions from the whole list so we will create a new method "pickQuestions"
                    pickQuestions();
                    loadUI();
                }
                else{
                    //Error getting Questions
                    quizTitle.setText("Error : " + task.getException().getMessage());
                }
            }
        });

        //set button  click listeners for answer button
        optionOneBtn.setOnClickListener(this);
        optionTwoBtn.setOnClickListener(this);
        optionThreeBtn.setOnClickListener(this);
        optionFourBtn.setOnClickListener(this);

        nextBtn.setOnClickListener(this);
    }

    private void loadUI() {
        //to load the UI we will created a new method
        quizTitle.setText("Quiz Data Loaded");
        questionText.setText("Load First Question");

        //after the questions and ui loads we will enable the answer button for that we will create another method
        enableOptions();

        //Load First Question
        loadQuestion(1);
    }

    private void loadQuestion(int questNum) {
        //set question number
        questionNumber.setText(questNum + "");

        //Load Question Text
        questionText.setText(questionsToAnswer.get(questNum-1).getQuestion());

        //Load Options
        optionOneBtn.setText(questionsToAnswer.get(questNum-1).getOption_a());
        optionTwoBtn.setText(questionsToAnswer.get(questNum-1).getOption_b());
        optionThreeBtn.setText(questionsToAnswer.get(questNum-1).getOption_c());
        optionFourBtn.setText(questionsToAnswer.get(questNum-1).getOption_d());

        //Question loaded person can answer now
        canAnswer = true;
        currentQuestion = questNum;

        //Start Question Timer
        startTimer(questNum);

    }

    private void startTimer(int questionNumber) {

        //set timer text
        Long timeToAnswer = questionsToAnswer.get(questionNumber-1).getTimer();
        questionTime.setText(timeToAnswer.toString());

        //Show Timer progressbar
        questionProgress.setVisibility(View.VISIBLE);

        //Start Countdown
        countDownTimer = new CountDownTimer(timeToAnswer*1000, 10){

            @Override
            public void onTick(long millisUntilFinished) {
                //update time
                questionTime.setText(millisUntilFinished/1000 + "");

                //progress in percent
                Long percent = millisUntilFinished/(timeToAnswer*10);
                questionProgress.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                //time up, cannot answer question
                canAnswer = false;

                questionFeedback.setText("Time's UP! No Answer was submitted.");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                notAnswered++;
                showNextBtn();
            }
        };

        countDownTimer.start();
    }

    private void enableOptions() {
        optionOneBtn.setVisibility(View.VISIBLE);
        optionTwoBtn.setVisibility(View.VISIBLE);
        optionThreeBtn.setVisibility(View.VISIBLE);
        optionFourBtn.setVisibility(View.VISIBLE);

        //enable option buttons
        optionOneBtn.setEnabled(true);
        optionTwoBtn.setEnabled(true);
        optionThreeBtn.setEnabled(true);
        optionFourBtn.setEnabled(true);

        //hide feedback and next button
        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);
    }

    private void pickQuestions() {
        for (int i=0;i<totalQuestionsToAnswer; i++){
            int randomNumber = getRandomInteger(allQuestionsList.size(), 0);
            questionsToAnswer.add(allQuestionsList.get(randomNumber));
            allQuestionsList.remove(randomNumber);

            Log.d("QUESTIONS LOG","Question : " + i + " : " + questionsToAnswer.get(i).getQuestion());
        }
    }
    public static int getRandomInteger(int maximum, int minimum){
        return ((int) (Math.random()*(maximum - minimum))) +minimum;
    }

    @Override
    public void onClick(View v) {
        //inside this onclick method we can check which button is pressed using switch statement and comparing the view id
        switch (v.getId()){
            case R.id.quiz_option_one:
                verifyAnswer(optionOneBtn);
                break;
            case R.id.quiz_option_two:
                verifyAnswer(optionTwoBtn);
                break;
            case R.id.quiz_option_three:
                verifyAnswer(optionThreeBtn);
                break;
            case R.id.quiz_option_four:
                verifyAnswer(optionFourBtn);
                break;
            case R.id.quiz_next_btn:
                if (currentQuestion == totalQuestionsToAnswer){
                 //Load results
                 submitResults();
                }else {
                    currentQuestion++;
                    loadQuestion(currentQuestion);
                    resetOptions();
                }
                break;
        }
    }

    // inside this method we will add results in firestore in a new collection called results
    //this will be stored for each document and will have the user id as the document id for the result
    private void submitResults() {
        //to add data to the document we created a new  hashmap
        //this will have the key type as string and value type as object which means any type of value can be stored in this
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("Correct", correctAnswers);
        resultMap.put("wrong", wrongAnswers);
        resultMap.put("unanswered", notAnswered);

        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(currentUserId).set(resultMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Go to results page
                }else {
                    //show error
                }

            }
        });
    }

    private void resetOptions() {
        optionOneBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionTwoBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionThreeBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionFourBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));

        optionOneBtn.setTextColor(getResources().getColor(R.color.colorLightText, null));
        optionTwoBtn.setTextColor(getResources().getColor(R.color.colorLightText, null));
        optionThreeBtn.setTextColor(getResources().getColor(R.color.colorLightText, null));
        optionFourBtn.setTextColor(getResources().getColor(R.color.colorLightText, null));

        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);

    }

    private void verifyAnswer(Button selectedAnswerBtn) {

        //check the answer
        if (canAnswer){

            //Set Answer Btn Text color to black
            selectedAnswerBtn.setTextColor(getResources().getColor(R.color.colorDark, null));

            if (questionsToAnswer.get(currentQuestion-1).getAnswer().equals(selectedAnswerBtn.getText())){

                //correct answer
                correctAnswers++;
                selectedAnswerBtn.setBackground(getResources().getDrawable(R.drawable.correct_answer_btn_bg, null));

                //Set Feedback Text
                questionFeedback.setText("Correct Answer");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary, null));

            }else{
                //wrong answer
                wrongAnswers++;
                selectedAnswerBtn.setBackground(getResources().getDrawable(R.drawable.wrong_answer_btn_bg, null));
                //Set Feedback Text
                questionFeedback.setText("Wrong Answer \n \n Correct Answer : " + questionsToAnswer.get(currentQuestion-1).getAnswer());
                questionFeedback.setTextColor(getResources().getColor(R.color.colorAccent, null));

            }
            //set can answer to false
            canAnswer = false;

            //Stop the timer
            countDownTimer.cancel();

            //Show next button
            showNextBtn();

        }
    }

    private void showNextBtn() {
        if (currentQuestion == totalQuestionsToAnswer){
            nextBtn.setText("Submit Results");

        }
        questionFeedback.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        nextBtn.setEnabled(true);
    }


}