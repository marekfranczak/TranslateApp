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

/**
 * The class is responsible for handling the first view in the application.
 * After taking a photo, it recognizes the text and translates it into the selected language.
 * @author Marek Frańczak
 * @since 1.0.0
 */
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
        binding = FragmentFirstBinding.inflate(inflater, container, false);
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

    /**
     * Method from view.onClickListener interface.
     * It is responsible for the operation of calling the method that handles capturing the photo.
     * @param view The root view of the fragment.
     */
    @SuppressLint("RestrictedApi")
    public void onClick(View view){
        capturePhoto();
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
     * This method starts the cameraX functionality. It first unbinds all existing bindings for the camera provider.
     * Then it creates a CameraSelector that specifies the rear camera should be used. A Preview instance is created and its surface provider is set to the surface provider of the "previewView".
     * An ImageCapture instance is also created with capture mode set to minimize latency.
     * Finally, the camera provider is bound to the lifecycle of this fragment, with the camera selector, preview, and image capture objects provided as arguments.
     * @param cameraProvider The parameter ProcessCameraProvider cameraProvider is an instance of the ProcessCameraProvider class and it represents the camera provider that can be used to get an instance of the camera and bind it to the lifecycle of the given object.
     *                       In this case, the camera is bound to the lifecycle of the Fragment object through the method bindToLifecycle(this, cameraSelector, preview, imageCapture).
     */
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

    /**
     * The method capturePhoto captures an image and stores it in the external storage. It uses the ImageCapture component of CameraX to take the picture. The timestamp of the image is used as its display name.
     * The MIME type of the image is set to "image/jpeg". The image is saved using the ImageCapture.takePicture method, which takes an OutputFileOptions object to specify the destination and content values of the saved image.
     * The saved image's URI is stored in the photoUri variable and the Data class.
     * Finally, the method makes a toast to indicate the processing of the image and runs the text recognition process.
     */
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

    /**
     * This function navigates the user to the next fragment, SecondFragment, using the NavHostFragment and a specified action, R.id.action_FirstFragment_to_SecondFragment.
     */
    private void passToNextLayout(){
        NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    /**
     * This function returns the main executor for the current context.
     * @return The main executor for the current context.
     */
    private Executor getExecutor(){
        return ContextCompat.getMainExecutor(this.getContext());
    }

    /**
     * This function displays a toast message with the specified text for a short duration.
     * The context for the toast is obtained from the fragment's context.
     * @param text Text that will be displayed.
     */
    private void makeToast(String text){
        Toast.makeText(FirstFragment.this.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * The function runTextRecognition performs text recognition on an image. It first retrieves the image from the photoUri stored in the object.
     * The method MediaStore.Images.Media.getBitmap is used for API levels less than 28, and ImageDecoder.decodeBitmap is used for API level 28 or higher.
     *
     * Next, the image is converted to a Bitmap object and used to create an InputImage object.
     * A TextRecognizer object is created using the method TextRecognition.getClient.
     *
     * Finally, the method process is called on the TextRecognizer object, passing in the InputImage.
     * The success or failure of the text recognition process is determined using the addOnSuccessListener and addOnFailureListener methods.
     * If successful, the recognized text is passed to the processTextRecognitionResult method.
     * If an error occurs, a toast message with the error message is displayed.
     */
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

    /**
     * The processTextRecognitionResult method processes the result of the text recognition task performed on an image using the Text Recognition API.
     * It takes the Text object as an argument, which contains the result of the text recognition.
     * The method first checks the number of text blocks in the result, and if it is zero, it shows a toast message indicating that no text was found and returns.
     * If there are text blocks, it sets the originalText variable to the recognized text.
     *
     * Then, the method checks if there is an internet connection. If there is an internet connection, the method gets the translate service and starts the translation process by calling the translate method.
     * If there is no internet connection, it sets the translateText in the Data object to "Lost connection..".
     * @param text  The Text object, which contains the result of the text recognition.
     */
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

    /**
     * translate() method is used to translate text from one language to another language.
     *
     * This method starts by initializing a string variable called `lanCode` and assigning the value "pl".
     * Then it checks if the language code stored in Data.getLanguages().getLanguageCode() is not null,
     * if it's not null then it updates the `lanCode` variable with the value stored in Data.getLanguages().getLanguageCode().
     *
     * The method then uses the translate object to translate the original text stored in the `originalText` variable.
     * The translate method takes the original text, target language code and the translate model as input parameters.
     *
     * The translate method returns the translated text in the form of Translation object.
     * The method then sets the translated text in the Data object by calling Data.setTranslateText().
     *
     * Finally, the method calls the passToNextLayout() method to navigate to the next layout.
     *
     * @throws NullPointerException if the language code stored in Data.getLanguages().getLanguageCode() is null.
     */
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

    /**
     * This method initializes and sets up the Google Cloud Translate API client by using the API key stored in the credentials.json file.
     * First, it sets the StrictMode policy to permit all actions to run on the main thread.
     * Then, it tries to open the credentials.json file from the resources folder using the openRawResource method.
     * It retrieves the Google Cloud credentials from the input stream using the fromStream method.
     * Next, it creates an instance of the TranslateOptions by passing the credentials to the newBuilder method.
     * Finally, it gets the translate service by calling the getService method on the TranslateOptions instance.
     * @throws if the credentials.json file is null.
     */
    private void getTranslateService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try(InputStream is = getResources().openRawResource(R.raw.credentials)){

            //get credentials
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //set credentials and get translate service
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Check if there is an active internet connection
     *
     * @return  a boolean value indicating whether there is an active internet connection (true) or not (false)
     */
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