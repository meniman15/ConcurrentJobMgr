import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        try {
            for (int i = 0; i < 10; i++) {
                if (i % 2 == 0) {
                    executor.execute(new Runnable() {
                        public void run() {
                            boolean success = taskMgr.insertTask(1, 1);
                            if (!success) {
                                throw new RuntimeException("Insert falied");
                            }
                        }
                    });
                    Thread.sleep(10);
                } else {
                    executor.execute(new Runnable() {
                        public void run() {
                            int jobNum = taskMgr.getNextJob();
                            if (jobNum == -1) {
                                throw new RuntimeException("remove falied");
                            }
                        }
                    });
                    Thread.sleep(10);
                }
            }
            Assert.assertEquals(taskMgr.getQueue().size(),0);
        }
        catch (Exception e){
            Assert.fail();
        }
    }
}
