package project.libraryapp.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Scanner;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import project.libraryapp.items.Book;
import project.libraryapp.items.Cd;
import project.libraryapp.items.Dvd;
import project.libraryapp.items.Item;
import project.libraryapp.items.Magazine;
import project.libraryapp.library.Library;
import project.libraryapp.member.Member;
import project.libraryapp.member.MemberIdServer;
import project.libraryapp.member.MemberList;
import project.libraryapp.storage.Storage;

/**
 * Creates and maintains the Library objects and MemberList object.
 * Handles checkIn, checkOut, displayLibraryItems,
 * addFileData, and displayMemberCheckedOutItems functionality
 * between the ui and the appropriate objects.
 */
public class Controller implements Serializable {
    private Library main = new Library(Library.Type.MAIN);
    private Library sister = new Library(Library.Type.SISTER);
    private MemberList memberList = new MemberList();
    private String savePath;

    public Controller() {
    }

    public Controller(String savePath) {
        this.savePath = savePath;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /**
     * Checks out the item to the ui set library by given cardNumber.
     *
     * @param cardNumber member's cardNumber who will check out the item
     * @param itemId     id of the item to check out
     * @param library    library type of where the item is
     * @return text to display to user
     */
    public String checkOut(int cardNumber, String itemId, Library.Type library) {
        String message = "";
        Library lib = getLib(library);
        Boolean isCheckedIn = lib.checkOut(itemId);

        Member member = this.memberList.getMember(cardNumber);
        if (member != null) {
            if (isCheckedIn == null)
                message += "\n\n***** Item " + itemId + " does not exist *****";
            else if (!isCheckedIn)
                message += "\n\n***** Item " + itemId + " is currently not available for checkout. *****";
            else {
                member.addItem(lib.getItem(itemId));
                message += "\n\n***** Checkout Successful *****\n";
                message += lib.getItem(itemId).toString() + " Status : " + lib.getItem(itemId).getStatus();
            }
        } else
            message += "\n\n***** Library card number " + cardNumber + " is invalid *****";

        Storage.save(this, savePath);
        return message;
    }

    /**
     * Checks in the item to the ui set library.
     *
     * @param itemId  id of the item to check in
     * @param library library type of where the item is
     * @return text to display to user
     */
    public String checkIn(String itemId, Library.Type library) {
        String message = "";
        Library lib = getLib(library);
        Boolean isCheckedOut = lib.checkIn(itemId);

        if (isCheckedOut == null)
            message += "\n\n***** Item " + itemId + " does not exist *****";
        else if (!isCheckedOut)
            message += "\n\n***** Item " + itemId + " is not checked out. *****";
        else {
            try {
                memberList.getMemberWithItem(lib.getItem(itemId)).removeItem(lib.getItem(itemId));
                message += "\n\n***** Checkin Successful *****\n";
                message += lib.getItem(itemId).toString() + " Status : " + lib.getItem(itemId).getStatus();
            } catch (NullPointerException e) {
                message += "\n\n***** Error: Item " + itemId + " is marked as checked out but no member has it checked out. *****";
            }
        }
        Storage.save(this, savePath);
        return message;
    }

    /**
     * Checks in the item to the ui set library.
     *
     * @param itemId  id of the item to check in
     * @param library library type of where the item is
     * @return text to display to user
     */
    public String changeItemStatus(String itemId, Item.Status status, Library.Type library) {
        Library lib = getLib(library);
        Item item = lib.getItem(itemId);
        String message = "";

        if (status == Item.Status.CHECKED_IN && item.getStatus() == Item.Status.CHECKED_OUT) {
            return checkIn(itemId, library);
        }
        if (!lib.changeStatus(itemId, status))
            message += "\n\n***** Item " + itemId + " does not exist *****";
        else {
            message += "\n\n***** Status change successful *****\n";
            message += lib.getItem(itemId).toString() + " Status : " + lib.getItem(itemId).getStatus();
        }
        Storage.save(this, savePath);
        return message;
    }

    /**
     * Adds items from input file to appropriate library.
     *
     * @param input   file to read data from
     * @param library library type of where the item is
     */
    public boolean addFileData(InputStream input, String fileType, Library.Type library) {
        Library lib = getLib(library);
        boolean success = false;
        if (fileType.toLowerCase().endsWith("json")) {
            success = addFileDataJson(input, lib);
        } else if (fileType.toLowerCase().endsWith("xml")) {
            success = addFileDataXml(input, lib);
        } else {
            System.out.println("Error: Invalid file type entered.");
            success = false;
        }
        Storage.save(this, savePath);
        return success;
    }

    /**
     * Reads a JSON file and adds items to ui set library.
     * Detects bad file entries and reports them.
     *
     * @param input file to read data from.
     * @param lib   library where the item is
     * @return false if file can't be read
     */
    boolean addFileDataJson(InputStream input, Library lib) {
        // id will hold the Item id from the parsed file
        String id = "";
        // type will hold the Item subtype: book, cd, dvd, or magazine
        String type = "";
        // name holds the title of the object, e.g. book name, album title, magazine title
        String name = "";
        // optionField holds an optional volume value that might be provided with books, magazines, and CDs
        String optionalField = "";
        // textLine holds all of the JSON after it iterates through the file.
        String textLine = "";
        // keyName holds the key name of one key-value pair while parsing the JSON file.
        String keyName = "";
        // value holds the value of one key-value pair while parsing the JSON file.
        String value = "";
        // startArray flags true when we start iterating through the JSON file (checking START_ARRAY);
        // goes through and parses the objects out of the file while still true, and then
        // it flags back to false once we are done going through and the JSONParser event is END_ARRAY.
        boolean startArray = false;

        Scanner scanner = new Scanner(input);
        try {
            while (scanner.hasNext()) {
                textLine = textLine + scanner.nextLine() + "\n";
            }
        } catch (NullPointerException e) {
            System.out.println("Couldn't find file");
        }
        scanner.close();

        JsonParser parser = Json.createParser(new StringReader(textLine));
        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            switch (event) {
                case START_ARRAY:
                    startArray = true;
                    break;
                case END_ARRAY:
                    startArray = false;
                    break;
                case END_OBJECT:
                    if (startArray) {
                        // Checks that we are not missing any id, name, and type values for the object
                        if (id != null && name != null && type != null && !id.equals("") && !name.equals("") && !type.equals("")) {
                            switch (type.toLowerCase()) {
                                case "cd":
                                    lib.addItem(new Cd(id, name, Item.Type.CD, optionalField));
                                    break;
                                case "book":
                                    lib.addItem(new Book(id, name, Item.Type.BOOK, optionalField));
                                    break;
                                case "magazine":
                                    lib.addItem(new Magazine(id, name, Item.Type.MAGAZINE, optionalField));
                                    break;
                                case "dvd":
                                    lib.addItem(new Dvd(id, name, Item.Type.DVD));
                                    break;
                                default:
                                    System.out.println("\nInvalid type in entry: ID = " + id + " , " +
                                            "Type = " + type + ", " + "Name = " + name);
                                    break;
                            }
                        } else {
                            System.out.println("\nMissing data in entry: ID = " + id + " , " +
                                    "Type = " + type + ", " + "Name = " + name);
                        }
                    }
                    break;
                // Clear the values to empty when parsing a new object
                case START_OBJECT:
                    id = "";
                    name = "";
                    type = "";
                    optionalField = "";
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_TRUE:
                    break;
                case KEY_NAME:
                    keyName = parser.getString();
                    break;
                case VALUE_STRING:
                case VALUE_NUMBER:
                    value = parser.getString();
                    switch (keyName.toLowerCase()) {
                        case "item_name":
                            name = value;
                            break;
                        case "item_type":
                            type = value;
                            break;
                        case "item_id":
                            id = value;
                            break;
                        case "item_artist":
                        case "item_author":
                        case "item_volume":
                            optionalField = value;
                            break;
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * Reads a XML file and adds items to ui set library.
     * Detects bad file entries and reports them.
     *
     * @param input file to read data from
     * @param lib   library where the item is
     * @return false if file can't be read
     */
    boolean addFileDataXml(InputStream input, Library lib) {
        // id will hold the Item id of a DOM object from the parsed file
        String id = "";
        // type will hold the Item subtype: book, cd, dvd, or magazine
        String type = "";
        // name holds the title of the object, e.g. book name, album title, magazine title
        String name = "";
        // author holds the author value for books
        String author = "";
        // artist holds the artist value for CDs
        String artist = "";
        // volume holds the volume # for magazine
        String volume = "";
        // doc is the instantiation of the object for the XML file
        Document doc = null;

        try {
            // dbFactory is an abstract class, using a factory API
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            // dBuilder created using the DocumentBuilderFactor instance
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // parses the XML file using the DocumentBuilder and returns a DOM Document object
            doc = dBuilder.parse(input);
            // getDocumentElement directly accesses the child node of the document element, and normalize
            // makes it so the text of a given node is combined and only separated by the actual structure
            // of the text nodes. It eliminates any empty text that's part of the node, and combines
            // the multiple lines of it.
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Item");

            // Iterate through the list of nodes pulled from the DOM Document object. Add valid items to the library,
            // and print out a warning when the nodes are missing mandatory information.
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    id = "";
                    type = "";
                    name = "";
                    author = "";
                    artist = "";
                    volume = "";

                    try {
                        id = eElement.getAttribute("id");
                        type = eElement.getAttribute("type");
                        name = eElement.getElementsByTagName("Name").item(0).getTextContent();
                    }
                    // If one of the mandatory attributes is null, don't add it.
                    catch (NullPointerException e) {
                        System.out.println("\nEntry missing an ID, type, and/or name: ID = " + id + " , " +
                                "Type = " + type + ", " + "Name = " + name);
                        continue;
                    }
                    // author field is optional
                    if (type.toLowerCase().equals("book"))
                        author = eElement.getElementsByTagName("Author").item(0).getTextContent();
                    // artist field is optional
                    if (type.toLowerCase().equals("cd"))
                        artist = eElement.getElementsByTagName("Artist").item(0).getTextContent();
                    // volume field is optional
                    if (type.toLowerCase().equals("magazine"))
                        volume = eElement.getElementsByTagName("Volume").item(0).getTextContent();

                    if (id != null && name != null && type != null && !id.equals("") && !name.equals("") && !type.equals("")) {
                        switch (type.toLowerCase()) {
                            case "cd":
                                lib.addItem(new Cd(id, name, Item.Type.CD, artist));
                                break;
                            case "book":
                                lib.addItem(new Book(id, name, Item.Type.BOOK, author));
                                break;
                            case "magazine":
                                lib.addItem(new Magazine(id, name, Item.Type.MAGAZINE, volume));
                                break;
                            case "dvd":
                                lib.addItem(new Dvd(id, name, Item.Type.DVD));
                                break;
                            default:
                                System.out.println("\nInvalid type in entry: ID = " + id + " , " +
                                        "Type = " + type + ", " + "Name = " + name);
                                break;
                        }
                    } else {
                        System.out.println("\nInvalid entry data: ID = " + id + " , " +
                                "Type = " + type + ", " + "Name = " + name);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Couldn't find file");
            return false;
        } catch (ParserConfigurationException e) {
            System.out.println("Error in parsing XML file. No items added.");
            return false;
        } catch (SAXException e) {
            System.out.println("Error in parsing XML file. No items added.");
            return false;
        }
        return true;
    }

    /**
     * Adds a member to memberList with a generated library card number.
     *
     * @param name name of new member
     * @return text to display to user
     */
    public String addMember(String name) {
        String message = "";

        Member member = this.memberList.createMember(name);
        message += ("\n\n***** New Member: " + member.getName().trim() + " Created *****" +
                "\n-- Library card number is: " + member.getLibraryCardNumber());

        Storage.save(this, MemberIdServer.instance(), savePath);
        return message;
    }

    /**
     * Displays items in library catalog by type.
     *
     * @param mask    mask of types to display: 1 = book, 2 = cd, 4 = dvd, 8 = magazine
     * @param library library where the item is
     * @return text to display to user
     */
    public String displayLibraryItems(int mask, Library.Type library) {
        String message = "";
        Library lib = getLib(library);
        if (mask == 0) {
            message += "\n\n***** No Item Type Selected *****";
        } else {
            message += ("Items in Library " + lib.getLibraryType());
            message += "\n---------------------------------------------------------------------------------------------------------";
            if ((mask & 1) == 1) {
                message += "\n\nBooks\n----------";
                message += lib.displayItemsOfType(Item.Type.BOOK);
            }
            if ((mask & 2) == 2) {
                message += "\n\nCDs\n----------";
                message += lib.displayItemsOfType(Item.Type.CD);
            }
            if ((mask & 4) == 4) {
                message += "\n\nDVDs\n----------";
                message += lib.displayItemsOfType(Item.Type.DVD);
            }
            if ((mask & 8) == 8) {
                message += "\n\nMagazines\n----------";
                message += lib.displayItemsOfType(Item.Type.MAGAZINE);
            }
            message += "\n---------------------------------------------------------------------------------------------------------";
        }
        return message;
    }

    /**
     * Gets the checked out items for this member.
     *
     * @param cardNumber member's cardNumber whose items will be displayed
     * @return text to display to user
     */
    public String displayMemberCheckedOutItems(int cardNumber) {
        String message = "";

        Member member = this.memberList.getMember(cardNumber);
        if (member != null) {
            ArrayList<Item> items = member.getCheckedOutItems();

            message += ("Items checked out by " + member.getName() + " - Member #: " + cardNumber);
            message += "\n---------------------------------------------------------------------------------------------------------";
            for (Item item : items) {
                message += "\n" + item.toString();
            }
            message += "\n---------------------------------------------------------------------------------------------------------";
        } else
            message += ("\n\n***** Library card number " + cardNumber + " is invalid *****\n");
        return message;
    }

    /**
     * Returns the library object designated by the library type.
     *
     * @param library library type
     * @return the library object
     */
    Library getLib(Library.Type library) {
        switch (library) {
            case MAIN:
                return main;
            case SISTER:
                return sister;
            default:
                return null;
        }
    }

    // for testing
    void addItemToLibrary(Item addThisItem, Library.Type library) {
        getLib(library).addItem(addThisItem);
    }
}
