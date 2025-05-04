
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    private Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (id, first_name, last_name, year_of_birth, student_group, grades) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getFirstName());
            pstmt.setString(3, student.getLastName());
            pstmt.setInt(4, student.getYearOfBirth());
            pstmt.setString(5, student.getGroup().toString());
            
            String grades = String.join(",", student.getGrades().stream()
                                              .map(String::valueOf)
                                              .toList());
            pstmt.setString(6, grades);
            
            pstmt.executeUpdate();
        }
    }

    public Student getStudent(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int yearOfBirth = rs.getInt("year_of_birth");
                Group group = Group.valueOf(rs.getString("student_group").toUpperCase());
                
                Student student = new Student(firstName, lastName, yearOfBirth, group);
                student.setId(id);

                String gradesStr = rs.getString("grades");
                if (gradesStr != null && !gradesStr.isEmpty()) {
                    List<Integer> grades = new ArrayList<>();
                    for (String grade : gradesStr.split(",")) {
                        grades.add(Integer.parseInt(grade.trim()));
                    }
                    student.setGrades(grades);
                }

                return student;
            }
        }
        return null;
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
