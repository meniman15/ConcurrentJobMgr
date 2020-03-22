import java.util.concurrent.*;

public class TaskMgr implements TaskManager {
    static final int MAX_TASKS = 3;
    private BoundedBlockingPriorityQueue<Job> queue = new BoundedBlockingPriorityQueue<Job>(MAX_TASKS);
    private ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(MAX_TASKS);


    public boolean insertTask(int jobNumber, int priority){
        if (priority>2){
            throw new IllegalArgumentException("Priority must be between 0 to 2");
        }
        if (queue.size() < MAX_TASKS){
            Job job = new Job(jobNumber,Job.Priority.values()[priority]);
            return queue.offer(job);
        }
        else
            return false;
    }

    public boolean insertReoccurenceTask(final int jobNumber, final int priority, int intervalMilSec){
        boolean result = insertTask(jobNumber, priority);
        try{
            //schedule an insertTask request in interval of milliseconds (intervalMilSec)
            scheduledExecutorService.schedule(new Callable() {
                                                  public Object call() {
                                                      return insertTask(jobNumber, priority);
                                                  }
                                              },
                    intervalMilSec,
                    TimeUnit.MILLISECONDS);
            return result;
        }
        catch (Exception e) {
            return result;
        }
        finally{
            try{
                scheduledExecutorService.shutdown();
            }
            catch (Exception e){
                System.out.println("ERROR: failed to shutdown scheduledExecutorService: "+ e);
            }
        }
    }

    public int getNextJob(){
        //size() is needed in order to ensure proper locking. (not isEmpty())
        if (queue.size() == 0){
            return -1;
        }
        return queue.remove().jobNumber;
    }

    public BoundedBlockingPriorityQueue<Job> getQueue() {
        return queue;
    }
}
