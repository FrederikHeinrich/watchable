package de.frederikheinrich.watchable;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a watchable object that allows listeners to be notified of value changes.
 *
 * @param <T> the type of the value
 */
@JsonAdapter(Watchable.WatchableTypeAdapter.class)
public class Watchable<T> {

    /**
     * This variable is an instance of the Gson class, which provides methods for converting Java objects to JSON
     * representation and vice versa. It is declared as private and final, which means it cannot be modified or
     * overridden. By making it static, there is only one instance of Gson shared among all instances of the containing
     * class. This can improve memory usage and performance.
     */
    private final static Gson gson = new Gson();
    /**
     * Represents a list of change listeners that are notified when a value changes.
     * This list is used in conjunction with the Watchable class to manage and notify listeners of value changes.
     *
     * @param <T> the type of the value
     */
    transient ArrayList<ChangeConsumer<T>> listeners = new ArrayList<>();
    /**
     * Represents a volatile variable that can be watched for value changes.
     *
     * @param <T> the type of the value
     */
    private volatile T value;

    /**
     * Represents a watchable object that allows listeners to be notified of value changes.
     *
     * @param <T> the type of the value
     */
    public Watchable(T value) {
        this.value = value;
    }

    /**
     * Creates a new Watchable object with the given value.
     *
     * @param value the initial value of the Watchable
     * @param <T>   the type of the value
     * @return a new Watchable object with the given value
     */
    public static <T> Watchable<T> of(T value) {
        return new Watchable<>(value);
    }

    /**
     * Adds a listener that will be notified when the value of the Watchable object changes.
     *
     * @param listener the listener to be added
     * @param <T>      the type of the value
     */
    public void watch(ChangeConsumer<T> listener) {
        listeners.add(listener);
    }

    /**
     * Sets the value of the Watchable object and notifies all registered listeners of the change.
     *
     * @param value the new value to be set
     * @param <T>   the type of the value
     */
    public synchronized void set(T value) {
        final T old = this.value;
        this.value = value;
        listeners.forEach(listener -> listener.onChange(old, value));
    }

    /**
     * Retrieves the current value of the Watchable object.
     *
     * @param <T> the type of the value
     * @return the current value of the Watchable object
     */
    public synchronized final T get() {
        return value;
    }

    /**
     * Represents a consumer that handles changes in values.
     *
     * @param <T> the type of the values
     */
    public interface ChangeConsumer<T> {
        /**
         * This method is called when a change occurs in the specified values.
         *
         * @param oldValue the old value before the change
         * @param newValue the new value after the change
         */
        void onChange(T oldValue, T newValue);

        /**
         * This method returns a new ChangeConsumer that first calls the current ChangeConsumer and then calls the specified ChangeConsumer.
         *
         * @param after The ChangeConsumer to be called after the current ChangeConsumer
         * @return A new ChangeConsumer that calls both the current ChangeConsumer and the specified ChangeConsumer
         * @throws NullPointerException if the specified ChangeConsumer is null
         */
        default ChangeConsumer<T> andThen(ChangeConsumer<T> after) {
            Objects.requireNonNull(after);

            return (l, r) -> {
                onChange(l, r);
                after.onChange(l, r);
            };
        }
    }

    /**
     * A custom Gson {@link TypeAdapter} for serializing and deserializing objects of type Watchable.
     * This adapter is used in conjunction with Gson to convert Watchable objects to and from JSON representations.
     * It serializes a Watchable object by converting its value to a JSON string.
     * It deserializes a JSON string to a Watchable object by creating a new Watchable object with the deserialized value.
     *
     * @param <T> the type parameter of the Watchable object
     */
    static class WatchableTypeAdapter<T> implements JsonSerializer<Watchable<T>>, JsonDeserializer<Watchable<T>> {


        /**
         * Serializes a Watchable object by converting its value to a JSON string.
         *
         * @param src       the Watchable object to be serialized
         * @param typeOfSrc the type of the Watchable object
         * @param context   the serialization context
         * @return a JsonElement representing the serialized Watchable object
         */
        @Override
        public JsonElement serialize(Watchable<T> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("value", src.value.toString());
            jsonObject.addProperty("type", src.value.getClass().getName());
            return jsonObject;
        }

        /**
         * Deserializes a JSON element to a Watchable object.
         *
         * @param json    The JSON element to be deserialized
         * @param typeOfT The type information of the Watchable object
         * @param context The context for deserialization
         * @return The deserialized Watchable object
         * @throws JsonParseException If the JSON element cannot be deserialized
         */
        @Override
        public Watchable<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return (Watchable<T>) new Watchable<>(gson.fromJson(json.getAsJsonObject().get("value").getAsString(), Class.forName(json.getAsJsonObject().get("type").getAsString())));
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("Failed to deserialize Watchable: class not found", e);
            }
        }
    }

    /**
     * WatchableCodec is a codec used for encoding and decoding Watchable objects.
     * It implements the Codec interface and is used by CodecRegistries to register itself.
     *
     * @param <T> the type parameter of the Watchable object
     */
    static class WatchableCodec<T> implements Codec<Watchable<T>> {


        static {
            CodecRegistries.fromCodecs(new WatchableCodec<>());
        }

        /**
         * Decodes a BsonReader into a Watchable object.
         *
         * @param reader  the BsonReader to decode
         * @param context the DecoderContext
         * @param <T>     the type parameter of the Watchable object
         * @return a Watchable object decoded from the BsonReader
         * @throws RuntimeException if there is a ClassNotFoundException when deserializing the value
         */
        @Override
        public Watchable<T> decode(BsonReader reader, DecoderContext context) {
            reader.readStartDocument();
            reader.readName("type");
            String type = reader.readString();
            reader.readName("value");
            T value = null;
            try {
                value = (T) gson.fromJson(reader.readString(), Class.forName(type));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            reader.readEndDocument();
            return new Watchable<>(value);
        }

        /**
         * Encodes a Watchable object into a BsonWriter.
         *
         * @param writer  the BsonWriter to encode the Watchable object into
         * @param t       the Watchable object to be encoded
         * @param context the EncoderContext
         * @param <T>     the type parameter of the Watchable object
         */
        @Override
        public void encode(BsonWriter writer, Watchable<T> t, EncoderContext context) {
            writer.writeStartDocument();
            writer.writeString("type", t.value.getClass().getName());
            writer.writeString("value", gson.toJson(t.value));
            writer.writeEndDocument();

        }

        /**
         * Returns the {@code Class} object representing the type of objects that this encoder can encode.
         * <p>
         * This method is implemented to return the {@code Class} object of a {@code Watchable} instance
         * with a null value. It is invoked to determine the type of objects that this encoder can handle.
         * </p>
         *
         * @return the {@code Class} object representing the type of objects that this encoder can encode.
         */
        @Override
        public Class<Watchable<T>> getEncoderClass() {
            return (Class<Watchable<T>>) new Watchable<T>(null).getClass();
        }
    }
}
