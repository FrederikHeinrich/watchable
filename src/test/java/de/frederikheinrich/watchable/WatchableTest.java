package de.frederikheinrich.watchable;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.frederikheinrich.watchable.Watchable.watch;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WatchableTest {

    /**
     * Tests the functionality of the WatchableValue class.
     */
    @Test
    public void testWatchableValue() {
        Watchable<Integer> watchable = watch(5);
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
        Watchable<Integer> watchable = watch(5);
        watchable.on((integer, integer2) -> {

        });
        watchable.on((oldValue, newValue) -> {
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
            Watchable<String> watchable = watch("hello");
            Watchable<String> deserializable = gson.fromJson(gson.toJson(watchable), Watchable.class);
            System.out.println("String: " + watchable.get() + " - " + deserializable.get());
            assertEquals(watchable.get(), deserializable.get());
        }

        {
            Watchable<?> watchable = watch(654);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = watch(true);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = watch(03.133f);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = watch((byte) 1);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = watch('G');
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = watch((short) 12);
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }
        {
            Watchable<?> watchable = watch(new ArrayList<String>(List.of("Listen", "gehen", "auch!")));
            assertEquals(watchable.get(), gson.fromJson(gson.toJson(watchable), Watchable.class).get());
        }

    }

    /**
     * <b>screw it... just use my Storage API :)</b>
     * <p>
     * This method tests the functionality of the `WatchableCodec` class. It encodes a `Watchable` object into a `BsonDocument`,
     * decodes the `BsonDocument` back into a `Watchable` object, and asserts that the original `Watchable` object is equal to the decoded one.
     */
//    @Test
//    public void testWatchableMongo() {
//        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
//        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
//
//        //CodecRegistries.fromCodecs(new Watchable.WatchableCodec<>());
//
//        try (TransitionWalker.ReachedState<RunningMongodProcess> running = Mongod.instance()
//                .withProcessOutput(
//                        Start.to(ProcessOutput.class)
//                                .initializedWith(ProcessOutput.silent())
//                )
//                .start(Version.Main.PRODUCTION)) {
//            try (MongoClient mongo = MongoClients.create("mongodb://" + running.current().getServerAddress())) {
//                MongoDatabase db = mongo.getDatabase("watchable");
//                MongoCollection<TestElement> col = db.getCollection("testElement", TestElement.class).withCodecRegistry(pojoCodecRegistry);
//                var insert = col.insertOne(new TestElement("test", 50));
//                System.out.println("Inserted " + insert);
//
//                MongoCollection<Document> colDoc = db.getCollection("testElement");
//                colDoc.find().forEach(document -> {
//                    System.out.println("Found " + document);
//                });
//
//                col.find().forEach(testElement -> {
//                    System.out.println("Found Element " + testElement);
//                    testElement.name().watch((oldValue, newValue) -> {
//                        System.out.println("Old value " + oldValue + " - " + newValue);
//                    });
//                    testElement.name().set("ASDASADASd");
//                });
//            }
//        }
//    }


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
        Watchable<Integer> watchable = new Watchable<>(5, Integer.class);
        assertEquals(5, watchable.get());
    }
}
