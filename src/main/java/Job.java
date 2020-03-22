
public class Job implements Comparable<Job> {
    int jobNumber;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }
    Priority priority;

    public Job(int jobNumber, Priority priority) {
        this.jobNumber = jobNumber;
        this.priority = priority;
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public Priority getPriority() {
        return priority;
    }

    public int compareTo(Job o) {
        return o.priority.ordinal() - this.priority.ordinal();
    }
}
