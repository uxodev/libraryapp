package project.libraryapp.items;

/**
 * No optional field
 */
public class Dvd extends Item {

    public Dvd() {
        super();
    }

    public Dvd(String id, String name, Type type) {
        super(id, name, type);
        this.checkOutTimeDays = 7;
    }

    @Override
    public String toString() {
        String message = super.toString();
        message += toStringEx();
        return message;
    }
}
