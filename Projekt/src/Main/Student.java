import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Student extends AbstractStudent {

    static {
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT COALESCE(MAX(id), 0) + 1 FROM students");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nextId = rs.getInt(1);
            } else {
                nextId = 1;
            }
        } catch (SQLException e) {
            nextId = 1;
            System.err.println("Failed to initialize nextId: " + e.getMessage());
        }
    }
    private String firstName;
    private String lastName;
    private int yearOfBirth;
    private Group group;
    private List<Integer> grades;

    public static final int MIN_GRADE = 1;
    public static final int MAX_GRADE = 5;

    public Student(String firstName, String lastName, int yearOfBirth, Group group) {
        super(firstName, lastName, yearOfBirth, group);
    }

    private static final java.util.Map<Character, String> MORSE_CODE = new java.util.HashMap<>() {
        {
            put('A', ".-");
            put('B', "-...");
            put('C', "-.-.");
            put('D', "-..");
            put('E', ".");
            put('F', "..-.");
            put('G', "--.");
            put('H', "....");
            put('I', "..");
            put('J', ".---");
            put('K', "-.-");
            put('L', ".-..");
            put('M', "--");
            put('N', "-.");
            put('O', "---");
            put('P', ".--.");
            put('Q', "--.-");
            put('R', ".-.");
            put('S', "...");
            put('T', "-");
            put('U', "..-");
            put('V', "...-");
            put('W', ".--");
            put('X', "-..-");
            put('Y', "-.--");
            put('Z', "--..");
            put(' ', "/");
        }
    };

    public String useSkill() {
        String fullName = firstName + " " + lastName;

        if (group == Group.CYBERSECURITY) {
            return String.valueOf(fullName.hashCode());
        } else {
            StringBuilder morse = new StringBuilder();
            for (char c : fullName.toUpperCase().toCharArray()) {
                String morseChar = MORSE_CODE.get(c);
                if (morseChar != null) {
                    morse.append(morseChar);
                }
            }
            return morse.toString().trim();
        }
    }
}