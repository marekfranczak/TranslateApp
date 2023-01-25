package com.translateapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.translateapp.databinding.FragmentFirstBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class FirstFragment extends Fragment implements View.OnClickListener{

    private FragmentFirstBinding binding;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Button translateButton;
    private Uri photoUri;
    private boolean connected = false;
    private String originalText;
    private Translate translate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        translateButton = view.findViewById(R.id.translateButton);
        previewView = view.findViewById(R.id.previewView);
        translateButton.setOnClickListener(this);
        cameraProviderFuture =  ProcessCameraProvider.getInstance(this.getContext());
        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e){
                e.printStackTrace();
            }
        }, getExecutor());
    }

    @SuppressLint("RestrictedApi")
    public void onClick(View view){
        capturePhoto();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider){
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void capturePhoto(){
        long timeStamp = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timeStamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        FirstFragment.this.getContext().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        photoUri = outputFileResults.getSavedUri();
                        Data.setUri(outputFileResults.getSavedUri());
                        makeToast("processing.. ");
                        runTextRecognition();
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException e) {
                        makeToast("Error: "+e.getMessage());
                    }
                }
        );
    }

    private void passToNextLayout(){
        NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    private Executor getExecutor(){
        return ContextCompat.getMainExecutor(this.getContext());
    }

    private void makeToast(String text){
        Toast.makeText(FirstFragment.this.getContext(), text, Toast.LENGTH_SHORT).show();
    }


    private void runTextRecognition(){
        Bitmap bitmap = null;
        ContentResolver contentResolver = getContext().getContentResolver();
        try {
            if(Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, photoUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
        } catch (Exception e) {
            originalText = "run text recognition";
            e.printStackTrace();
        }

        assert bitmap != null;
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();
        recognizer.process(image)
                .addOnSuccessListener(
                        text -> {
                            Data.setOriginalText(text.getText());
                            processTextRecognitionResult(text);
                        })
                .addOnFailureListener(
                        e -> makeToast("Error: "+e.getMessage()));
    }

    private void processTextRecognitionResult(Text text){
        List<Text.TextBlock> blocks = text.getTextBlocks();
        if (blocks.size() == 0) {
            makeToast("Text not found.. ");
            originalText = "Text not found..";
            return;
        }
        originalText = text.getText();
        if(checkInternetConnection()){
            // if there is internet connection, get translate service and start translation
            getTranslateService();
            if(originalText == null)
                originalText = "Text not found..";
            else
                translate();
        } else {
            // if not, display "no connection" warning
            Data.setTranslateText("Lost connection..");
        }
    }

    private void translate() {
        String lanCode = "pl";
        try {
            if (Data.getLanguages().getLanguageCode() != null)
                lanCode = Data.getLanguages().getLanguageCode();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        Translation translation = translate.translate(originalText,
                Translate.TranslateOption.targetLanguage(lanCode), Translate.TranslateOption.model("base"));
        Data.setTranslateText(translation.getTranslatedText());
        passToNextLayout();
    }

    private void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try(InputStream is = getResources().openRawResource(R.raw.credentials)){

            //get credentials
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //set credentials and get translate service
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    private boolean checkInternetConnection() {

        //Check internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork != null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                connected = true;
            } else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                connected = true;
            }
        } else {
            connected = false;
        }
        return connected;
    }
}