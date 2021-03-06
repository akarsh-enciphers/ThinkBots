package com.example.thinkbots;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import java.util.List;


public class ListFragment extends Fragment implements QuizListAdapter.OnQuizListItemClicked {

    private NavController navController;
    private RecyclerView listView;
    private QuizListViewModel quizListViewModel;
    private QuizListAdapter adapter;
    private ProgressBar listProgress;
    private Animation fadeInAnim;
    private Animation fadeOutAnim;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        listView = view.findViewById(R.id.list_view);
        listProgress = view.findViewById(R.id.list_progress);
        adapter = new QuizListAdapter(this);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setHasFixedSize(true);
        listView.setAdapter(adapter);
        fadeInAnim = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
        fadeOutAnim =AnimationUtils.loadAnimation(getContext(),R.anim.fade_out);
    }
    //we will initialize the viewmodel class in our own activity created method

    @Override
    public void onActivityCreated(@Nullable  Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //initializing view model class
        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);

        //once the view model class is initialized then it will call for firebaserepository.getQuizData method in (QuizListViewModel class) which will then query the data from firestore(firebase repository) and will send it to a list which we sent from the interface back to the view model class to the mutable live data

        //this observer will make sure that if anything changes with or quizListModelData  its gonna notify us and send us the new data using the quizListModels variable
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {
                //Load Recycler View
                listView.startAnimation(fadeInAnim);
                listProgress.startAnimation(fadeOutAnim);
                adapter.setQuizListModels(quizListModels);
                adapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    public void onItemClicked(int position) {

        ListFragmentDirections.ActionListFragmentToDetailsFragment action = ListFragmentDirections.actionListFragmentToDetailsFragment();
        action.setPosition(position);
        navController.navigate(action);
    }
}