package project.libraryapp.member;

import java.io.Serializable;
import java.util.HashMap;

import project.libraryapp.items.Item;

/**
 * Creates and maintains list of member objects.
 */
public class MemberList implements Serializable {
    private HashMap<Integer, Member> memberList = new HashMap<>();
    private MemberIdServer idServer = MemberIdServer.instance();

    public MemberList() {
    }

    /**
     * Creates and adds a member to memberList with a generated library card number.
     *
     * @param name name of new member
     * @return member that was added
     */
    public Member createMember(String name) {
        int id = idServer.getId();
        Member member = new Member(id, name);
        memberList.put(id, member);
        return member;
    }

    public Member getMember(int id) {
        return memberList.get(id);
    }

    /**
     * Get the member with this item checked out.
     *
     * @param item find the member who has this item
     * @return the member who has the item
     */
    public Member getMemberWithItem(Item item) {
        for (Member member : memberList.values()) {
            if (member.hasItem(item)) {
                return member;
            }
        }
        return null;
    }

    public int getNumberOfMembers() {
        return memberList.size();
    }
}
