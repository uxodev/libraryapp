package project.libraryapp.member;

import java.io.Serializable;
import java.util.ArrayList;

import project.libraryapp.items.Item;

/**
 * Creates and maintains a library card number
 * and a list of checked out items for the member.
 */
public class Member implements Serializable {

    private String name;
    private int libraryCardNumber;
    // the items that this member has checked out
    private ArrayList<Item> checkedOutItems = new ArrayList<>();

    public Member(int libraryCardNumber, String name) {
        this.libraryCardNumber = libraryCardNumber;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLibraryCardNumber() {
        return libraryCardNumber;
    }

    /**
     * Adds an item to the members checked out list.
     *
     * @param item the item to add
     */
    public void addItem(Item item) {
        checkedOutItems.add(item);
    }

    /**
     * Removes an item from the members checked out list.
     *
     * @param item the item to remove
     */
    public void removeItem(Item item) {
        checkedOutItems.remove(item);
    }

    /**
     * @return the list of checked out items
     */
    public ArrayList<Item> getCheckedOutItems() {
        return checkedOutItems;
    }

    /**
     * Checks if an item is checked out by this user
     *
     * @param item item to verify
     * @return true if item is checked out by this user, false otherwise
     */
    public boolean hasItem(Item item) {
        for (Item checkedOutItem : checkedOutItems) {
            if (checkedOutItem.equals(item)) {
                return true;
            }
        }
        return false;
    }
}
