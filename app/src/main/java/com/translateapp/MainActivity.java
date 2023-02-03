package com.translateapp;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.translateapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * MainActivity is an activity class that is the main screen for the application.
 * This class sets up the toolbar, navigation bar, and the action bar.
 *
 * @extends AppCompatActivity
 * @author Marek Frańczak
 * @since 1.0.0
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The AppBarConfiguration instance used to configure the app bar.
     */
    private AppBarConfiguration appBarConfiguration;
    /**
     * The binding instance for the MainActivity layout.
     */
    private ActivityMainBinding binding;

    /**
     * This method is called when the activity is first created.
     * It sets up the toolbar, navigation bar, and action bar.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    /**
     * This method creates the options menu.
     *
     * @param menu The options menu.
     * @return A boolean indicating whether the menu was created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for(Languages languages : Languages.values())

            menu.add(languages.toString());
        return true;
    }

    /**
     * This method handles the clicks on the action bar items.
     *
     * @param item The clicked menu item.
     * @return A boolean indicating whether the item was handled.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(item == null) {
            System.out.println(item.getTitle());
            Data.setLanguages(Languages.valueOf("Poland"));
        }

        int id = item.getItemId();
        try {
            Data.setLanguages(Languages.valueOf(item.getTitle().toString()));
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's activity hierarchy from the action bar.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}