package tw.commonground.backend.service.lock;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class LockService {

    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    private final Object globalLock = new Object();

    public <T> T executeWithLock(String key, Supplier<T> task) {
        Object lock;

        synchronized (globalLock) {
            lock = locks.computeIfAbsent(key, k -> new Object());
        }

        synchronized (lock) {
            try {
                return task.get();
            } finally {
                locks.remove(key, lock);
            }
        }
    }
}
