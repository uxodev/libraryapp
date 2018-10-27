package project.libraryapp.library;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

import project.libraryapp.items.Item;

/**
 * Creates and maintains a list of items at this library.
 * Allows items to be added to it via JSON or XML files.
 * Allows items to be checked out and checked in.
 * Allows all items in the library catalog to be displayed by type.
 */
public class Library implements Serializable {

    private HashMap<String, Item> itemList = new HashMap<>();
    private Type libraryType = null;

    public Library(Type type) {
        this.libraryType = type;
    }

    /**
     * handles checking out an item
     * sets the item's available flag false
     * sets the item's setDateDue to current date plus item's checkout time
     *
     * @param itemId id of the item to check out
     * @return null if item does not exist, false if item is not available, otherwise true
     */
    public Boolean checkOut(String itemId) {
        Item item = this.getItem(itemId);
        if (item == null) {
            return null;
        } else if (!item.isAvailable()) {
            return false;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, item.getCheckOutTimeDays());
            item.setAvailable(false);
            item.setDateDue(cal);
            item.setStatus(Item.Status.CHECKED_OUT);
            return true;
        }
    }

    /**
     * handles checking in an item
     * sets the item's available flag true
     * sets the item's setDateDue to null
     *
     * @param itemId id of the item to check in
     * @return null if item does not exist, false if item is available, otherwise true
     */
    public Boolean checkIn(String itemId) {
        Item item = itemList.get(itemId);
        if (item == null) {
            return null;
        } else if (item.isAvailable()) {
            return false;
        } else {
            item.setStatus(Item.Status.SHELVING);
            item.setDateDue(null);
            return true;
        }
    }

    /**
     * handles changing the status of an item
     * sets the item's status to the input status
     *
     * @param itemId id of the item to check in
     * @param status status to change
     * @return null if item does not exist, otherwise true
     */
    public Boolean changeStatus(String itemId, Item.Status status) {
        Item item = itemList.get(itemId);
        if (item == null) {
            return false;
        } else {
            if (status == Item.Status.CHECK_STATUS)
                return true;
            item.setStatus(status);
            if (status == Item.Status.CHECKED_IN) {
                item.setAvailable(true);
            }
            if (status == Item.Status.MISSING || status == Item.Status.REFERENCE_ONLY || status == Item.Status.REMOVED_FROM_CIRCULATION
                    || status == Item.Status.SHELVING) {
                item.setAvailable(false);
            }

            return true;
        }
    }

    /**
     * Displays items of given type in this Library's catalog.
     *
     * @param type the type of the items to display
     * @return text to display to user
     */
    public String displayItemsOfType(Item.Type type) {
        String message = "";
        for (Item value : itemList.values()) {
            if (type.equals(value.getType())) {
                message += "\n" + value.toString() + " Status : " + value.getStatus();
            }
        }
        return message;
    }

    /**
     * Gets item from the list of items in this library.
     *
     * @param id item to get
     * @return the item object
     */
    public Item getItem(String id) {
        return itemList.get(id);
    }

    /**
     * Gets library type (main or sister)
     *
     * @return Main or Sister
     */
    public Type getLibraryType() {
        return libraryType;
    }

    /**
     * Adds item to the list of items in this library.
     *
     * @param item The item object to be added to list
     **/
    public void addItem(Item item) {
        itemList.put(item.getId(), item);
    }

    public int size() {
        return itemList.size();
    }

    public enum Type {
        MAIN, SISTER
    }
}
