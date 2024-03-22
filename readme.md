
# Interpolating paths with lazy providers

Gradle is moving towards using `Provider`s in all APIs.
With properties becoming lazy instead of eager values, it becomes increasingly harder to compose them.

For instance, concatenating strings requires a `zip` call: `p1.zip(p2) { l, r -> l + " " + r }`.

This project explores how Java's new String Templates feature (in [Second Preview](https://openjdk.org/jeps/459)) may help here.

An interesting thing about the String Templates is that only the template has to be a string, but not the result.
This allows creating template `Processor`s that can do custom "interpolation" at runtime.

By defining a custom template processor, we can support lazy string concatenation that allows `Provider` values to be mixes with regular runtime strings.
The best part is that the result will always be a provider, so the users are never accidentally fall back to eager evaluation.

Here is an example of the template processor in action:
```java
String prefix = "root/path";
Provider<String> lazyDirName = () -> "child";

Provider<String> lazyPath = PATH."\{prefix}/\{lazyDirName}";

System.out.println(lazyPath.get());
// prints 'root/path/child'
```

It is questionable whether the "slash dance" `/\{}` inside the template string is more readable than an explicit `zip` call.
However, the underlying mechanism can be used for more things.

We could introduce a general `PSTR` (Provider string) template processor that behaves exactly the same as the default `STR` one, but always returns a `Provider` and never eagerly evaluates the template expressions.

```java
Provider<String> time = myTask.getTimeOutput();

outputFile.write(PSTR."""
    This is a custom report from a custom task
    It contains some execution time: \{time}
    But the resulting multi-line string is computed lazily,
    and the `time` provider is not evaluated immediately
    """)
```