package pl.fhframework.docs.forms.service;

import pl.fhframework.docs.forms.model.example.Person;
import pl.fhframework.docs.forms.model.example.Student;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by krzysztof.kobylarek on 2016-11-30.
 */
public class StudentService {

    private static final AtomicInteger SEQ = new AtomicInteger(1);

    private static List<Student> students = new LinkedList<>(
            Arrays.asList(
                    new Student(new Person(1l, "Bill", "Gates", "New York", "Male", "Active", null, null, null, null),
                            new LinkedList<>(Arrays.asList(
                                    new Student.Classes("Physics", "Newton",
                                            Arrays.asList(
                                                    new Student.Classes.Grades("3", "Not bad"),
                                                    new Student.Classes.Grades("5", "Good"),
                                                    new Student.Classes.Grades("2", "Bad"))),
                                    new Student.Classes("Mathematics", "Lagrange",
                                            Arrays.asList(
                                                    new Student.Classes.Grades("4", "Not bad"),
                                                    new Student.Classes.Grades("6", "Great"),
                                                    new Student.Classes.Grades("2", "Bad")))))
                    ),
                    new Student(new Person(2l, "Larry", "Ellison", "Los Angeles", "Male", "Inactive", null, null, null, null),
                            new LinkedList<>(Arrays.asList(
                                    new Student.Classes("Physics", "Newton",
                                            Arrays.asList(
                                                    new Student.Classes.Grades("4", "Good"),
                                                    new Student.Classes.Grades("5", "Very good"),
                                                    new Student.Classes.Grades("5", "Very good"))),
                                    new Student.Classes("Mathematics", "Lagrange",
                                            Arrays.asList(
                                                    new Student.Classes.Grades("5", "Very good"),
                                                    new Student.Classes.Grades("6", "Genius"),
                                                    new Student.Classes.Grades("5", "Very good")))))
                    ),
                    new Student(new Person(3l, "Sara", "Gates", "San Francisco", "Female", "Active", null, null, null, null),
                            new LinkedList<>(Arrays.asList(
                                    new Student.Classes("Physics", "Newton",
                                            new LinkedList<>(Arrays.asList(
                                                    new Student.Classes.Grades("1", "Bad!"),
                                                    new Student.Classes.Grades("2", "Bad"),
                                                    new Student.Classes.Grades("1", "Bad")))),
                                    new Student.Classes("Mathematics", "Lagrange",
                                            new LinkedList<>(Arrays.asList(
                                                    new Student.Classes.Grades("3", "Not bad"),
                                                    new Student.Classes.Grades("4", "Good"),
                                                    new Student.Classes.Grades("2", "Repeat classes"))))))
                    )));

    public static List<Student> findAll() {
        return students;
    }

    public static void removeStudent(Student student) {
        students.remove(student);
    }

    public static void addStudent() {
        int index = SEQ.getAndIncrement();
        students.add(new Student(new Person(100L + index, "Sara", "Gates" + index, "San Francisco", "Female", "Active", null, null, null, null),
                new LinkedList<>(Arrays.asList(
                    new Student.Classes("Physics", "Newton",
                            new LinkedList<>(Arrays.asList(
                            new Student.Classes.Grades("1", "Bad!"),
                            new Student.Classes.Grades("2", "Bad"),
                            new Student.Classes.Grades("1", "Bad")))),
                    new Student.Classes("Mathematics", "Lagrange",
                            new LinkedList<>(Arrays.asList(
                            new Student.Classes.Grades("3", "Not bad"),
                            new Student.Classes.Grades("4", "Good"),
                            new Student.Classes.Grades("2", "Repeat classes"))))))
        ));
    }

    public static void addClass(Student student) {
        student.getClasses().add(new Student.Classes("Physics" + SEQ.getAndIncrement(), "Newton",
                new LinkedList<>(Arrays.asList(
                                new Student.Classes.Grades("1", "Bad!"),
                                new Student.Classes.Grades("2", "Bad"),
                                new Student.Classes.Grades("1", "Bad")))));
    }

    public static void removeClass(Student.Classes studentClass) {
        for (Student student : students) {
            for (Student.Classes c : student.getClasses()) {
                if (studentClass == c) {
                    student.getClasses().remove(c);
                    return;
                }
            }
        }
    }

}
