package writers;

/**
 * Generic abstraction for writing objects
 * @param <T> Type to wite
 */
public interface DataWriter<T> {
    void write(T data);
}
