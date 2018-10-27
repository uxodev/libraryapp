//package project.libraryapp.ui;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//
//import javafx.application.Application;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import project.libraryapp.controller.Controller;
//import project.libraryapp.items.Item;
//import project.libraryapp.library.Library;
//import project.libraryapp.storage.Storage;
//
//
///**
// * This program will take a .JSON or .xml file of items in a Library, and create a collection
// * of the items. Then it will allow items to be checked out and checked back in.
// * The 4 types of items are : CD, DVD, book, and magazine.
// */
//public class LibraryUI extends Application {
//
//    // itemID is the unique string identifier (digits and letters) for a library item,
//    // and is tracked during check in's and check out's.
//    private final TextField itemId = new TextField();
//    private final TextField itemId2 = new TextField();
//    // cardNumber is a unique integer associated with a library user. The card numbers are
//    // incremented up by 1 each time a user is added via the MemberIdServer class.
//    private final TextField cardNumber = new TextField();
//    // text area for outputting messages to the user during program execution.
//    private final TextArea text = new TextArea();
//    // Allows for scrolling of the text area; important for listing library items and
//    // outputting longer checked-out lists for the user.
//    private final ScrollPane scrollPane = new ScrollPane();
//    // fileChooser is used to pick the XML and JSON files sent from the sister library and add them
//    // to the application data.
//    private final FileChooser fileChooser = new FileChooser();
//    private Stage changeItemStatusStage = new Stage();
//    // Specifies that this application is the main Library using Type
//    private Library.Type library = Library.Type.MAIN;
//    // Instantiation of the Controller object that will load any serialized Controller data, and then be used
//    // in the execution of the application.
//    private Controller app = new Controller(System.getProperty("user.dir") + "\\");
//
//    public static void main(String[] args) {
//        Application.launch(args);
//    }
//
//    @Override
//    public void start(final Stage primaryStage) throws IOException {
//        app = Storage.loadController(System.getProperty("user.dir") + "\\"); // Load data from file
//
//        VBox pane = new VBox();
//
//        HBox topPane = new HBox();
//        topPane.setAlignment(Pos.CENTER);
//        topPane.setPadding(new Insets(.5, .5, .5, .5));
//
//        final ToggleGroup libraries = new ToggleGroup();
//        final RadioButton rbMain = new RadioButton("Main Library");
//        rbMain.setToggleGroup(libraries);
//        rbMain.setSelected(true);
//        RadioButton rbSister = new RadioButton("Sister Library");
//        rbSister.setToggleGroup(libraries);
//
//        VBox leftTopPane = new VBox(9);
//        leftTopPane.setAlignment(Pos.BASELINE_LEFT);
//        leftTopPane.getChildren().addAll(new Label("Item ID: "), new Label("Library Card Number:"), new Label(""), rbMain, rbSister);
//
//        // Create radio buttons for the different items
//        HBox itemsPane = new HBox();
//        itemsPane.setAlignment(Pos.CENTER);
//        itemsPane.setPadding(new Insets(.5, .5, .5, .5));
//        itemsPane.setMinHeight(30);
//        final CheckBox cbBooks = new CheckBox("Books");
//        final CheckBox cbCDs = new CheckBox("CDs");
//        final CheckBox cbDVDs = new CheckBox("DVDs");
//        final CheckBox cbMagazines = new CheckBox("Magazines");
//        cbBooks.setSelected(true);
//        cbCDs.setSelected(true);
//        cbDVDs.setSelected(true);
//        cbMagazines.setSelected(true);
//        cbBooks.setMinWidth(100);
//        cbCDs.setMinWidth(100);
//        cbDVDs.setMinWidth(100);
//        cbMagazines.setMinWidth(100);
//        itemsPane.getChildren().addAll(cbBooks, cbCDs, cbDVDs, cbMagazines);
//
//        VBox rightTopPane = new VBox();
//        Button btAddFileData = new Button("Add File Data");
//        btAddFileData.setMaxWidth(Double.MAX_VALUE);
//        Button btAddMember = new Button("Add Member");
//        btAddMember.setMaxWidth(Double.MAX_VALUE);
//        Button btCheckOut = new Button("Check Out");
//        btCheckOut.setMaxWidth(Double.MAX_VALUE);
//        Button btCheckIn = new Button("Check In");
//        btCheckIn.setMaxWidth(Double.MAX_VALUE);
//        Button btChangeItemStatus = new Button("Change/Check Item Status");
//        btChangeItemStatus.setMaxWidth(Double.MAX_VALUE);
//        Button btDisplay = new Button("Display Library Items");
//        btDisplay.setMaxWidth(Double.MAX_VALUE);
//        Button btCheckedOut = new Button("User's checked out items");
//        btCheckedOut.setMaxWidth(Double.MAX_VALUE);
//        rightTopPane.getChildren().addAll(itemId, cardNumber, btAddFileData, btAddMember, btCheckOut,
//                btCheckIn, btChangeItemStatus, btCheckedOut, btDisplay);
//
//        topPane.getChildren().addAll(leftTopPane, rightTopPane);
//        HBox.setHgrow(leftTopPane, Priority.ALWAYS);
//        HBox.setHgrow(rightTopPane, Priority.ALWAYS);
//
//        HBox textPane = new HBox();
//        textPane.setAlignment(Pos.CENTER);
//        textPane.setPadding(new Insets(1.5, .5, .5, .5));
//        textPane.getChildren().add(scrollPane);
//        scrollPane.setContent(text);
//        scrollPane.setFitToHeight(true);
//        scrollPane.setFitToWidth(true);
//        HBox.setHgrow(scrollPane, Priority.ALWAYS);
//        textPane.setMaxHeight(Double.MAX_VALUE);
//        textPane.setPrefHeight(400);
//        text.setScrollTop(Double.MAX_VALUE);
//
//        pane.getChildren().addAll(topPane, itemsPane, textPane);
//
//        Scene scene = new Scene(pane);
//        primaryStage.setTitle("Library");
//        primaryStage.setHeight(750);
//        primaryStage.setWidth(750);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("JSON", "*.json"),
//                new FileChooser.ExtensionFilter("XML", "*.xml")
//        );
//
//        //
//        // Process the btCheckIn button -- call the checkIn() method
//        //
//        btCheckIn.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    text.appendText(app.checkIn(itemId.getText().trim(), library));
//                } catch (NumberFormatException ex) {
//                    text.appendText("\nIncorrect card number format");
//                }
//            }
//        });
//
//        //
//        // Create the ChangeItemStatus Popup
//        //
//        VBox pane2 = new VBox();
//
//        HBox topPane2 = new HBox();
//        topPane2.setAlignment(Pos.CENTER);
//        topPane2.setPadding(new Insets(.5, .5, .5, .5));
//
//        final ToggleGroup states = new ToggleGroup();
//        final RadioButton rbCheckStatus = new RadioButton("Check Item Status");
//        rbCheckStatus.setToggleGroup(states);
//        final RadioButton rbCheckedIn = new RadioButton("Checked In");
//        rbCheckedIn.setToggleGroup(states);
//        final RadioButton rbMissing = new RadioButton("Missing");
//        rbMissing.setToggleGroup(states);
//        final RadioButton rbOverdue = new RadioButton("Overdue");
//        rbOverdue.setToggleGroup(states);
//        final RadioButton rbShelving = new RadioButton("Shelving");
//        rbShelving.setToggleGroup(states);
//        final RadioButton rbRemoved = new RadioButton("Removed From Circulation");
//        rbRemoved.setToggleGroup(states);
//        final RadioButton rbReference = new RadioButton("Reference Only");
//        rbReference.setToggleGroup(states);
//        VBox bottomPane = new VBox();
//        bottomPane.setPadding(new Insets(.5, .5, .5, 6.5));
//        bottomPane.getChildren().addAll(rbCheckStatus, rbCheckedIn, rbMissing, rbOverdue, rbReference, rbRemoved, rbShelving);
//
//        VBox leftTopPane2 = new VBox(9);
//        leftTopPane2.setAlignment(Pos.BASELINE_LEFT);
//        leftTopPane2.getChildren().addAll(new Label("Item ID: "));
//
//        VBox rightTopPane2 = new VBox();
//        rightTopPane2.getChildren().addAll(itemId2);
//
//        topPane2.getChildren().addAll(leftTopPane2, rightTopPane2);
//        HBox.setHgrow(leftTopPane2, Priority.ALWAYS);
//        HBox.setHgrow(rightTopPane2, Priority.ALWAYS);
//
//        HBox buttonPane = new HBox();
//        buttonPane.setPadding(new Insets(3.5, 3.5, .5, 6.5));
//        Button btSave = new Button("Save");
//        buttonPane.setAlignment(Pos.CENTER);
//        buttonPane.getChildren().addAll(btSave);
//
//        pane2.getChildren().addAll(topPane2, bottomPane, buttonPane);
//        changeItemStatusStage.setTitle("Change/Check Item Status"); // Set the stage title
//        changeItemStatusStage.setHeight(250);
//        changeItemStatusStage.setWidth(300);
//        changeItemStatusStage.setScene(new Scene(pane2)); // Place the scene in the stage
//
//        btSave.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent e) {
//                if (rbMissing.isSelected()) {
//                    text.appendText(app.changeItemStatus(itemId2.getText().trim(), Item.Status.MISSING, library));
//                } else if (rbOverdue.isSelected()) {
//                    text.appendText(app.changeItemStatus(itemId2.getText().trim(), Item.Status.OVERDUE, library));
//                } else if (rbCheckStatus.isSelected()) {
//                    text.appendText(app.changeItemStatus(itemId2.getText().trim(), Item.Status.CHECK_STATUS, library));
//                } else if (rbReference.isSelected()) {
//                    text.appendText(app.changeItemStatus(itemId2.getText().trim(), Item.Status.REFERENCE_ONLY, library));
//                } else if (rbRemoved.isSelected()) {
//                    text.appendText(app.changeItemStatus(itemId2.getText().trim(), Item.Status.REMOVED_FROM_CIRCULATION, library));
//                } else if (rbShelving.isSelected()) {
//                    text.appendText(app.changeItemStatus(itemId2.getText().trim(), Item.Status.SHELVING, library));
//                } else if (rbCheckedIn.isSelected()) {
//                    text.appendText(app.changeItemStatus(itemId2.getText().trim(), Item.Status.CHECKED_IN, library));
//                } else {
//                    text.appendText("\nNo action taken, there was no status button selected");
//                }
//                changeItemStatusStage.close();
//            }
//        });
//
//        //
//        // Process the btCheckIn button -- call the checkIn() method
//        //
//        btChangeItemStatus.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                changeItemStatusStage.showAndWait();
//            }
//        });
//
//        //
//        // Process the btCheckOut button -- call the checkOut() method
//        //
//        btCheckOut.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    text.appendText(app.checkOut((Integer.parseInt(cardNumber.getText().trim())), itemId.getText().trim(), library));
//                } catch (NumberFormatException ex) {
//                    text.appendText("\nIncorrect card number format");
//                }
//            }
//        });
//
//        //
//        // Process the btCheckedOut button -- call the displayMemberCheckedOutItems() method
//        //
//        btCheckedOut.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                try {
//                    text.clear();
//                    text.appendText(app.displayMemberCheckedOutItems((Integer.parseInt(cardNumber.getText().trim()))));
//                } catch (NumberFormatException ex) {
//                    text.appendText("\nIncorrect card number format");
//                }
//            }
//        });
//
//        //
//        // Process the btAddMember button -- call the addMember() method
//        //
//        btAddMember.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                JOptionPane newMember = new JOptionPane();
//                String result = (String) newMember.showInputDialog(null, "Enter Member Name:", "New Member", JOptionPane.PLAIN_MESSAGE, null, null, "New Member");
//                // if dialog had input, Controller will add a new member with input name
//                // and the next member ID available via MemberIdServer.
//                if (result != null) {
//                    text.appendText(app.addMember(result));
//                } else {
//                    text.appendText("\nNo member name entered.");
//                }
//            }
//        });
//
//        //
//        // Process the btAddFileData button -- call the addFileData() method
//        //
//        btAddFileData.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                File file = fileChooser.showOpenDialog(primaryStage);
//                InputStream inputStream = null;
//                String fileType = null;
//                if (file != null) {
//                    text.appendText("\nFile used: " + file.getAbsolutePath());
//
//                    if (file.getAbsolutePath().toLowerCase().endsWith("json"))
//                        fileType = "json";
//                    else if (file.getAbsolutePath().toLowerCase().endsWith("xml"))
//                        fileType = "xml";
//                    else {
//                        text.appendText("Error: Invalid file type entered.");
//                    }
//
//                    try {
//                        inputStream = new FileInputStream(file);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (app.addFileData(inputStream, fileType, library)) {
//                        text.appendText("\nFile added: " + file.getAbsolutePath());
//                    } else {
//                        text.appendText("\nFile add failed");
//                    }
//                } else {
//                    text.appendText("\nNo file added.");
//                }
//            }
//        });
//
//        //
//        // Process the btDisplay button -- calls the displayLibraryItems() method
//        // Masks allow for the selection of more than one Item type to display.
//        //
//        btDisplay.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                text.clear();
//                int mask = 0;
//                if (cbBooks.isSelected()) {
//                    mask += 1;
//                }
//                if (cbCDs.isSelected()) {
//                    mask += 2;
//                }
//                if (cbDVDs.isSelected()) {
//                    mask += 4;
//                }
//                if (cbMagazines.isSelected()) {
//                    mask += 8;
//                }
//                text.appendText(app.displayLibraryItems(mask, library));
//            }
//        });
//
//        //
//        // Handles getting the set Library radio button and setting the variable appropriately
//        //
//        libraries.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
//            public void changed(ObservableValue<? extends Toggle> ov,
//                                Toggle old_toggle, Toggle new_toggle) {
//                if (rbMain.isSelected()) {
//                    library = Library.Type.MAIN;
//                } else {
//                    library = Library.Type.SISTER;
//                }
//                if (library == Library.Type.MAIN)
//                    text.appendText("\nMain Library:");
//                else
//                    text.appendText("\nSister Library:");
//            }
//        });
//    }
//}
