package project.libraryapp.items;

/**
 * Optional field: author
 */
public class Book extends Item {

    private String author;

    public Book() {
        super();
    }

    public Book(String id, String name, Type type, String author) {
        super(id, name, type);
        this.author = author;
        this.checkOutTimeDays = 21;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        String message = super.toString();
        if (author != null)
            message += " (" + author + ")";
        message += toStringEx();
        return message;
    }
}
