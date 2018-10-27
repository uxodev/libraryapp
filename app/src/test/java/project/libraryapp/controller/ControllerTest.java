package project.libraryapp.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import project.libraryapp.items.Item;
import project.libraryapp.library.Library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ControllerTest {
    private static Controller testController;

    private Item itemAvailable;
    private Item itemCheckedOutBy1;
    private Item itemNotAvailableAndNotCheckedOut;

    private Item itemBookAtMain;
    private Item itemMagazineAtMain;
    private Item itemCdAtMain;
    private Item itemDvdAtMain;
    private Item itemBookAtSister;
    private Item itemMagazineAtSister;
    private Item itemCdAtSister;
    private Item itemDvdAtSister;

    @BeforeClass
    public static void setUpClass() {
        testController = new Controller(System.getProperty("user.dir") + "\\");
        testController.addMember("Member 1");
        testController.addMember("Member 2");
    }

    @Before
    public void setUp() throws Exception {
        itemAvailable = new Item("itemAvailable", "item1", Item.Type.BOOK);
        itemAvailable.setAvailable(true);
        testController.addItemToLibrary(itemAvailable, Library.Type.MAIN);

        itemCheckedOutBy1 = new Item("itemCheckedOutBy1", "item2", Item.Type.BOOK);
        testController.addItemToLibrary(itemCheckedOutBy1, Library.Type.MAIN);
        testController.checkOut(1, "itemCheckedOutBy1", Library.Type.MAIN);

        itemNotAvailableAndNotCheckedOut = new Item("itemNotAvailableAndNotCheckedOut", "item3", Item.Type.BOOK);
        testController.addItemToLibrary(itemNotAvailableAndNotCheckedOut, Library.Type.MAIN);
        testController.getLib(Library.Type.MAIN).getItem("itemNotAvailableAndNotCheckedOut").setAvailable(false);

        itemBookAtMain = new Item("Book1", "Book1", Item.Type.BOOK);
        testController.addItemToLibrary(itemBookAtMain, Library.Type.MAIN);
        testController.checkOut(2, "Book1", Library.Type.MAIN);
        itemMagazineAtMain = new Item("Magazine1", "Magazine1", Item.Type.MAGAZINE);
        testController.addItemToLibrary(itemMagazineAtMain, Library.Type.MAIN);
        testController.checkOut(2, "Magazine1", Library.Type.MAIN);
        itemCdAtMain = new Item("CD1", "CD1", Item.Type.CD);
        testController.addItemToLibrary(itemCdAtMain, Library.Type.MAIN);
        testController.checkOut(2, "CD1", Library.Type.MAIN);
        itemDvdAtMain = new Item("DVD1", "DVD1", Item.Type.DVD);
        testController.addItemToLibrary(itemDvdAtMain, Library.Type.MAIN);
        testController.checkOut(2, "DVD1", Library.Type.MAIN);

        itemBookAtSister = new Item("Book2", "Book2", Item.Type.BOOK);
        testController.addItemToLibrary(itemBookAtSister, Library.Type.SISTER);
        testController.checkOut(2, "Book2", Library.Type.SISTER);
        itemMagazineAtSister = new Item("Magazine2", "Magazine2", Item.Type.MAGAZINE);
        testController.addItemToLibrary(itemMagazineAtSister, Library.Type.SISTER);
        testController.checkOut(2, "Magazine2", Library.Type.SISTER);
        itemCdAtSister = new Item("CD2", "CD2", Item.Type.CD);
        testController.addItemToLibrary(itemCdAtSister, Library.Type.SISTER);
        testController.checkOut(2, "CD2", Library.Type.SISTER);
        itemDvdAtSister = new Item("DVD2", "DVD2", Item.Type.DVD);
        testController.addItemToLibrary(itemDvdAtSister, Library.Type.SISTER);
        testController.checkOut(2, "DVD2", Library.Type.SISTER);
    }

    @After
    public void tearDown() throws Exception {
        Path path = Paths.get(System.getProperty("user.dir") + "/ControllerData.bin");
        Files.deleteIfExists(path);
        path = Paths.get(System.getProperty("user.dir") + "/MemberServerData.bin");
        Files.deleteIfExists(path);
    }

    @Test
    public void checkOut() throws Exception {
        String response = "";
        // check out item that doesn't exist
        response = testController.checkOut(1, "BadItemID", Library.Type.MAIN);
        assertTrue(response.contains("does not exist"));

        // check out item already checked out
        response = testController.checkOut(1, "itemCheckedOutBy1", Library.Type.MAIN);
        assertTrue(response.contains("not available for checkout"));

        // use invalid card number
        response = testController.checkOut(99, "itemCheckedOutBy1", Library.Type.MAIN);
        assertTrue(response.contains("is invalid"));

        // test success
        response = testController.checkOut(1, "itemAvailable", Library.Type.MAIN);
        assertTrue(response.contains("Checkout Successful"));
    }

    @Test
    public void checkIn() throws Exception {
        String response = "";
        // test error condition
        response = testController.checkIn("itemNotAvailableAndNotCheckedOut", Library.Type.MAIN);
        assertTrue(response.contains("marked as checked out but no member has it checked out"));

        // check in item that doesn't exist
        response = testController.checkIn("BadItemID", Library.Type.MAIN);
        assertTrue(response.contains("does not exist"));

        // check in item that is not checked out
        response = testController.checkIn("itemAvailable", Library.Type.MAIN);
        assertTrue(response.contains("not checked out"));

        // test success
        response = testController.checkIn("itemCheckedOutBy1", Library.Type.MAIN);
        assertTrue(response.contains("Checkin Successful"));
    }

    @Test
    public void addFileData() throws Exception {
        testAddFileDataJson();
        testAddFileDataXml();
    }

    @Test
    public void testAddFileDataJson() {
        // Initialize the lib object to null
        Library lib = new Library(Library.Type.MAIN);

        //
        // Test for a wrong file type
        //
        File file = new File(System.getProperty("user.dir") + "/assets/TestWrongFileType.txt");
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        testController.addFileData(input, "json", Library.Type.MAIN);
        //
        // Now test with a valid file that has three entries that have errors :
        // 1) No ID (ID = "")
        // 2) Invalid type = VHS
        // 3) Name = null (No name field)
        // It then checks that 8 items were added
        //
        file = new File(System.getProperty("user.dir") + "/assets/Test.json");
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        testController.addFileDataJson(input, lib);
        assertEquals(8, lib.size());
        assertEquals("The Stand", lib.getItem("1adf5").getName());
    }

    @Test
    public void testAddFileDataXml() {
        // Initialize the lib object to null

        Library lib = new Library(Library.Type.MAIN);

        //
        // Test with a valid file that has three entries that have errors :
        // 1) No ID (ID = "")
        // 2) Invalid type = VHS
        // 3) Name = null (No name field)
        // It then checks that 4 items were added
        //
        File file = new File(System.getProperty("user.dir") + "/assets/Test.xml");
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        testController.addFileDataXml(input, lib);
        assertEquals("Number of items added is incorrect: ", 4, lib.size());
    }

    @Test
    public void addMember() throws Exception {
        String response = "";
        response = testController.addMember("testMember");
        assertTrue(response.contains("Library card number is: 3"));
    }

    @Test
    public void displayLibraryItems() throws Exception {
        // Test no item types selected
        String response = "";
        response = testController.displayLibraryItems(0, Library.Type.MAIN);
        assertTrue(response.contains("***** No Item Type Selected *****"));
        // Test when all item types selected
        response = testController.displayLibraryItems(1 + 2 + 4 + 8, Library.Type.MAIN);
        assertTrue(response.contains("Book1"));
        assertTrue(response.contains("Magazine1"));
        assertTrue(response.contains("CD1"));
        assertTrue(response.contains("DVD1"));
    }

    @Test
    public void displayMemberCheckedOutItems() throws Exception {
        // Test for an invalid member number
        String response = "";
        response = testController.displayMemberCheckedOutItems(200);
        assertTrue(response.contains("***** Library card number "));
        assertTrue(response.contains("***** Library card number 200 is invalid *****"));
        // Test with valid member number
        response = testController.displayMemberCheckedOutItems(2);
        assertTrue(response.contains("Book1"));
        assertTrue(response.contains("Magazine1"));
        assertTrue(response.contains("CD1"));
        assertTrue(response.contains("DVD1"));
        assertTrue(response.contains("Book2"));
        assertTrue(response.contains("Magazine2"));
        assertTrue(response.contains("CD2"));
        assertTrue(response.contains("DVD2"));
    }
}
