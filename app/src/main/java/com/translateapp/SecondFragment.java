package com.translateapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.translateapp.databinding.FragmentSecondBinding;

/**
 * The class is responsible for handling the second view in the application.
 * This class display data photo and texts received from first class.
 * @author Marek Frańczak
 * @since 1.0.0
 */
public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private TextView textView;
    private ImageView imageView;


    /**
     * This method is an overridden method from the Fragment class.
     * It is called when the fragment's view is being created. The method returns the root view of the fragment's layout.
     * The method first inflates the layout for the fragment by calling the inflate method on the FragmentFirstBinding object and passing the inflater, container, and false as parameters.
     * The false parameter indicates that the inflated layout should not be added to the container view.
     *
     * @param inflater A LayoutInflater object that is used to inflate the fragment's layout.
     * @param container A ViewGroup object that is the parent view of the fragment.
     * @param savedInstanceState A Bundle object that can be used to save the state of the fragment.
     * @return The method returns the root view of the layout by calling the getRoot method on the binding object.
     * @see Fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * The onViewCreated method is a part of the Fragment lifecycle and it's called when the View associated with the fragment has been created and added to the UI.
     * This method is typically used to initialize the views and perform other setup tasks for the fragment's user interface.
     * @param view The root view of the fragment.
     * @param savedInstanceState parameter is a Bundle object that is used to pass data between various Android activities.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.textView);
        displayText(Data.getOriginalText(), Data.getTranslateText());
        imageView = view.findViewById(R.id.imageView);
        imageView.setImageURI(Data.getUri());
        imageView = view.findViewById(R.id.imageView);
    }

    /**
     * The onDestroyView method is a lifecycle method in the Android framework that is called when the view associated with a fragment is being destroyed.
     * In this case, it is being overridden to set the value of the binding variable to null.
     * This may be done to release resources and prevent memory leaks associated with the view.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * The method that connect texts and display them.
     * @param originalText Text recognized in the photo.
     * @param translateText Text after translation
     */
    private void displayText(String originalText, String translateText){
        String text = "Original: \n"+originalText+"\n\nTranslate: \n"+translateText;
        textView.setText(text);
    }

}