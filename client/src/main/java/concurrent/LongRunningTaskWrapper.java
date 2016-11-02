package concurrent;

/**
 * This is helper class for wrapping long running Runnable
 * It is used
 * @param <T> Runnable to run
 */
public class LongRunningTaskWrapper<T extends Runnable>  implements Runnable {

    private T task;

    public LongRunningTaskWrapper(T task) {
        this.task = task;
    }


    @Override
    public void run() {
        new Thread(task).start();
    }
}
