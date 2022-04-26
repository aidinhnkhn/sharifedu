package elements.courses;

public class Grade {
    private String courseId;
    private double grade;
    private boolean W;
    private boolean finished;
    private int unit;
    private String name,professorId;
    public Grade(String courseId, double grade) {
        this.courseId = courseId;
        this.grade = grade;
        this.W = false;
        this.finished = false;
        this.unit=Course.getCourse(courseId).getUnit();
        this.name=Course.getCourse(courseId).getName();
        this.professorId=Course.getCourse(courseId).getProfessorId();
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getCourseId() {
        return courseId;
    }

    public boolean isW() {
        return W;
    }

    public void setW(boolean w) {
        W = w;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String showGrade() {
        if (this.W) return "W";
        if (!finished) return "N/A";
        else return Double.toString(this.grade);
    }
}