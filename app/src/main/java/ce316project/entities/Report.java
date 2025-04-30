package ce316project.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ce316project.entities.Student;
import ce316project.views.MainPage;

public class Report {
    private List<Student> students;
    private String projectName;
    private final List<String> student_id_list;
    private final List<String> student_result_list;

    public Report(List<Student> students, String projectName) {
        this.students = students;
        this.projectName = projectName;
        student_id_list = new ArrayList<>();
        student_result_list = new ArrayList<>();
        for (Student student : students) {
            student_id_list.add(student.getStudentId());
            student_result_list.add(student.getStatus().toString());
        }
    }

    public List<String> getStudent_id_list() {
        return student_id_list;
    }

    public List<String> getStudent_result_list() {
        return student_result_list;
    }
}
