package org.example;

import java.util.Iterator;

public class Main {

    public static void main() {
        String prefix = "root/path";
        Provider<String> lazyDirName = () -> "child";

        Provider<String> lazyPath = PATH."\{prefix}/\{lazyDirName}";

        System.out.println(lazyPath.get()); // prints 'root/path/child'
    }

    static StringTemplate.Processor<Provider<String>, RuntimeException> PATH =
            StringTemplate.Processor.of(st -> {
                Provider<String> lazy = () -> {
                    StringBuilder sb = new StringBuilder();
                    Iterator<String> fragIter = st.fragments().iterator();
                    for (Object value : st.values()) {
                        sb.append(fragIter.next());
                        var itemString = switch (value) {
                            case Provider<?> provider -> provider.get();
                            default -> String.valueOf(value);
                        };
                        sb.append(itemString);
                    }
                    sb.append(fragIter.next());
                    return sb.toString();
                };

                return lazy;
            });

    interface Provider<T> {
        T get();
    }

}
