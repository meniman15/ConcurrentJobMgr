import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Tests {

    @Test
    public void addNewJobToEmptyQueue_success(){
        TaskMgr taskMgr = new TaskMgr();
        boolean result = taskMgr.insertTask(1,1);
        Assert.assertTrue(result);
    }

    @Test
    public void addDifferentJobs_getHighPriorityJob(){
        TaskMgr taskMgr = new TaskMgr();
        boolean result = taskMgr.insertTask(1,1);
        boolean result2 = taskMgr.insertTask(2,2);
        boolean result3 = taskMgr.insertTask(3,1);
        Assert.assertTrue(result);
        Assert.assertTrue(result2);
        Assert.assertTrue(result3);
        int jobNumber = taskMgr.getNextJob();
        Assert.assertEquals(jobNumber, 2 );
    }

    @Test
    public void addMoreThanCapacity_failAtLastAddition(){
        TaskMgr taskMgr = new TaskMgr();
        boolean result = taskMgr.insertTask(1,1);
        boolean result2 = taskMgr.insertTask(2,2);
        boolean result3 = taskMgr.insertTask(3,1);
        boolean result4 = taskMgr.insertTask(4,2);
        Assert.assertTrue(result);
        Assert.assertTrue(result2);
        Assert.assertTrue(result3);
        Assert.assertFalse(result4);
    }

    @Test
    public void addReoccurenceTask_successOnBothAndVerifyIntervaledInsert(){
        TaskMgr taskMgr = new TaskMgr();
        boolean result = taskMgr.insertReoccurenceTask(1,2,1000);
        Assert.assertTrue(result);
        //assert that reoccurenceTask was not done twice immediately
        Assert.assertEquals(taskMgr.getQueue().size(),1);
        try{
            Thread.sleep(1500);
        }
        catch (Exception e){}
        Assert.assertEquals(taskMgr.getQueue().size(),2);
    }

    @Test
    public void getTaskWhenNoTaskAvailable_fail(){
        TaskMgr taskMgr = new TaskMgr();
        Assert.assertEquals(taskMgr.getNextJob(), -1);
    }

    @Test
    public void getTaskAfterReoccurenceTaskWasAdded_success(){
        TaskMgr taskMgr = new TaskMgr();
        boolean result = taskMgr.insertReoccurenceTask(1,2,1000);
        Assert.assertNotEquals(taskMgr.getNextJob(), -1);
    }

    @Test
    public void insertAndThenGetInConcurrentWay_success(){
        final TaskMgr taskMgr = new TaskMgr();
        final int NUMBER_OF_THREADS = 6;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        Runnable insertJob = new Runnable() {
            @Override
            public void run() {
                boolean success = taskMgr.insertTask(1, 1);
                System.out.println("Completed insert");
                if (!success) {
                    throw new RuntimeException("Insert falied");
                }
            }
        };
        Runnable removeJob = new Runnable() {
            @Override
            public void run() {
                try {
                    while (taskMgr.getQueue().isEmpty()){
                        System.out.println("waiting until queue is not empty");
                    }
                    int jobNum = taskMgr.getNextJob();
                    System.out.println("Completed remove");
                    if (jobNum == -1) {
                        throw new RuntimeException("remove falied");
                    }
                }
                catch (Exception e) {
                    System.out.println(e);
                }
            }
        };

        try {
            for (int i = 0; i < NUMBER_OF_THREADS; i++) {
                if (i % 2 == 0) {
                    executor.execute(insertJob);
                } else {
                    executor.execute(removeJob);
                }
            }
            while (executor.getCompletedTaskCount() < NUMBER_OF_THREADS){
                executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            }
            Assert.assertEquals(taskMgr.getQueue().size(),0);
        }
        catch (Exception e){
            Assert.fail("got exception: " + e);
        }
    }
}
