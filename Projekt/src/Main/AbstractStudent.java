import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStudent {
    protected static int nextId;
    protected int id;
    protected String firstName;
    protected String lastName;
    protected int yearOfBirth;
    protected Group group;
    protected List<Integer> grades;

    public static final int MIN_GRADE = 1;
    public static final int MAX_GRADE = 5;

    public AbstractStudent(String firstName, String lastName, int yearOfBirth, Group group) {
        this.id = nextId++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
        this.group = group;
        this.grades = new ArrayList<>();
    }

    public abstract String useSkill();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public int getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(int yearOfBirth) { this.yearOfBirth = yearOfBirth; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }
    public List<Integer> getGrades() { return grades; }
    public void setGrades(List<Integer> grades) { this.grades = grades; }

    public Float getAverageGrade() {
        if (grades.isEmpty()) return 0.0f;
        return (float) grades.stream().mapToInt(Integer::intValue).sum() / grades.size();
    }

    public void addGrade(int grade) {
        if (grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new IllegalArgumentException("Grade must be between " + MIN_GRADE + " and " + MAX_GRADE);
        }
        this.grades.add(grade);
    }

    public void print() {
        System.out.println("Student ID: " + this.getId());
        System.out.println("First Name: " + this.getFirstName());
        System.out.println("Last Name: " + this.getLastName());
        System.out.println("Year of Birth: " + this.getYearOfBirth());
        System.out.println("Group: " + this.getGroup());
        System.out.println("Average Grade: " + this.getAverageGrade());
        System.out.println("===========================================");
    }
}