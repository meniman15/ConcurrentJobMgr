import java.util.concurrent.PriorityBlockingQueue;

public class BoundedBlockingPriorityQueue<T> extends PriorityBlockingQueue<T> {
        private int maxItems;

        public BoundedBlockingPriorityQueue(int maxItems){
            this.maxItems = maxItems;
        }

        @Override
        public boolean offer(T e) {
            boolean success = super.offer(e);
            if(!success || this.size() >= maxItems){
                return false;
            }
            return true;
        }
}
