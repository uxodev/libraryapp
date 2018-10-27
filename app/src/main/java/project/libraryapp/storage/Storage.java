package project.libraryapp.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import project.libraryapp.controller.Controller;
import project.libraryapp.member.MemberIdServer;

/**
 * Storage class: this class works with the ControllerData.bin and MemberServerData.bin files
 * To create a persistent state for the library items, libraries, and member checkouts.
 * It is dependent on instances of the Controller and MemberIdServer classes.
 * save is called whenever an instance of the Controller class completes the
 * checkIn, checkOut, addMember, and addFileData methods.
 */
public class Storage {
    // controllerFile is a serialized copy of a Controller object, which contains the main library,
    // sister library, and member list.
    // memberServerFile contains a serialized copy of a MemberIdServer object. This keeps track of
    // the next member ID to use for new members (increments by 1 for each new ID).
    private static Controller controller = new Controller();
    private static MemberIdServer server = MemberIdServer.instance();

    // Loads a Controller object from the ControllerData.bin file.
    public static Controller loadController(String filePath) {
        controller.setSavePath(filePath);
        File checkFileExists = new File(filePath + "ControllerData.bin");
        if (checkFileExists.exists() && checkFileExists.length() > 0) {
            try {
                FileInputStream file = new FileInputStream(filePath + "ControllerData.bin");
                ObjectInputStream input = new ObjectInputStream(file);
                controller = (Controller) input.readObject();
                input.close();
                file.close();
            }
            // If the controller file is not found, notify the user that it did not load.
            catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Controller file not found.");
            }
            // If there is an IO Exception other than the file isn't found, notify the user and prompt them to restart the application.
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading controller file. Restart the application" +
                        " to try loading the file again.");
            }
            // If there is a ClassNotFoundException, there's something wrong with how the app loaded since it's probably missing a jar or
            // one of the other class files.
            catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("Error starting the library application. Restart the application" +
                        " to try loading the file again.");
            }
        } else {
            try {
                checkFileExists.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error creating controller file. Restart the application" +
                        " to try creating the file again.");
            }
        }
        return controller;
    }

    // Load the MemberIdServer object from its serialized file. This is needed to ensure
    // any new member IDs are incremented based off the last member ID (+1 added for each new member).
    public static MemberIdServer loadServer(String filePath) {
        try {
            FileInputStream serverFile = new FileInputStream(filePath + "ControllerData.bin");
            ObjectInputStream serverInput = new ObjectInputStream(serverFile);
            MemberIdServer.retrieve(serverInput);
            serverInput.close();
            serverFile.close();
        }
        // If the member file is not found, notify the user that it did not load.
        // This means any saved information was not loaded, so information used could be inaccurate.
        catch (FileNotFoundException e) {
            System.out.println("Could not find the member file. Restart the application.");
        }         // If the member did not load for another reason, notify the user that it did not load.
        // This means any saved information was not loaded, so information used could be inaccurate.
        catch (IOException e) {
            System.out.println("Could not load the list of library members. Restart the application.");
        }
        return server;
    }

    /* Saves any changes made in the app to both the controller file and the memberServerId files.
    @param contData: the current instance of a Controller object that will be saved into ControllerData.bin
    @param idServer: the instance of MemberIdServer that will be saved into MemberServerData.bin
    @return boolean: whether the save was successful (true) or failed (false)
     */
    public static boolean save(Controller contData, MemberIdServer idServer, String filePath) {

        // Save the whole controller object
        try {
            FileOutputStream file = new FileOutputStream(filePath + "ControllerData.bin");
            ObjectOutputStream output = new ObjectOutputStream(file);
            output.writeObject(contData);
            output.close();
            file.close();
        }
        // If the Controller object couldn't be saved to the controllerFile, warn the user no changes were saved.
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not save changes to the controller file. Please try again.");
            return false;
        }
        // This means there was an error writing to or closing the Controller file. Warn the user the changes may not have saved.
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not save changes to the controller file. Please retry.");
            return false;
        }

        // Save the MemberIdServer
        try {
            FileOutputStream serverFile = new FileOutputStream(filePath + "MemberServerData.bin");
            ObjectOutputStream serverOut = new ObjectOutputStream(serverFile);
            serverOut.writeObject(idServer);
            serverOut.close();
            serverFile.close();
        }
        // If the MemberIdServer object couldn't be saved to the memberServerFile, warn the user no changes were saved.
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find or access the member file. Please retry.");
            return false;
        }
        // This means there was an error writing to or closing the MemberIdServer file. Warn the user the changes may not have saved.
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not save changes to the member file. Please retry.");
            return false;
        }
        return true;
    }

    /* Saves any changes made in the app to both the controller file, and returns
    // a boolean based on whether it was successful.
    @param contData: instance of Controller to be saved in ControllerData.bin
    @return boolean: true if save was successful, false if it failed.
    */
    public static boolean save(Controller contData, String filePath) {

        // Save the whole controller object
        try {
            FileOutputStream file = new FileOutputStream(filePath + "ControllerData.bin");
            ObjectOutputStream output = new ObjectOutputStream(file);
            output.writeObject(contData);
            output.close();
            file.close();
        }
        // If the Controller object couldn't be saved to the controllerFile, warn the user no changes were saved.
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not save changes to the controller file. Please try again.");
            return false;
        }
        // This means there was an error writing to or closing the Controller file. Warn the user the changes may not have saved.
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not save changes to the controller file. Please retry.");
            return false;
        }
        return true;
    }
}
