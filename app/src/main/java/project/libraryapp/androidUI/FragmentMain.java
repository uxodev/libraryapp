package project.libraryapp.androidUI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import project.libraryapp.R;
import project.libraryapp.controller.Controller;
import project.libraryapp.storage.Storage;

/**
 * ActivityMain is the start screen when the library app loads.
 * It has buttons that call other activities for the application to add files,
 * add members, change item status, display member's checked out items, and show
 * items in either library.
 */
public class FragmentMain extends Fragment implements View.OnClickListener {

    public Controller controller = new Controller();
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        view.findViewById(R.id.addFileData).setOnClickListener(this);
        view.findViewById(R.id.addMember).setOnClickListener(this);
        view.findViewById(R.id.checkInOut).setOnClickListener(this);
        view.findViewById(R.id.itemStatus).setOnClickListener(this);
        view.findViewById(R.id.membersItems).setOnClickListener(this);
        view.findViewById(R.id.displayLibrary).setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        controller = Storage.loadController(getActivity().getExternalFilesDir(null).getPath() + "/");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addFileData:
                FragmentAddFile fragmentAddFile = new FragmentAddFile();
                swapFragment(view, fragmentAddFile);
                break;
            case R.id.addMember:
                FragmentAddMember fragmentAddMember = new FragmentAddMember();
                swapFragment(view, fragmentAddMember);
                break;
            case R.id.checkInOut:
                FragmentCheckInOut fragmentCheckInOut = new FragmentCheckInOut();
                swapFragment(view, fragmentCheckInOut);
                break;
            case R.id.itemStatus:
                FragmentItemStatus fragmentItemStatus = new FragmentItemStatus();
                swapFragment(view, fragmentItemStatus);
                break;
            case R.id.membersItems:
                FragmentMembersItems fragmentMembersItems = new FragmentMembersItems();
                swapFragment(view, fragmentMembersItems);
                break;
            case R.id.displayLibrary:
                FragmentDisplayLibrary libraryActivity = new FragmentDisplayLibrary();
                swapFragment(view, libraryActivity);
                break;
        }
    }

    private void swapFragment(View view, Fragment fragment) {
        int container;
        if (view.getRootView().findViewById(R.id.fragment_container_side) != null) {
            container = R.id.fragment_container_side;
        } else {
            container = R.id.fragment_container_main;
        }
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
