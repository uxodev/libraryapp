package project.libraryapp.member;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import project.libraryapp.items.Item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MemberListTest {
    MemberList memberlist;
    Member member1, member2, member3;
    Item item1, item2, item3;

    @Before
    public void setUp() throws Exception {
        memberlist = new MemberList();
        item1 = new Item("3g", "Science", Item.Type.DVD);
        item2 = new Item("4g", "Philosphy", Item.Type.MAGAZINE);
        item3 = new Item("5g", "Engineering", Item.Type.BOOK);
    }

    @Test
    public void test() {
        member1 = this.memberlist.createMember("Jim");
        assertTrue(member1 instanceof Member);

        member2 = this.memberlist.createMember("Sam");
        member3 = this.memberlist.createMember("Bill");
        assertEquals(3, this.memberlist.getNumberOfMembers());
        assertTrue(member1.equals(this.memberlist.getMember(1)));
        assertEquals("Jim", member1.getName());
        assertEquals(1, member1.getLibraryCardNumber());
        assertTrue(member2.equals(this.memberlist.getMember(2)));
        assertEquals("Sam", member2.getName());

        this.member1.addItem(this.item1);
        this.member1.addItem(this.item2);
        this.member3.addItem(this.item3);
        assertTrue(this.member1.getCheckedOutItems() instanceof ArrayList<?>);

        ArrayList<Item> member1ItemList = this.member1.getCheckedOutItems();
        assertEquals(item2, member1ItemList.get(1));
        assertEquals(this.member3, this.memberlist.getMemberWithItem(item3));

        this.member3.removeItem(item3);
        assertFalse(this.member3.hasItem(item3));
        assertEquals(null, this.memberlist.getMemberWithItem(item3));
    }
}
