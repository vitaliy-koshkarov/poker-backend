package poker.core;

public interface Rollback<T> {
    void rollback(T snapshot);
}
