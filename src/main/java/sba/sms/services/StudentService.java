package sba.sms.services;

import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

@Log
public class StudentService implements StudentI {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    private final CourseService courseService = new CourseService();

    @Override
    public List<Student> getAllStudents() {
        log.info("Log.getAllStudents.start");

        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            var hql = "FROM Student";

            var query = session.createQuery(hql, Student.class);

            var students = query.getResultList();

            transaction.commit();

            log.info("Log.getAllStudents: Successfully got all students");
            return students;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
            return Collections.emptyList();
        } finally {
            log.info("Log.getAllStudents.end");
        }
    }

    @Override
    public void createStudent(Student student) {
        log.info("Log.createStudent.start: Creating student: " + student.getName());
        if (validateStudent(student.getEmail(), student.getPassword())) {
            log.info("Log.createStudent: Student already exists: " + student.getName());
            return;
        }

        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            session.persist(student);

            transaction.commit();

            log.info("Log.createStudent: Student created: " + student.getName());

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }

        log.info("Log.createStudent.end");
    }

    @Override
    public Student getStudentByEmail(String email) {
        log.info("Log.getStudentByEmail.start: Getting student by email: " + email);

        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            var hql = "FROM Student s WHERE s.email = :email";

            var query = session.createQuery(hql, Student.class);
            query.setParameter("email", email);

            var student = query.getSingleResultOrNull();

            transaction.commit();

            if (student != null) {
                log.info("Log.getStudentByEmail.success: Student found with email: " + email);
            } else {
                log.info("Log.getStudentByEmail.noResult: No student found with email: " + email);
            }

            return student;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
            return null;
        } finally {
            log.info("Log.getStudentByEmail.end");
        }
    }

    @Override
    public boolean validateStudent(String email, String password) {
        log.info("Log.validateStudent.start: Validating student with email: " + email);

        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            var hql = "FROM Student s WHERE s.email = :email and s.password = :password";

            var query = session.createQuery(hql, Student.class);
            query.setParameter("email", email);
            query.setParameter("password", password);

            var student = query.getSingleResultOrNull();

            transaction.commit();

            return student != null;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            log.info("Log.validateStudent.end");
        }
    }

    @Override
    public void registerStudentToCourse(String email, int courseId) {
        log.info("Log.registerStudentToCourse.start: Registering student to course: " + email);

        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            var student = getStudentByEmail(email);
            var course = courseService.getCourseById(courseId);

            if (student == null || course == null) {
                log.warning("Log.registerStudentToCourse.failed: Student or course not found: " + email);
                transaction.rollback();
                return;
            }

            student.getCourses().add(course);
            course.getStudents().add(student);

            session.merge(student);

            transaction.commit();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
        log.info("Log.registerStudentToCourse.end");
    }

    @Override
    public List<Course> getStudentCourses(String email) {
        log.info("Log.getStudentCourses.start: Getting student courses by email: " + email);
        var student = getStudentByEmail(email);

        List<Course> result;
        if (student == null) {
            result = Collections.emptyList();
        } else {
            var studentCourses = student.getCourses();
            result = new ArrayList<>(studentCourses);
        }

        log.info("Log.getStudentCourses.end");
        return result;
    }
}
