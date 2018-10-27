package project.libraryapp.androidUI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import project.libraryapp.R;

/**
 * ActivityMain is the start screen when the library app loads.
 * It has buttons that call other activities for the application to add files,
 * add members, change item status, display member's checked out items, and show
 * items in either library.
 */
public class ActivityMain extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if using side container
        if (findViewById(R.id.fragment_container_side) != null) {
            // being restored from a previous state
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            FragmentMain fragmentMain = new FragmentMain();
            FragmentDisplayLibrary starterFragment = new FragmentDisplayLibrary();

//             Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_main, fragmentMain).commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_side, starterFragment).commit();
        } else {
            // being restored from a previous state
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            FragmentMain fragmentMain = new FragmentMain();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_main, fragmentMain).commit();
        }
    }
}
