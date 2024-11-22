package sba.sms.services;

import lombok.extern.java.Log;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * CourseService is a concrete class. This class implements the
 * CourseI interface, overrides all abstract service methods and
 * provides implementation for each method.
 */

@Log
public class CourseService implements CourseI {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public void createCourse(Course course) {
        log.info("Log.createCourse.start: Creating course: " + course.getName());

        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            session.persist(course);

            transaction.commit();

            log.info("Log.createCourse: Course created: " + course.getName());

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }

        log.info("Log.createCourse.end");
    }

    @Override
    public Course getCourseById(int courseId) {
        log.info("Log.getCourseById.start: Getting course by email: " + courseId);

        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            var hql = "FROM Course c WHERE c.id = :courseId";

            var query = session.createQuery(hql, Course.class);
            query.setParameter("courseId", courseId);

            var course = query.getSingleResultOrNull();

            transaction.commit();

            if (course != null) {
                log.info("Log.getCourseById.success: Course found with id: " + courseId);
            } else {
                log.info("Log.getCourseById.noResult: No Course found with id: " + courseId);
            }

            return course;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            if (transaction != null) {
                transaction.rollback();
            }
            return null;
        } finally {
            log.info("Log.getCourseById.end");
        }
    }

    @Override
    public List<Course> getAllCourses() {
        log.info("Log.getAllCourses.start");

        Transaction transaction = null;
        try (var session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "FROM Course";
            var query = session.createQuery(hql, Course.class);

            log.info("Log.getAllCourses.end");
            return query.getResultList();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            return Collections.emptyList();
        }
    }
}