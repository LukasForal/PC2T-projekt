package Main;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private static int nextId = 1;
    private int id;
    private String firstName;
    private String lastName;
    private int yearOfBirth;
    private Group group;
    private List<Integer> grades;

    public static final int MIN_GRADE = 1;
    public static final int MAX_GRADE = 5;

    public Student(String firstName, String lastName, int yearOfBirth, Group group) {
        this.id = nextId++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
        this.group = group;
        this.grades = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public Group getGroup() {
        return group;
    }

    public List<Integer> getGrades() {
        return grades;
    }

    public void setGrades(List<Integer> grades) {
        this.grades = grades;
    }

    public void print() {
        System.out.println("Student ID: " + this.getId());
        System.out.println("First Name: " + this.getFirstName());
        System.out.println("Last Name: " + this.getLastName());
        System.out.println("Year of Birth: " + this.getYearOfBirth());
        System.out.println("Group: " + this.getGroup());
        System.out.println("Average Grade: " + this.getAverageGrade());
    }

    public Float getAverageGrade() {
        Integer totalGrades = 0;
        if (grades.size() > 0) {
            for (int i = 0; i < grades.size(); i++) {
                totalGrades += grades.get(i);
            }
        } else {
            return (float) 0.0;
        }
        return (float) totalGrades / grades.size();
    }

    public void addGrade(int grade) {
        if (grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new IllegalArgumentException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
        }
        this.grades.add(grade);
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
