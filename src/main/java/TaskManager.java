public interface TaskManager {
    boolean insertTask(int jobNumber, int priority);
    boolean insertReoccurenceTask(final int jobNumber, final int priority, int intervalMilSec);
    int getNextJob();
}
