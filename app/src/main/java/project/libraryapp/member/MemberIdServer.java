package project.libraryapp.member;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * MemberIdServer class
 */
public class MemberIdServer implements Serializable {

    private transient static MemberIdServer server;
    private int idCounter = 1;              // New server starts at 1

    // Singleton
    private MemberIdServer() {
    }

    public static MemberIdServer instance() {
        if (server == null) {
            return (server = new MemberIdServer());
        } else {
            return server;
        }
    }

    public static void retrieve(ObjectInputStream in) {
        try {
            server = (MemberIdServer) in.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("Could not save changes to the member file. Please try again.");
        } catch (IOException e) {
            System.out.println("Could not load the list of library members. Restart the application.");
        }
    }

    public int getId() {
        return idCounter++;
    }
}
