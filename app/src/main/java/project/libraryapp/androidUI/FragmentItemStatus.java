package project.libraryapp.androidUI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import project.libraryapp.R;
import project.libraryapp.controller.Controller;
import project.libraryapp.items.Item;
import project.libraryapp.library.Library;
import project.libraryapp.storage.Storage;

/**
 * ItemStatusActivity is reached via the "ItemStatus" button in the MainActivity.
 * It takes user input for a given library, a given library item ID, and then shows either the
 * status, or attempts to change the status, based on one of 7 radio button options.
 */
public class FragmentItemStatus extends Fragment implements View.OnClickListener {
    // controller handles updating the item's status in the libraries using the controller's
    // changeItemStatus method.
    Controller controller = new Controller();
    View view;
    // editTextItemId takes user input for the item they want to check or change the status for.
    private EditText editTextItemId;
    // rbMain and rbSister are radio buttons to specify which library.
    // The other rb radio buttons are for viewing the status (rbCheckStatus) or changing the item's
    // status (e.g. checking it in, flagging it as missing, etc.)
    private RadioButton rbMain, rbSister, rbCheckStatus, rbCheckedIn, rbMissing, rbOverdue, rbReference, rbRemoved, rbShelving;
    // textItemStatus displays success or failure output to the user.
    private TextView textItemStatus;

    /*
    @param savedInstanceState: loads a previous state if it was stored.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_status, container, false);

        rbMain = (RadioButton) view.findViewById(R.id.rbMain);
        rbMain.setChecked(true);
        rbSister = (RadioButton) view.findViewById(R.id.rbSister);
        editTextItemId = (EditText) view.findViewById(R.id.editTextItemId);
        rbCheckStatus = (RadioButton) view.findViewById(R.id.rbCheckStatus);
        rbCheckStatus.setChecked(true);
        rbCheckedIn = (RadioButton) view.findViewById(R.id.rbCheckedIn);
        rbMissing = (RadioButton) view.findViewById(R.id.rbMissing);
        rbOverdue = (RadioButton) view.findViewById(R.id.rbOverdue);
        rbReference = (RadioButton) view.findViewById(R.id.rbReference);
        rbRemoved = (RadioButton) view.findViewById(R.id.rbRemoved);
        rbShelving = (RadioButton) view.findViewById(R.id.rbShelving);
        view.findViewById(R.id.btnItemStatus).setOnClickListener(this);
        textItemStatus = (TextView) view.findViewById(R.id.textItemStatus);
        textItemStatus.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        controller = Storage.loadController(getActivity().getExternalFilesDir(null).getPath() + "/");
    }

    // When Change Item Status is clicked, it checks which library is selected, and then
    // tries to process the status change for the item ID in the item text box for the view.
    @Override
    public void onClick(View view) {
        // Reference to the library the user wants to work with, and is passed to the
        // checkIn or checkOut methods of the controller
        Library.Type library;
        if (rbSister.isChecked()) {
            library = Library.Type.SISTER;
        } else {
            library = Library.Type.MAIN;
        }
        // Checks which radio button is currently selected, and calls the appropriate method in
        // controller to update the item status. Also returns a string to print whether it was
        // successful or failed.
        if (rbCheckStatus.isChecked()) {
            textItemStatus.append(controller.changeItemStatus(editTextItemId.getText().toString().trim(), Item.Status.CHECK_STATUS, library));
        } else if (rbCheckedIn.isChecked()) {
            textItemStatus.append(controller.changeItemStatus(editTextItemId.getText().toString().trim(), Item.Status.CHECKED_IN, library));
        } else if (rbMissing.isChecked()) {
            textItemStatus.append(controller.changeItemStatus(editTextItemId.getText().toString().trim(), Item.Status.MISSING, library));
        } else if (rbOverdue.isChecked()) {
            textItemStatus.append(controller.changeItemStatus(editTextItemId.getText().toString().trim(), Item.Status.OVERDUE, library));
        } else if (rbReference.isChecked()) {
            textItemStatus.append(controller.changeItemStatus(editTextItemId.getText().toString().trim(), Item.Status.REFERENCE_ONLY, library));
        } else if (rbRemoved.isChecked()) {
            textItemStatus.append(controller.changeItemStatus(editTextItemId.getText().toString().trim(), Item.Status.REMOVED_FROM_CIRCULATION, library));
        } else if (rbShelving.isChecked()) {
            textItemStatus.append(controller.changeItemStatus(editTextItemId.getText().toString().trim(), Item.Status.SHELVING, library));
        }
    }
}
