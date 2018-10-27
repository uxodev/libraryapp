package project.libraryapp.items;

/**
 * Optional field: artist
 */
public class Cd extends Item {

    private String artist;

    public Cd() {
        super();
    }

    public Cd(String id, String name, Type type, String artist) {
        super(id, name, type);
        this.artist = artist;
        this.checkOutTimeDays = 7;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        String message = super.toString();
        if (artist != null)
            message += " (" + artist + ")";
        message += toStringEx();
        return message;
    }

}
