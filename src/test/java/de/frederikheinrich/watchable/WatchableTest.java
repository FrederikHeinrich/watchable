package de.frederikheinrich.watchable;

import com.google.gson.Gson;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void testWatchableGson() {
        Gson gson = new Gson();
        {
            Watchable<String> watchable = Watchable.of("hello");
            Watchable<String> deserializable = gson.fromJson(gson.toJson(watchable), Watchable.class);
            System.out.println("String: " + watchable.get() + " - " + deserializable.get());
            assertEquals(watchable.get(), deserializable.get());
        }

        {
            Watchable<?> watchable = Watchable.of(654);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = Watchable.of(true);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = Watchable.of(03.133f);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = Watchable.of((byte) 1);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = Watchable.of('G');
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = Watchable.of((short) 12);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = Watchable.of(new ArrayList<String>(List.of("Listen", "gehen", "auch!")));
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }

    }

    /**
     * This method tests the functionality of the `WatchableCodec` class. It encodes a `Watchable` object into a `BsonDocument`,
     * decodes the `BsonDocument` back into a `Watchable` object, and asserts that the original `Watchable` object is equal to the decoded one.
     */
    @Test
    public void testWatchableBson() {
        {
            var codec = new Watchable.WatchableCodec<Integer>() {
            };
            var watchable = Watchable.of(42);
            var doc = new BsonDocument();
            codec.encode(new BsonDocumentWriter(doc), watchable, EncoderContext.builder().build());
            System.out.println(doc.toJson());
            var decoded = codec.decode(new BsonDocumentReader(doc), DecoderContext.builder().build());
            assertEquals(watchable.get(), decoded.get());
        }
        {
            var codec = new Watchable.WatchableCodec<String>() {
            };
            var watchable = Watchable.of("Hallo");
            var doc = new BsonDocument();
            codec.encode(new BsonDocumentWriter(doc), watchable, EncoderContext.builder().build());
            System.out.println(doc.toJson());
            var decoded = codec.decode(new BsonDocumentReader(doc), DecoderContext.builder().build());
            assertEquals(watchable.get(), decoded.get());
        }
        {
            var codec = new Watchable.WatchableCodec<Double>() {
            };
            var watchable = Watchable.of(4d);
            var doc = new BsonDocument();
            codec.encode(new BsonDocumentWriter(doc), watchable, EncoderContext.builder().build());
            System.out.println(doc.toJson());
            var decoded = codec.decode(new BsonDocumentReader(doc), DecoderContext.builder().build());
            assertEquals(watchable.get(), decoded.get());
        }
        {
            var codec = new Watchable.WatchableCodec<ArrayList<String>>() {
            };
            var watchable = Watchable.of(new ArrayList<>(List.of("Der", "typische", "Listen", "check", ".")));
            var doc = new BsonDocument();
            codec.encode(new BsonDocumentWriter(doc), watchable, EncoderContext.builder().build());
            System.out.println(doc.toJson());
            var decoded = codec.decode(new BsonDocumentReader(doc), DecoderContext.builder().build());
            assertEquals(watchable.get(), decoded.get());
        }
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
}
