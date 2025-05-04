import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
  private static List<Student> students = new ArrayList<>();
  private static List<Student> cybersecurityStudents = new ArrayList<>();
  private static List<Student> telecommunicationStudents = new ArrayList<>();
  private static Connection connection;

  private static void loadStudentsFromDatabase(StudentDAO studentDAO) throws SQLException {
    String sql = "SELECT id FROM students";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        int studentId = rs.getInt("id");
        Student student = studentDAO.getStudent(studentId);
        if (student != null) {
          createStudent(student);
        }
      }
    }
  }

  public static void main(String[] args) {
    try {
      connection = DatabaseConnection.getConnection();
      StudentDAO studentDAO = new StudentDAO(connection);
      loadStudentsFromDatabase(studentDAO);
      System.out.println("Students loaded from database successfully!");
    } catch (SQLException e) {
      System.out.println("Failed to load students from database!");
    }
    Scanner scanner = new Scanner(System.in);
    boolean running = true;
    while (running) {
      System.out.println("\n=== Student Management System ===");
      System.out.println("1. Create a new student");
      System.out.println("2. Add grade to student");
      System.out.println("3. Delete student");
      System.out.println("4. Find student");
      System.out.println("5. Use student skill");
      System.out.println("6. Print all students");
      System.out.println("7. Get average grade of all students in groups");
      System.out.println("8. Get amount of students in groups");
      System.out.println("9. Save student to file");
      System.out.println("10. Import student");
      System.out.println("0. Exit");
      int choice = getValidInteger(scanner);
      switch (choice) {
        case 1:
          System.out.println("\n=== Create New Student ===");
          System.out.print("Enter first name: ");
          String firstName = scanner.nextLine();
          System.out.print("Enter last name: ");
          String lastName = scanner.nextLine();
          System.out.print("Enter year of birth: ");
          int yearOfBirth = scanner.nextInt();
          scanner.nextLine();
          System.out.println("\nAvailable groups:");
          for (Group group : Group.values()) {
            System.out.println("- " + group);
          }
          System.out.print("Enter group (CYBERSECURITY/TELECOMMUNICATION): ");
          String groupInput = scanner.nextLine().toUpperCase();
          Group group = Group.valueOf(groupInput);
          Student student = new Student(firstName, lastName, yearOfBirth, group);
          createStudent(student);
          System.out.println("\nStudent created successfully with ID: " + student.getId());
          break;
        case 2:
          System.out.println("\n=== Add Grade to Student ===");
          System.out.print("Enter student ID: ");
          int studentId = scanner.nextInt();
          scanner.nextLine();
          System.out.print("Enter grade (1-5): ");
          int grade = scanner.nextInt();
          scanner.nextLine();
          getStudent(studentId).addGrade(grade);
          System.out.println("Grade added successfully!");
          break;
        case 3:
          System.out.println("\n=== Delete Student ===");
          System.out.print("Enter student ID to delete: ");
          studentId = scanner.nextInt();
          scanner.nextLine();
          Student studentToDelete = getStudent(studentId);
          if (studentToDelete != null) {
            System.out.print("Are you sure? (Y to confirm): ");
            String confirm = scanner.nextLine().trim().toUpperCase();
            if (confirm.equals("Y")) {
              deleteStudent(studentId);
              System.out.println("Student deleted successfully!");
            } else {
              System.out.println("Deletion cancelled.");
            }
          } else {
            System.out.println("Student not found!");
          }
          break;
        case 4:
          System.out.println("\n=== Find Student ===");
          System.out.print("Enter student ID to find: ");
          studentId = scanner.nextInt();
          student = getStudent(studentId);
          student.print();
          break;
        case 5:
          System.out.println("\n=== Use Student Skill ===");
          System.out.print("Enter student ID: ");
          studentId = scanner.nextInt();
          student = getStudent(studentId);
          System.out.println(student.useSkill());
          break;
        case 6:
          printAllStudents();
          break;
        case 7:
          System.out.println("\n=== Average Grade of Groups ===");
          System.out.println("Cybersecurity Students Average Grade: " + getAverageGrade(cybersecurityStudents));
          System.out.println("Telecommunication Students Average Grade: " + getAverageGrade(telecommunicationStudents));
          break;
        case 8:
          System.out.println("\n=== Amount of Students in Groups ===");
          System.out.println("Cybersecurity Students: " + cybersecurityStudents.size());
          System.out.println("Telecommunication Students: " + telecommunicationStudents.size());
          System.out.println("Total Students: " + students.size());
          break;
        case 9:
          System.out.println("\n=== Save Student to File ===");
          System.out.print("Enter student ID: ");
          studentId = scanner.nextInt();
          scanner.nextLine();
          saveStudentToFile(studentId);
          break;
        case 10:
          System.out.println("\n=== Import Student ===");
          System.out.print("Enter filename (with .txt extension): ");
          String filename = scanner.nextLine();
          if (importStudent(filename))
            System.out.println("Student imported successfully!");
          else
            System.out.println("Student import failed!");
          break;
        case 0:
          System.out.println("\nSynchronizing students with database before exit...");
          try {
            String sql = "SELECT id FROM students";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            List<Integer> dbStudentIds = new ArrayList<>();
            while (rs.next()) {
              dbStudentIds.add(rs.getInt("id"));
            }

            StudentDAO studentDAO = new StudentDAO(connection);
            for (Student currentStudent : students) {
              if (!dbStudentIds.contains(currentStudent.getId())) {
                System.out.println("Saving student ID " + currentStudent.getId() + " to database...");
                studentDAO.saveStudent(currentStudent);
              }
            }

            List<Integer> memoryStudentIds = students.stream()
                .map(Student::getId)
                .toList();
            for (Integer dbId : dbStudentIds) {
              if (!memoryStudentIds.contains(dbId)) {
                System.out.println("Deleting student ID " + dbId + " from database...");
                studentDAO.deleteStudent(dbId);
              }
            }
            System.out.println("Synchronization complete!");
          } catch (SQLException e) {
            System.out.println("Error during synchronization: " + e.getMessage());
          }
          running = false;
          break;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    }

    scanner.close();

    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static int getValidInteger(Scanner scanner) {
    while (true) {
      System.out.print("Please enter your choice: ");
      String input = scanner.nextLine();

      if (isInteger(input)) {
        return Integer.parseInt(input);
      }
    }
  }

  private static boolean isInteger(String str) {
    try {
      Integer.parseInt(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private static Student getStudent(int id) {
    for (Student student : students) {
      if (student.getId() == id) {
        return student;
      }
    }
    return null;
  }

  private static void createStudent(Student student) {
    students.add(student);
    if (student.getGroup() == Group.CYBERSECURITY) {
      cybersecurityStudents.add(student);
    } else if (student.getGroup() == Group.TELECOMMUNICATION) {
      telecommunicationStudents.add(student);
    }
  }

  private static void deleteStudent(Integer studentId) {
    Student student = getStudent(studentId);
    if (student != null) {
      students.remove(student);
      if (student.getGroup() == Group.CYBERSECURITY) {
        cybersecurityStudents.remove(student);
      } else {
        telecommunicationStudents.remove(student);
      }
    }
  }

  private static void printAllStudents() {
    System.out.println("\n=== All Students ===");
    System.out.println("Cybersecurity Students (" + cybersecurityStudents.size() + "):");
    cybersecurityStudents.stream()
        .sorted((s1, s2) -> s1.getLastName().compareTo(s2.getLastName()))
        .forEach(s -> s.print());

    System.out.println("\nTelecommunication Students (" + telecommunicationStudents.size() + "):");
    telecommunicationStudents.stream()
        .sorted((s1, s2) -> s1.getLastName().compareTo(s2.getLastName()))
        .forEach(s -> s.print());

  }

  private static float getAverageGrade(List<Student> students) {
    Float totalAverageGrades = 0.0f;
    for (Student student : students) {
      totalAverageGrades += student.getAverageGrade();
    }
    return totalAverageGrades / students.size();
  }

  private static void saveStudentToFile(int studentId) {
    Student student = getStudent(studentId);
    if (student != null) {
      try {
        String filename = "student_" + student.getId() + ".txt";
        java.io.PrintWriter writer = new java.io.PrintWriter(filename);
        writer.println("Student ID: " + student.getId());
        writer.println("First Name: " + student.getFirstName());
        writer.println("Last Name: " + student.getLastName());
        writer.println("Year of Birth: " + student.getYearOfBirth());
        writer.println("Group: " + student.getGroup());
        writer.println("Average Grade: " + student.getAverageGrade());
        writer.println("Grades: " + student.getGrades());
        writer.close();
        System.out.println("Student information saved to " + filename);
      } catch (java.io.FileNotFoundException e) {
        System.out.println("Error saving student information: " + e.getMessage());
      }
    } else {
      System.out.println("Student not found!");
    }

  }

  private static boolean importStudent(String filename) {
    try {
      List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filename));

      if (lines.size() < 5) {
        System.out.println("File does not contain enough data.");
        return false;
      }
      int id = 0, yearOfBirth = 0;
      String firstName = "", lastName = "";
      Group group = null;
      List<Integer> grades = new ArrayList<>();
      for (String line : lines) {
        System.out.println(line);
        if (line.toLowerCase().contains("id")) {
          id = Integer.parseInt(line.split(": ")[1].trim());

        } else if (line.toLowerCase().contains("year")) {
          yearOfBirth = Integer.parseInt(line.split(": ")[1].trim());
        } else if (line.toLowerCase().contains("group")) {
          group = Group.valueOf(line.split(": ")[1].trim().toUpperCase());
        } else if (line.toLowerCase().contains("first")) {
          firstName = line.split(": ")[1].trim();
        } else if (line.toLowerCase().contains("last")) {
          lastName = line.split(": ")[1].trim();
        } else if (line.toLowerCase().contains("grades")) {
          String gradesString = line.split(": ")[1].trim();
          gradesString = gradesString.substring(1, gradesString.length() - 1).trim();
          if (gradesString.length() > 0) {
            String[] gradesStr = gradesString.split(",");
            for (String gradeStr : gradesStr) {
              System.out.println(gradeStr);
              grades.add(Integer.parseInt(gradeStr));
            }
          }
        }
      }

      Student student = new Student(firstName, lastName, yearOfBirth, group);
      if (getStudent(id) == null) {
        student.setId(id);
      }
      student.setGrades(grades);

      createStudent(student);
      return true;
    } catch (Exception e) {
      System.out.println("Error importing student: " + e.getMessage());
      return false;
    }
  }
}