# watchable

[![](https://jitpack.io/v/FrederikHeinrich/watchable.svg)](https://jitpack.io/#FrederikHeinrich/watchable)

Watchable is a Java library that provides a generic class `Watchable` which allows observing changes to an object.

## Usage

### Installation

Add the dependency to Watchable to your Maven or Gradle configuration:

Maven:

```xml

<dependency>
    <dependency>
        <groupId>com.github.FrederikHeinrich</groupId>
        <artifactId>watchable</artifactId>
        <version>Tag</version>
    </dependency>
</dependency>
```

Gradle:

```groovy
implementation 'com.github.FrederikHeinrich:watchable:Tag'
```

### Examples

Create a `Watchable` object with an initial value:

```java
Watchable<Integer> watchable = Watchable.watch(5);
```

Monitor changes to this object by adding a listener:

```java
watchable.on((oldValue, newValue) ->{
        System.out.println("Value changed from " + oldValue + " to " + newValue + ".");
});
```

Change the value of the object:

```java
watchable.set(10);
```

### Notes

- The `set` method of `Watchable` is thread-safe.
- The `Watchable` object can be serialized and deserialized using Gson.

## Contributing

If you find a bug or have an enhancement suggestion, please create an issue or pull request on GitHub.
