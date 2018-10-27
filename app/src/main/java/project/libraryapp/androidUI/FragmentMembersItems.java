package project.libraryapp.androidUI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import project.libraryapp.R;
import project.libraryapp.controller.Controller;
import project.libraryapp.storage.Storage;

/**
 * FragmentMembersItems lets the user enter a library card #, and then displays any items
 * that are currently associated with the number.
 * editTextMemberItems allows input for the card #.
 * textMemberItems displays the return items to the user.
 * controller contains the member and item information.
 */
public class FragmentMembersItems extends Fragment implements View.OnClickListener {
    Controller controller = new Controller();
    View view;
    private EditText editTextMemberItems;
    private TextView textMemberItems;

    /*
    Loads the controller object, displays the edittext box, and the button to display the items.
    @param savedInstanceState: loads a previous state if it was stored.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_members_items, container, false);

        editTextMemberItems = (EditText) view.findViewById(R.id.editTextMemberItems);
        view.findViewById(R.id.btnAddMember).setOnClickListener(this);
        textMemberItems = (TextView) view.findViewById(R.id.textMemberItems);
        textMemberItems.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        controller = Storage.loadController(getActivity().getExternalFilesDir(null).getPath() + "/");
    }

    @Override
    public void onClick(View view) {
        try {
            textMemberItems.append(controller.displayMemberCheckedOutItems((Integer.parseInt(editTextMemberItems.getText().toString().trim()))));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            builder.setMessage("Card number cannot be empty.");
            builder.setTitle("Error");
            builder.show();
            return;
        }
    }
}
