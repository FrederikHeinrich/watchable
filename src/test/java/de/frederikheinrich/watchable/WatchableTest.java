package de.frederikheinrich.watchable;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WatchableTest {

    @Test
    public void testWatchableValue() {
        Watchable<Integer> watchable = Watchable.of(5);
        assertEquals(5, watchable.get());

        watchable.set(10);
        assertEquals(10, watchable.get());
    }

    @Test
    public void testWatchableListener() {
        Watchable<Integer> watchable = Watchable.of(5);
        watchable.watch((oldValue, newValue) -> {
            assertEquals(5, oldValue);
            assertEquals(10, newValue);
        });

        watchable.set(10);
    }

    @Test
    public void testWatchableSerialization() {
        Watchable<String> watchable = Watchable.of("hello");
        Gson gson = new Gson();
        String json = gson.toJson(watchable);

        Watchable<?> deserialized = gson.fromJson(json, Watchable.class);
        assertEquals("hello", deserialized.get());
    }

}
