package com.example.loveflowerview.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.loveflowerview.R;
import com.example.loveflowerview.databinding.FragmentHomeBinding;
import com.example.loveflowerview.view.LoveFlowerView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private Button textView, delete;
    private ConstraintLayout mMainLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView = binding.textHome;
        mMainLayout = binding.homeLayout;
        delete = binding.textDelete;

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRes();
    }

    private LoveFlowerView loveFlowerView;
    private void initRes(){
        Log.d("tag", "onViewCreated");
        if (loveFlowerView == null) {
            Log.d("tag", "loveFlowerView create");
            loveFlowerView = new LoveFlowerView(getContext());
        }
        textView.setOnClickListener((View view) -> {
            loveFlowerView.addFlowerView();
        });
        delete.setOnClickListener((View view) -> {
            loveFlowerView.removeFlowerLayout();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("tag", "onStop");
//        loveFlowerView.removeFlowerView();
    }
}