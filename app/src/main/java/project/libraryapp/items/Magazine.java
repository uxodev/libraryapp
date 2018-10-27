package project.libraryapp.items;

/**
 * Optional field: volume
 */
public class Magazine extends Item {

    private String volume;

    public Magazine() {
        super();
    }

    public Magazine(String id, String name, Type type, String volume) {
        super(id, name, type);
        this.volume = volume;
        this.checkOutTimeDays = 7;
    }

    public Magazine(String id, String name, Type type) {
        super(id, name, type);
        this.volume = null;
        this.checkOutTimeDays = 7;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        String message = super.toString();
        if (volume != null)
            message += "- Volume: " + volume;
        message += toStringEx();
        return message;
    }
}
