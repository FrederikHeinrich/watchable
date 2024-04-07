package de.frederikheinrich.watchable;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class WatchableTest {

    /**
     * Tests the functionality of the WatchableValue class.
     */
    @Test
    public void testWatchableValue() {
        Watchable<Integer> watchable = Watchable.of(5);
        assertEquals(5, watchable.get());

        watchable.set(10);
        assertEquals(10, watchable.get());
    }

    /**
     * Test case for the watch(ChangeConsumer<T> listener) method.
     * <p>
     * This test verifies that the WatchableListener is correctly invoked when the value of the watchable is changed.
     * It checks that the old value and the new value received by the listener are correct.
     */
    @Test
    public void testWatchableListener() {
        Watchable<Integer> watchable = Watchable.of(5);
        watchable.watch((integer, integer2) -> {

        });
        watchable.watch((oldValue, newValue) -> {
            assertEquals(5, oldValue);
            assertEquals(10, newValue);
        });

        watchable.set(10);
    }

    /**
     * Test case for serializing and deserializing a Watchable object using Gson.
     */
    @Test
    public void testWatchableJsonSerialization() {
        Watchable<String> watchable = Watchable.of("hello");
        Gson gson = new Gson();
        String json = gson.toJson(watchable);

        Watchable<?> deserialized = gson.fromJson(json, Watchable.class);
        assertEquals("hello", deserialized.get());
    }

    /**
     * Test case for the andThen(ChangeConsumer<T> after) method in the ChangeConsumer interface.
     */
    @Test
    public void testChangeConsumerAndThen() {
        List<String> collectedResponses = new ArrayList<>();
        Watchable.ChangeConsumer<Integer> firstConsumer = (oldValue, newValue) -> collectedResponses.add("First: " + oldValue + " -> " + newValue);
        Watchable.ChangeConsumer<Integer> secondConsumer = (oldValue, newValue) -> collectedResponses.add("Second: " + oldValue + " -> " + newValue);

        Watchable.ChangeConsumer<Integer> combinedConsumer = firstConsumer.andThen(secondConsumer);
        combinedConsumer.onChange(5, 10);

        assertEquals(Arrays.asList("First: 5 -> 10", "Second: 5 -> 10"), collectedResponses);
    }

    /**
     * Test case for the constructor Watchable(T value).
     * <p>
     * The test validates that the value of the newly created Watchable object
     * matches the value it was initialized with.
     */
    @Test
    public void testWatchableConstructorWithValue() {
        Watchable<Integer> watchable = new Watchable<>(5);
        assertEquals(5, watchable.get());
    }

    /**
     * Test case for the constructor Watchable().
     * <p>
     * The test validates that the value of the newly created Watchable object
     * with no arguments is correctly null.
     */
    @Test
    public void testWatchableConstructorNoValue() {
        Watchable<String> watchable = new Watchable<>();
        assertNull(watchable.get());
    }
}
