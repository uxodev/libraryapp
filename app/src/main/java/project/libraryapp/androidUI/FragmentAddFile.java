package project.libraryapp.androidUI;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;


import javax.json.stream.JsonParsingException;

import project.libraryapp.R;
import project.libraryapp.controller.Controller;
import project.libraryapp.library.Library;
import project.libraryapp.storage.Storage;

/*
 * FragmentAddFile runs when the add file button is clicked in ActivityMain.
 * It loads the controller file when created, and gives the user the option to select a
 * json or xml file to load into one of the two library options: main or sister.
 * It then adds the file's data when add file data is selected.
 */
public class FragmentAddFile extends Fragment implements View.OnClickListener {
    private static final int READ_REQUEST_CODE = 42;
    Controller controller = new Controller();
    View view;
    private Uri uri;
    private TextView textFindFile, textAddFileData;
    private RadioButton radioMain, radioSister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_file, container, false);

        view.findViewById(R.id.btnFindFile).setOnClickListener(this);
        textFindFile = (TextView) view.findViewById(R.id.textFindFile);
        textFindFile.setMovementMethod(new ScrollingMovementMethod());
        radioMain = (RadioButton) view.findViewById(R.id.radioMain);
        radioMain.setChecked(true);
        radioSister = (RadioButton) view.findViewById(R.id.radioSister);
        view.findViewById(R.id.btnAddJsonFileData).setOnClickListener(this);
        view.findViewById(R.id.btnAddXmlFileData).setOnClickListener(this);
        textAddFileData = (TextView) view.findViewById(R.id.textAddFileData);
        textAddFileData.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        controller = Storage.loadController(getActivity().getExternalFilesDir(null).getPath() + "/");
    }

    // Actions for when the Find File and Add File Data buttons are clicked.
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFindFile:
                textFindFile.setText("");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
                break;
            case R.id.btnAddJsonFileData:
                addData("json", view);
                break;
            case R.id.btnAddXmlFileData:
                addData("xml", view);
                break;
        }
    }

    private void addData(String fileType, View view) {
        Library.Type lib;
        InputStream inputStream = null;
        if (radioSister.isChecked()) {
            lib = Library.Type.SISTER;
        } else {
            lib = Library.Type.MAIN;
        }
        try {
            inputStream = getActivity().getContentResolver().openInputStream(uri);
        }
        // Issue with using the file selected - tell user to retry and make sure file is
        // still available (in case they selected external and it got disconnected, or it
        // was mis-entered)
        catch (FileNotFoundException e) {
            e.printStackTrace();
            // Create an alert stating no URI was chosen
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Could not load that file. Make sure the file is still accessible " +
                    "in the location that you chose.");
            builder.setTitle("Error");
            // Make it easier for the user to return to the activity by including a negative button
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int arg1) {
                    d.cancel();
                }

            });
            builder.show();
            return;
        }
        // URI is null if a file isn't picked - catch it and tell the user to select a file
        catch (NullPointerException e) {
            e.printStackTrace();
            // Create an alert stating no URI was chosen
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("No file was chosen. Select a file first and try again.");
            builder.setTitle("Error");
            builder.show();
            return;
        }
        try {
            if (controller.addFileData(inputStream, fileType, lib)) {
                textAddFileData.append("Your file data has been imported" + "\n");
            } else {
                textAddFileData.append("File data add failed." + "\n");
            }
        } catch (JsonParsingException e) {
            e.printStackTrace();
            textAddFileData.append("Incorrect file type." + "\n");
        }
    }

    // onActivityResult runs when Find File is clicked.
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                uri = resultData.getData();
                textFindFile.append("File used: \n" + uri.toString() + "\n");
            }
        }
    }
}