package com.translateapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.translateapp.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private TextView textView;
    private Button saveButton;
    private Button deleteButton;
    private ImageView imageView;
    private Uri photoUri;
    private boolean connected = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        System.out.println("URI: "+String.valueOf(photoUri));
        textView = view.findViewById(R.id.textView);
        displayText(Data.getOriginalText(), Data.getTranslateText());
        saveButton = view.findViewById(R.id.translateAndSave);
        imageView = view.findViewById(R.id.imageView);
        imageView.setImageURI(Data.getUri());
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passToFirstLayout();
            }
        });
        deleteButton = view.findViewById(R.id.translateAndDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passToFirstLayout();
            }
        });
        imageView = view.findViewById(R.id.imageView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void passToFirstLayout(){
        NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
    }

    private void makeToast(String text){
        Toast.makeText(SecondFragment.this.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void displayText(String originalText, String translateText){
        textView.setText("Original: \n"+originalText+"\n\nTranslate: \n"+translateText);
    }

}