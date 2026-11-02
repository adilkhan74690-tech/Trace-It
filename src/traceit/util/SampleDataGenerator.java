package traceit.util;

import traceit.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SampleDataGenerator {

    public static List<LostItem> generateSampleLostItems() {
        List<LostItem> list = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.of(2026, 10, 28, 10, 0, 0);

        // 1. Aditya Sharma - Black Leather Wallet (Returned)
        User u1 = new User("U101", "Aditya Sharma", "aditya.sharma@campus.edu", "+91-98765-43210", "Student");
        LostItem item1 = new LostItem(
                "LST-1001",
                "Black Leather Wallet",
                ItemCategory.WALLET,
                "Black",
                "Wildhorn",
                new Location("Central Library"),
                baseTime.plusHours(4).plusMinutes(30), // Oct 28 14:30
                "Black bifold leather wallet with coin pocket and student ID slot",
                "White perimeter stitch along edge, contains college ID",
                u1,
                200.0,
                "Phone"
        );
        item1.setStatus(ItemStatus.RETURNED);
        list.add(item1);

        // 2. Arjun Verma - Samsung Smartphone (Possible Match)
        User u2 = new User("U102", "Arjun Verma", "arjun.verma@campus.edu", "+91-98765-43211", "Student");
        LostItem item2 = new LostItem(
                "LST-1002",
                "Samsung Galaxy Smartphone",
                ItemCategory.SMARTPHONE,
                "Phantom Black",
                "Samsung",
                new Location("Computer Science Block"),
                baseTime.plusDays(3).plusHours(1).plusMinutes(15), // Oct 31 11:15
                "Samsung Galaxy M34 with matte black ring case and matte screen protector",
                "Small scratch near charging port, Spider-Man sticker on back",
                u2,
                500.0,
                "Email"
        );
        item2.setStatus(ItemStatus.POSSIBLE_MATCH);
        list.add(item2);

        // 3. Priya Singh - Boat Wireless Earbuds (Lost / Active)
        User u3 = new User("U103", "Priya Singh", "priya.singh@campus.edu", "+91-98765-43212", "Student");
        list.add(new LostItem(
                "LST-1003",
                "Boat Airdopes Wireless Earbuds",
                ItemCategory.EARBUDS,
                "Active Black",
                "Boat",
                new Location("Main Canteen"),
                baseTime.plusDays(4).plusHours(3).plusMinutes(40), // Nov 01 13:40
                "Boat wireless bluetooth earbuds inside matte black charging case",
                "Small green anime keychain attached to case loop",
                u3,
                100.0,
                "Email"
        ));

        // 4. Riya Malhotra - Student ID Card (Returned)
        User u4 = new User("U104", "Riya Malhotra", "riya.malhotra@campus.edu", "+91-98765-43213", "Student");
        LostItem item4 = new LostItem(
                "LST-1004",
                "Student ID Card & Lanyard",
                ItemCategory.ID_CARD,
                "Navy Blue",
                "Campus Admin",
                new Location("Seminar Hall"),
                baseTime.plusDays(1).plusHours(6).plusMinutes(20), // Oct 29 16:20
                "College RFID smart identity card with blue department ribbon lanyard",
                "Roll number 23BCS042 printed, CS Department stamp",
                u4,
                50.0,
                "Phone"
        );
        item4.setStatus(ItemStatus.RETURNED);
        list.add(item4);

        // 5. Karan Patel - HP Laptop Charger (Lost / Active)
        User u5 = new User("U105", "Karan Patel", "karan.patel@campus.edu", "+91-98765-43214", "Student");
        list.add(new LostItem(
                "LST-1005",
                "HP Laptop Power Adapter",
                ItemCategory.LAPTOP_CHARGER,
                "Black",
                "HP",
                new Location("Electronics Laboratory"),
                baseTime.plusDays(4).minusMinutes(30), // Nov 01 09:30
                "65W blue-pin HP laptop charging adapter with velcro cable tie",
                "Yellow electrical tape wrapped near connector",
                u5,
                0.0,
                "Email"
        ));

        // 6. Ananya Gupta - Blue Backpack (Lost / Active - No Match)
        User u6 = new User("U106", "Ananya Gupta", "ananya.gupta@campus.edu", "+91-98765-43215", "Student");
        list.add(new LostItem(
                "LST-1006",
                "Blue College Backpack",
                ItemCategory.BACKPACK,
                "Navy Blue",
                "Skybags",
                new Location("College Parking Area"),
                baseTime.plusDays(2).plusHours(7).plusMinutes(45), // Oct 30 17:45
                "Skybags college backpack containing engineering physics and calculus notebooks",
                "Red zipper pull tag, small tear on side bottle mesh",
                u6,
                150.0,
                "Phone"
        ));

        // 7. Rahul Mehta - Stainless Steel Water Bottle (Lost / Active)
        User u7 = new User("U107", "Rahul Mehta", "rahul.mehta@campus.edu", "+91-98765-43216", "Student");
        list.add(new LostItem(
                "LST-1007",
                "Stainless Steel Water Bottle",
                ItemCategory.WATER_BOTTLE,
                "Silver",
                "Milton",
                new Location("Sports Ground"),
                baseTime.plusDays(4).plusHours(7).plusMinutes(10), // Nov 01 17:10
                "750ml insulated stainless steel water flask with black screw cap",
                "ISRO mission sticker on the side",
                u7,
                50.0,
                "Email"
        ));

        // 8. Simran Kaur - Casio Wristwatch (Verified)
        User u8 = new User("U108", "Simran Kaur", "simran.kaur@campus.edu", "+91-98765-43217", "Student");
        LostItem item8 = new LostItem(
                "LST-1008",
                "Casio Vintage Wristwatch",
                ItemCategory.WATCH,
                "Silver",
                "Casio",
                new Location("Girls Hostel Block A"),
                baseTime.minusDays(1).plusHours(10).plusMinutes(15), // Oct 27 20:15
                "Casio vintage digital stainless steel wristwatch with rectangular dial",
                "Scratch across bottom right corner of display glass",
                u8,
                200.0,
                "Phone"
        );
        item8.setStatus(ItemStatus.VERIFIED);
        list.add(item8);

        // 9. Aditya Sharma - Hostel Room Keys (Lost / Active - No Match)
        list.add(new LostItem(
                "LST-1009",
                "Hostel Room Brass Keys",
                ItemCategory.KEYS,
                "Brass Gold",
                "Godrej",
                new Location("Boys Hostel Block B"),
                baseTime.plusDays(3).plusHours(12), // Oct 31 22:00
                "Pair of 2 Godrej brass door keys on a metal loop with bike key",
                "Blue silicone key cover on the room key",
                u1,
                100.0,
                "Phone"
        ));

        // 10. Karan Patel - Notebook (Lost / Active - No Match)
        list.add(new LostItem(
                "LST-1010",
                "Data Structures Spiral Notebook",
                ItemCategory.OTHER,
                "Brown / Kraft",
                "Classmate",
                new Location("Central Library"),
                baseTime.plusDays(4).plusHours(5).plusMinutes(30), // Nov 01 15:30
                "A4 spiral bound lecture notebook containing CS201 Data Structures notes and diagrams",
                "Name Karan Patel written on first page header in blue ink",
                u5,
                50.0,
                "Email"
        ));

        return list;
    }

    public static List<FoundItem> generateSampleFoundItems() {
        List<FoundItem> list = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.of(2026, 10, 28, 10, 0, 0);

        // 1. Rajesh Kumar (Staff) - Black Leather Wallet (Returned)
        User f1 = new User("F201", "Rajesh Kumar", "library.desk@campus.edu", "+91-98765-00101", "Staff");
        FoundItem item1 = new FoundItem(
                "FND-1001",
                "Black Wildhorn Leather Wallet",
                ItemCategory.WALLET,
                "Black",
                "Wildhorn",
                new Location("Central Library"),
                baseTime.plusHours(4).plusMinutes(45), // Oct 28 14:45
                "Black leather wallet turned in by student helper, contains cards and student ID",
                "White perimeter stitching, silver coin zip",
                f1,
                "Library Circulation Counter Safe",
                "What name and branch are printed on the ID inside?"
        );
        item1.setStatus(ItemStatus.RETURNED);
        list.add(item1);

        // 2. Deepak Verma (Staff) - Samsung Mobile Phone (Possible Match)
        User f2 = new User("F202", "Deepak Verma", "electronics.lab@campus.edu", "+91-98765-00104", "Staff");
        FoundItem item2 = new FoundItem(
                "FND-1002",
                "Black Samsung Mobile Phone",
                ItemCategory.SMARTPHONE,
                "Black",
                "Samsung",
                new Location("Computer Science Block"),
                baseTime.plusDays(3).plusHours(1).plusMinutes(45), // Oct 31 11:45
                "Samsung smartphone in black protective ring case with patterned back",
                "Spider-Man graphic sticker on rear case",
                f2,
                "CS Block HOD Office Cupboard",
                "What is the lock screen wallpaper and pattern lock?"
        );
        item2.setStatus(ItemStatus.POSSIBLE_MATCH);
        list.add(item2);

        // 3. Sunita Sharma (Staff) - Student ID Card (Returned)
        User f3 = new User("F203", "Sunita Sharma", "canteen.admin@campus.edu", "+91-98765-00103", "Staff");
        FoundItem item3 = new FoundItem(
                "FND-1003",
                "Student Identity Card",
                ItemCategory.ID_CARD,
                "Blue",
                "Campus Admin",
                new Location("Seminar Hall"),
                baseTime.plusDays(1).plusHours(7), // Oct 29 17:00
                "Official college ID card with lanyard found after guest lecture",
                "CS Department lanyard, photograph visible",
                f3,
                "Campus Security Office Registry",
                "State the roll number and father's name on card."
        );
        item3.setStatus(ItemStatus.RETURNED);
        list.add(item3);

        // 4. Vikram Singh (Security) - White Noise Earbuds (Found / Active - Weak Match)
        User f4 = new User("F204", "Vikram Singh", "security.office@campus.edu", "+91-98765-00102", "Security");
        list.add(new FoundItem(
                "FND-1004",
                "Noise White Earbuds Case",
                ItemCategory.EARBUDS,
                "White",
                "Noise",
                new Location("Sports Ground"),
                baseTime.plusDays(2).plusHours(8), // Oct 30 18:00
                "White Noise wireless earbuds case found on bleachers",
                "Plain white case without any keychain or skin",
                f4,
                "Security Lost & Found Vault Room 102",
                "What is the model name printed on the bottom?"
        ));

        // 5. Deepak Verma (Staff) - HP Laptop Power Supply (Found / Active)
        list.add(new FoundItem(
                "FND-1005",
                "HP Laptop Power Supply",
                ItemCategory.LAPTOP_CHARGER,
                "Black",
                "HP",
                new Location("Electronics Laboratory"),
                baseTime.plusDays(4).plusMinutes(15), // Nov 01 10:15
                "Black HP 65W AC power adapter with round pin connector",
                "Yellow electrical tape wrapped near barrel jack",
                f2,
                "Electronics Lab Component Cabinet B",
                "What markings or tape are wrapped on the wire?"
        ));

        // 6. Vikram Singh (Security) - Silver Steel Water Bottle (Found / Active)
        list.add(new FoundItem(
                "FND-1006",
                "Silver Steel Water Bottle",
                ItemCategory.WATER_BOTTLE,
                "Silver",
                "Milton",
                new Location("Sports Ground"),
                baseTime.plusDays(4).plusHours(7).plusMinutes(40), // Nov 01 17:40
                "Stainless steel water bottle with black screw top and carrying loop",
                "Space theme sticker on side",
                f4,
                "Security Lost & Found Vault Room 102",
                "Describe the sticker graphic on the body."
        ));

        // 7. Sunita Sharma (Staff) - Casio Digital Watch (Verified)
        FoundItem item7 = new FoundItem(
                "FND-1007",
                "Casio Digital Watch",
                ItemCategory.WATCH,
                "Silver",
                "Casio",
                new Location("Girls Hostel Block A"),
                baseTime.minusDays(1).plusHours(11), // Oct 27 21:00
                "Vintage metal band digital watch found on study table",
                "Small scratch mark on lower right edge of screen",
                f3,
                "Girls Hostel Warden Office",
                "State the current alarm time set on the watch."
        );
        item7.setStatus(ItemStatus.VERIFIED);
        list.add(item7);

        return list;
    }
}
