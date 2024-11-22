package sba.sms.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sba.sms.models.Student;


class StudentServiceTest {

    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        this.studentService = new StudentService();

        // load data
        Student student = new Student();
        student.setName("Mikayil");
        student.setEmail("mika@gmail.com");
        student.setPassword("asdf");
        studentService.createStudent(student);
    }

    @Test
    public void testCreateStudent() {
        // given
        Student student = new Student();
        student.setName("Mikayil2");
        student.setEmail("mika2@gmail.com");
        student.setPassword("asdf");

        // when
        studentService.createStudent(student);

        // then
        var result = studentService.getStudentByEmail(student.getEmail());
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Mikayil2", result.getName());

    }

    @Test
    public void testGetAllStudents() {

        var result = studentService.getAllStudents();

        System.out.println(result);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void testGetStudentByEmail() {
        // Act
        Student student = studentService.getStudentByEmail("mika@gmail.com");

        // Assert
        Assertions.assertNotNull(student);
        Assertions.assertEquals("Mikayil", student.getName());
    }

    @Test
    void testValidateStudent() {
        // Act
        boolean isValid = studentService.validateStudent("mika@gmail.com", "asdf");

        // Assert
        Assertions.assertTrue(isValid);

        // Negative case
        boolean isInvalid = studentService.validateStudent("mika@gmail.com", "wrongpassword");
        Assertions.assertFalse(isInvalid);
    }
}