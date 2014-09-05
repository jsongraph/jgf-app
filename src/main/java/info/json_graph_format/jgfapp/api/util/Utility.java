package info.json_graph_format.jgfapp.api.util;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.io.File.separator;
import static java.lang.Character.*;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.System.arraycopy;
import static java.lang.Thread.currentThread;
import static java.lang.reflect.Array.newInstance;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.compile;
import static org.slf4j.LoggerFactory.getLogger;

public class Utility {
    private final static Pattern ALPHA_REGEX = compile("\\p{Alnum}+");
    private static final long[] byteTable = createLookupTable();
    private final static Pattern DIGIT_REGEX = compile("\\p{Digit}+");
    private static final long HMULT = 7664345821815920749L;
    private static final long HSTART = 0xBB40E64DA205B064L;
    private final static int MAX_PORT = 65535;
    private final static int MIN_PORT = 0;
    private static int ONE_MEGABYTE = 1024 * 1024;
    private static int pid = -1;

    /**
     * Returns an array of strings from the supplied var-args.
     *
     * @param strings Zero or more strings
     * @return {@code String}; may be of length zero
     */
    public static String[] array(String... strings) {
        if (strings == null) return new String[0];
        String[] ret = new String[strings.length];
        for (int i = 0; i < strings.length; ret[i] = strings[i], i++) ;
        return ret;
    }

    /**
     * Returns a hash set of type {@code E} optimized to the
     * {@link Collection#size() size} of the supplied {@link Collection
     * collection}.
     *
     * @param <E> Formal type parameter collection element
     * @param c {@link Collection}; may be null
     * @return Hash set of type {@code E}
     */
    public static <E> HashSet<E> asHashSet(final Collection<E> c) {
        if (noItems(c)) {
            return new HashSet<E>();
        }
        return new HashSet<E>(c);
    }

    /**
     * Inserts the platform-specific filesystem path separator between the
     * provided strings.
     *
     * @param strings
     * @return String in the following form:
     *         {@code strings[0]<path_separator>strings[1]<path_separator>...<strings[n]>}
     *         , or <b>null</b> if {@code strings} is null
     */
    public static String asPath(final String... strings) {
        if (strings == null) return null;
        final StringBuilder bldr = new StringBuilder();
        for (final String string : strings) {
            if (bldr.length() != 0) bldr.append(separator);
            bldr.append(string);
        }
        return bldr.toString();
    }

    /**
     * Inserts the platform-specific filesystem path separator between
     * {@code directory} and {@code filename} and returns the resulting string.
     *
     * @param directory Non-null string
     * @param filename Non-null string
     * @return String following the format
     *         {@code directory<path_separator>filename}
     */
    public static String asPath(final String directory, final String filename) {
        return directory.concat(separator).concat(filename);
    }

    /**
     * Casts.
     *
     * @param o {@link Object}
     * @param <T> The type to cast to
     * @return {@code T}
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object o) {
        return (T) o;
    }

    /**
     * Closes a {@link Closeable closeable resource} without reporting an
     * {@link IOException io error}.
     * <p>
     * This method is a no-op if {@code closeable} is {@code null}.
     * </p>
     *
     * @param closeable the {@link Closeable closeable} to close
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Computes a 64bit hash code of a {@link CharSequence character sequence}.
     * <p>
     * Using a 64bit hash code minimizes collisions at the cost of size.
     * </p>
     *
     * @param cs the {@link CharSequence character sequence} to hash
     * @return the 64bit hash
     */
    public static long computeHash64(CharSequence cs) {
        long h = HSTART;
        final long hmult = HMULT;
        final long[] ht = byteTable;
        final int len = cs.length();
        for (int i = 0; i < len; i++) {
            char ch = cs.charAt(i);
            h = (h * hmult) ^ ht[ch & 0xff];
            h = (h * hmult) ^ ht[(ch >>> 8) & 0xff];
        }
        return h;
    }

    /**
     * Concatenates the two arrays {@code a} and {@code b} and returns the
     * result.
     *
     * @param a {@code int[]} a
     * @param b {@code int[]} b
     * @return {@code int[]} concatenation
     */
    public static int[] concatArrays(int[] a, int[] b) {
        final int alen = a.length;
        final int blen = b.length;
        if (alen == 0) {
            return b;
        }
        if (blen == 0) {
            return a;
        }
        final int[] result = new int[alen + blen];
        arraycopy(a, 0, result, 0, alen);
        arraycopy(b, 0, result, alen, blen);
        return result;
    }

    /**
     * Returns a hash map of type {@code K, V} optimized to trade memory
     * efficiency for CPU time.
     * <p>
     * Use constrained hash maps when the capacity of a hash map is known to be
     * greater than sixteen (the default initial capacity) and the addition of
     * elements beyond the map's capacity will not occur. The hash map
     * implementation will automatically adjust the size to the next nearest
     * power of two.
     * </p>
     *
     * @param <K> Formal type parameter key
     * @param <V> Formal type parameter value
     * @param s Initial hash map capacity
     * @return Hash map of type {@code K, V}
     */
    public static <K, V> HashMap<K, V> constrainedHashMap(final int s) {
        return new HashMap<K, V>(s, 1.0F);
    }

    /**
     * Returns a hash set of type {@code T} optimized to trade memory
     * efficiency
     * for CPU time.
     * <p>
     * Use constrained hash sets when the capacity of a hash set is known to be
     * greater than sixteen (the default initial capacity) and the addition of
     * elements beyond the set's capacity will not occur. The hash set
     * implementation will automatically adjust the size to the next nearest
     * power of two.
     * </p>
     *
     * @param <T> Formal type parameter
     * @param s Initial hash set capacity
     * @return Hash set of type {@code T}
     */
    public static <T> HashSet<T> constrainedHashSet(final int s) {
        return new HashSet<T>(s, 1.0F);
    }

    /**
     * Copy bytes from the {@link InputStream input stream} to the
     * {@link OutputStream output stream} in 4 kilobyte increments.
     *
     * @param input the {@link InputStream input stream} to read from, which
     * cannot be null
     * @param output the {@link OutputStream output stream} to write to, which
     * cannot be null
     * @return the number of {@code bytes} read
     * @throws IOException Thrown if an IO error occurred while copying data
     */
    public static long copy(final InputStream input, final OutputStream output)
            throws IOException {
        // guard against null streams
        requireNonNull(input);
        requireNonNull(output);

        byte[] buf = new byte[4096];
        long count = 0;
        int bytesRead = 0;
        while ((bytesRead = input.read(buf)) != -1) {
            output.write(buf, 0, bytesRead);
            count += bytesRead;
        }

        return count;
    }

    /**
     * Copies a {@link File source file} to a {@link File destination file}.
     *
     * @param src the {@link File source file}, which must be non-null and
     * readable
     * @param dest the {@link File destination file}, which must be non-null
     * and
     * writable
     * @throws IOException Thrown if an IO error occurred copying the
     * {@link File src} to {@link File dest}
     * @throws IOException thrown if
     * <ul>
     * <li>{@code src} or {@code dest} is {@code null}</li>
     * <li>{@code src} is not readable</li>
     * <li>{@code dest} is not writeable</li>
     * </ul>
     */
    public static void copyFile(final File src, final File dest)
            throws IOException {
        if (!readable(src)) {
            throw new IOException(format("%s: unreadable", src));
        }
        if (!writable(dest)) {
            throw new IOException(format("%s: unwritable", src));
        }

        // initialize streams/channels
        FileInputStream srcInput = null;
        FileChannel srcChannel = null;
        FileOutputStream destOutput = null;
        FileChannel destChannel = null;
        try {
            srcInput = new FileInputStream(src);
            srcChannel = srcInput.getChannel();
            destOutput = new FileOutputStream(dest);
            destChannel = destOutput.getChannel();

            // transfer from src to dest channel, one megabyte at a time
            long size = srcChannel.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count =
                        (size - pos) > ONE_MEGABYTE ? ONE_MEGABYTE
                                : (size - pos);
                pos += destChannel.transferFrom(srcChannel, pos, count);
            }
        } finally {
            // close channels and streams
            if (destChannel != null) {
                destChannel.close();
            }
            if (destOutput != null) {
                destOutput.close();
            }
            if (srcChannel != null) {
                srcChannel.close();
            }
            if (srcInput != null) {
                srcInput.close();
            }
        }
    }

    /**
     * Returns the number of elements produced by an iterator.
     *
     * @param iter {@link Iterable}
     * @return int
     */
    public static int count(Iterable<?> iter) {
        int ret = 0;
        for (@SuppressWarnings("unused") Object o : iter) ret++;
        return ret;
    }

    /**
     * Create the provided directory, and all necessary subdirectories, if they
     * do not already exist.
     *
     * @param directory Path to create
     * @throws RuntimeException Thrown if directory creation failed
     */
    public static void createDirectories(final String directory) {
        if (directory == null) return;
        final File f = new File(directory);
        if (!f.isDirectory()) {
            if (!f.mkdirs()) throw new RuntimeException("couldn't create "
                    + directory);
        }
    }

    /**
     * Create the provided directory, if it does not already exist.
     *
     * @param directory Path to create
     * @throws RuntimeException Thrown if directory creation failed
     */
    public static void createDirectory(final String directory) {
        if (directory == null) return;
        final File f = new File(directory);
        if (!f.isDirectory()) {
            if (!f.mkdir()) throw new RuntimeException("couldn't create "
                    + directory);
        }
    }

    /**
     * Deletes the directory {@code dir}, and all of its contents.
     *
     * @param dir {@link File}
     * @return boolean {@code true} if success, {@code false} otherwise
     */
    public static boolean deleteDirectory(final File dir) {
        if (!deleteDirectoryContents(dir)) {
            return false;
        }

        return dir.delete();
    }

    /**
     * Recursively deletes all files and folders within the directory
     * <tt>dir</tt>.
     *
     * @param dir {@link File}, the directory to empty contents for
     * @return boolean determines whether or not the delete was successful,
     *         <tt>true</tt> if success, <tt>false</tt> otherwise
     */
    public static boolean deleteDirectoryContents(final File dir) {
        if (dir == null || !dir.isDirectory()) {
            return false;
        }

        File[] files = dir.listFiles();
        for (final File file : files) {
            if (file.isDirectory()) {
                if (!deleteDirectory(file)) {
                    return false;
                }
            } else {
                if (!file.delete()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns an empty list of type {@code T} when {@code l} is empty,
     * {@code l} otherwise.
     *
     * @param <T> Formal type parameter
     * @param l {@link List} of type {@code <T>}; may be {@code null} or empty
     * @return {@link Collections#emptyList()} or {@code l}
     */
    public static <T> List<T> emptyListWhenEmpty(List<T> l) {
        if (noItems(l)) {
            return emptyList();
        }
        return l;
    }

    /**
     * Returns a {@link Set} view of the entries contained in this map.
     * <p>
     * This method should <strong>always</strong> be preferred instead of
     * iterating a maps keys and invoking {@link Map#get(Object) get()} on each
     * iteration. The following code should never be used:
     * <p/>
     * <pre>
     * <code>
     * Map<String, String> map = [...]
     * for (final String key : map.keySet()) {
     *     String value = map.get(key);
     *     // do something with key/value
     * }
     * </code>
     * </pre>
     * <p/>
     * Instead, write:
     * <p/>
     * <pre>
     * <code>
     * Map<String, String> map = [...]
     * for (final Entry<String, String> e : entries(map)) {
     *     // do something with e.getKey/e.getValue
     * }
     * </code>
     * </pre>
     * <p/>
     * </p>
     *
     * @param m {@link Map}; may be null
     * @return {@link Set} of {@link java.util.Map.Entry map entries}
     */
    public static <K, V> Set<Map.Entry<K, V>> entries(Map<K, V> m) {
        if (m == null) {
            return emptySet();
        }
        return m.entrySet();
    }

    /**
     * Extends {@code Object.equals()} to make {@code null}s equal.
     *
     * @param o1 an object
     * @param o2 another object
     * @return {@code true} if both {@code o1} and {@code o2} are null or
     *         {@code o1} equals {@code o2} in the sense of
     *         {@link Object#equals(Object)} .
     */
    public static boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    /**
     * Logs the message to the bundle's error level.
     *
     * @param b {@link BundleContext}
     * @param msg {@link String}
     */
    public static void error(final BundleContext b, final String msg) {
        final String symbolicName = b.getBundle().getSymbolicName();
        final Logger log = getLogger(symbolicName);
        log.error(prefix(b, msg));
    }

    /**
     * Logs the message to the bundle's error level.
     *
     * @param b {@link BundleContext}
     * @param msg {@link String}
     * @param t {@link Throwable}
     */
    public static void error(final BundleContext b, final String msg,
                             final Throwable t) {
        final String symbolicName = b.getBundle().getSymbolicName();
        final Logger log = getLogger(symbolicName);
        log.error(prefix(b, msg), t);
    }

    /**
     * Applies the {@link FilterFunction fx} to each of the items in the
     * {@link Iterable iterable}, returning the first found match or null.
     *
     * @param iter {@link Iterable} the {@link Iterable iterable} of type
     * {@code <T>}
     * @param fx {@link FilterFunction} the filter function to apply to type
     * {@code <T>}, which should not be {@code null}
     * @return {@code <T>} the first found item of the {@link Iterable
     *         iterable}
     *         or {@code null} if either no match was found, {@code fx} is
     *         {@code null},
     *         or {@code iter} is {@code null}
     */
    public static <T> T findFirst(Iterable<T> iter, FilterFunction<T> fx) {
        if (nulls(iter, fx)) {
            return null;
        }

        for (final T t : iter) {
            if (fx.match(t)) return t;
        }
        return null;
    }

    /**
     * Applies the {@link FilterFunction fx} to each of the items in the
     * {@link Iterable iterable}, returning the last found match or null.
     *
     * @param iter {@link Iterable} the {@link Iterable iterable} of type
     * {@code <T>}
     * @param fx {@link FilterFunction} the filter function to apply to type
     * {@code <T>}, which should not be {@code null}
     * @return {@code <T>} the last found item of the {@link Iterable iterable}
     *         or {@code null} if either no match was found, {@code fx} is
     *         {@code null},
     *         or {@code iter} is {@code null}
     */
    public static <T> T findLast(Iterable<T> iter, FilterFunction<T> fx) {
        if (nulls(iter, fx)) {
            return null;
        }

        T match = null;
        for (final T t : iter) {
            if (fx.match(t)) {
                match = t;
            }
        }
        return match;
    }

    /**
     * Returns a new {@link Collection} containing only items from
     * {@link Collection c} that match {@link FilterFunction fx}.
     *
     * @param c {@link Collection}; {@code null} returns {@code null}
     * @param fx {@link FilterFunction}; {@code null} returns {@code null}
     * @return {@link Collection}
     */
    public static <T> Collection<T> filter(Collection<T> c, FilterFunction<T> fx) {
        if (nulls(c, fx)) {
            return null;
        }

        Iterator<T> it = c.iterator();
        List<T> l = sizedArrayList(c.size());
        while (it.hasNext()) {
            T t = it.next();
            if (fx.match(t)) {
                l.add(t);
            }
        }
        return l;
    }

    /**
     * Applies the function {@code fx} to each item {@code <T>} in the
     * {@link Iterable iterable}.
     * <p>
     * If either {@code iter} or {@code fx} is {@code null} then no operation
     * is
     * performed.
     * </p>
     *
     * @param iter {@link Iterable iterable} of type {@code <T>}
     * @param fx the {@link Function function} to apply to type {@code <T>}
     */
    public static <T> void forEach(Iterable<T> iter, Function<? super T> fx) {
        if (nulls(iter, fx)) {
            return;
        }

        for (final T t : iter) {
            fx.apply(t);
        }
    }

    /**
     * Applies the function {@code fx} to each of the {@link #entries(Map)
     * entries} within the supplied map.
     *
     * @param map Non-null {@link Map}
     * @param fx Non-null {@link EntryFunction} to apply
     */
    public static <K, V> void forEach(Map<K, V> map, EntryFunction<K, V> fx) {
        Set<Entry<K, V>> entries = entries(map);
        for (final Entry<K, V> entry : entries) {
            K key = entry.getKey();
            V value = entry.getValue();
            fx.apply(key, value);
        }
    }

    /**
     * Delegate for {@link BundleContext#getProperty(String)}.
     * <p>
     * Returns the value of the specified property. If the key is not found in
     * the Framework properties, the system properties are then searched. The
     * method returns <code>null</code> if the property is not found.
     * </p>
     *
     * @param ctxt The {@link BundleContext bundle context}
     * @param key The name of the requested property.
     * @return The value of the requested property, or <code>null</code> if the
     *         property is undefined.
     * @throws SecurityException If the caller does not have the appropriate
     * <code>PropertyPermission</code> to read the property, and the Java
     * Runtime Environment supports permissions.
     */
    public static String getBundleProperty(BundleContext ctxt, String key) {
        return ctxt.getProperty(key);
    }

    /**
     * Returns the first cause (<em>primium movens</em>) for a {@link Throwable
     * throwable}.
     *
     * @param t the {@link Throwable throwable}
     * @return {@link Throwable} the first cause for the throwable, the
     *         original
     *         throwable if this is the first cause, or <tt>null</tt> if the
     *         <tt>t</tt>
     *         throwable was null
     */
    public static Throwable getFirstCause(Throwable t) {
        if (t == null) {
            return null;
        }

        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        return cause;
    }

    /**
     * Returns the first message available in a stack trace.
     * <p>
     * This method can be used to obtain the first occurrence of
     * {@link Exception#getMessage() the exception message} through a series of
     * {@link Exception#getCause() causes}.
     * </p>
     *
     * @param t the {@link Throwable throwable}
     * @return {@link String}; may be null
     */
    public static String getFirstMessage(Throwable t) {
        if (t == null) {
            return null;
        }

        String ret = t.getMessage();
        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
            if (cause.getMessage() != null) {
                ret = cause.getMessage();
            }
        }
        return ret;
    }

    /**
     * Returns the virtual machine's process identifier.
     *
     * @return {@code int}
     */
    public static int getPID() {
        if (pid == -1) {
            RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
            String name = mx.getName();
            String token = name.split("@")[0];
            pid = parseInt(token);
        }
        return pid;
    }

    /**
     * Returns the {@link Thread#currentThread() current thread's} identifier.
     *
     * @return {@code long}
     */
    public static long getThreadID() {
        return currentThread().getId();
    }

    /**
     * Returns {@code true} if the collection is non-null and non-empty,
     * {@code false} otherwise.
     *
     * @param c Collection, may be null
     * @return boolean
     */
    public static boolean hasItems(final Collection<?> c) {
        return c != null && !c.isEmpty();
    }

    /**
     * Returns {@code true} if the map is non-null and is non-empty,
     * {@code false} otherwise.
     *
     * @param <K> Captured key type
     * @param <V> Captured value type
     * @param m Map of type {@code <K, V>}, may be null
     * @return boolean
     */
    public static <K, V> boolean hasItems(final Map<K, V> m) {
        return m != null && !m.isEmpty();
    }

    /**
     * Returns {@code true} if the array is non-null and has a length greater
     * than zero, {@code false} otherwise.
     *
     * @param <T> Captured array type
     * @param t Array of type {@code <T>}
     * @return boolean
     */
    public static <T> boolean hasItems(final T[] t) {
        return t != null && t.length > 0;
    }

    /**
     * Returns {@code true} if the string is non-null and non-empty,
     * {@code false} otherwise.
     *
     * @param s String, may be null
     * @return boolean
     */
    public static boolean hasLength(final String s) {
        return s != null && !s.isEmpty();
    }

    /**
     * Returns {@code true} if the element {@code t} is in the array of
     * {@code T}.
     *
     * @param t Non-null element to search for
     * @param ts Array of {@code T} to search in; may contain nulls
     * @param <T>
     * @return {@code true} if {@code t} is found in the array,
     *         {@code false} otherwise
     */
    public static <T> boolean in(final T t, final T[] ts) {
        if (ts == null) return false;
        for (final T elem : ts) if (t.equals(elem)) return true;
        return false;
    }

    /**
     * Returns {@code true} if the key is in the map, {@code false} otherwise.
     *
     * @param key Key
     * @param map {@link Map}
     * @return boolean
     */
    public static boolean in(final Object key, final Map<?, ?> map) {
        if (map == null) return false;
        return map.containsKey(key);
    }

    /**
     * Returns {@code true} if the object is in the set, {@code false}
     * otherwise.
     *
     * @param obj Object
     * @param set {@link Set}
     * @return boolean
     */
    public static boolean in(final Object obj, final Set<?> set) {
        if (obj == null) return false;
        return set.contains(obj);
    }

    /**
     * Returns an array-based variant of the provided indexed map. This method
     * <b>should not</b> be used if any of the keys used are greater than the
     * size of the map. This is very useful if you have a {@link Map map} where
     * each value is given an index starting from zero. This is a very common
     * pattern in the BEL framework.
     * <p>
     * Here is an example where the use of this method would be beneficial.
     * Given some {@code list} of 100,000 elements, an index is assigned to
     * each
     * element of the list:
     * <p/>
     * <pre>
     * <code>
     * Map<Integer, MyObject> map = sizedHashMap(list.size());
     * for (int i = 0; i < list.size(); i++) {
     *     map.put(i, list.get(i));
     * }
     * </code>
     * </pre>
     * <p/>
     * A typical use of {@code map} follows:
     * <p/>
     * <pre>
     * <code>
     * for (int i = 0; i < map.size(); i++) {
     *     MyObject m = map.get(i);
     *     // use "m"
     * }
     * </code>
     * </pre>
     * <p/>
     * The problem with such code is the costly invocation to
     * {@link Map#get(Object)} on every iteration. This becomes very expensive
     * with very large collections.
     * </p>
     * <p>
     * Different ways of accessing the data becomes necessary (for example,
     * using a {@link List list} structure and calling {@link List#get(int)},
     * see guidelines below). Taking this a step further, we can very easily
     * rewrite this map as an array and get considerable speed improvements.
     * <p/>
     * <pre>
     * <code>
     * </code>
     * MyObject[] indexed = index(map);
     * for (int i = 0; i < indexed.length; i++) {
     *     MyObject m = indexed[i];
     *     // use "m"
     * }
     * </pre>
     * <p/>
     * Whatever costs you pay up front for indexing the map quickly vanish on
     * each iteration of the loop.
     * </p>
     * <p/>
     * <h3>Guidelines</h3> Considering the following as a good baseline when it
     * comes to using large numbers of objects.
     * <ol>
     * <li>{@link Map#get(Object)} can be fast when using optimized maps and
     * hash functions though will usually perform worse than {@link List
     * lists}.
     * <li>{@link List#get(int)} almost always beats {@link Map#get(Object)} in
     * performance.</li>
     * <li>Index access into an array always beats accessing either a
     * {@link List list} or {@link Map map}.
     * </ol>
     *
     * @param map The {@link Map map} to index. The keys used by the map
     * <b>should never</b> be greater than the {@link Map#size() size} of the
     * map. Under ideal conditions, each value {@code 0 <= x < = map.size()}
     * should have a key assigned. Otherwise, the resulting array will waste
     * memory (i.e., it will be a spare array).
     * @return {@code T[]}
     */
    public static <T> T[] index(Class<T> cls, Map<Integer, T> map) {
        final int size = map.size();
        @SuppressWarnings("unchecked")
        T[] ret = (T[]) newInstance(cls, size);
        Set<Entry<Integer, T>> entries = entries(map);

        for (final Entry<Integer, T> e : entries) {
            if (e.getKey() == null) continue;
            int key = e.getKey();
            T value = e.getValue();
            ret[key] = value;
        }
        return ret;
    }

    /**
     * Logs the message to the bundle's info level.
     *
     * @param b {@link BundleContext}
     * @param msg {@link String}
     */
    public static void info(final BundleContext b, final String msg) {
        final String symbolicName = b.getBundle().getSymbolicName();
        final Logger log = getLogger(symbolicName);
        log.info(prefix(b, msg));
    }

    /**
     * Returns true if a string contains one or more alphanumeric (i.e., the
     * {@code Alnum} character class) characters and nothing else.
     *
     * @param s {@link String}
     * @return boolean
     */
    public static boolean isAlphanumeric(String s) {
        if (!hasLength(s)) {
            return false;
        }
        Matcher m = ALPHA_REGEX.matcher(s);
        return m.matches();
    }

    /**
     * Returns {@code true} if {@link String s} is numeric (i.e., the
     * {@code Digit} POSIX character class) characters and nothing else.
     *
     * @param s {@link String}
     * @return boolean
     */
    public static boolean isNumeric(String s) {
        if (!hasLength(s)) {
            return false;
        }
        Matcher m = DIGIT_REGEX.matcher(s);
        return m.matches();
    }

    /**
     * Returns {@code true} if {@code port} is a valid value, {@code false} if
     * it is not valid.
     *
     * @param port the port number
     * @return boolean
     */
    public static boolean isValidPortNumber(int port) {
        return port > MIN_PORT && port <= MAX_PORT;
    }

    /**
     * Joins the elements of the provided {@link Collection collection} into a
     * single {@link String string}, using the provided {@code separator} and
     * string elements.
     * <p>
     * For example: <br>
     * <blockquote> {@code join(asList("foo", "bar"), "*")}<br>
     * </blockquote> returns<br>
     * <blockquote> {@code "foo*bar"} </blockquote>
     * </p>
     *
     * @param strings {@link Object objects} to stringify and join together
     * @param separator Separator {@link String string}
     * @return String
     */
    public static String join(Collection<? extends Object> strings,
                              String separator) {
        StringBuilder sb = new StringBuilder();
        if (strings != null) {
            Iterator<? extends Object> i = strings.iterator();
            while (i.hasNext()) {
                sb.append(i.next());
                if (i.hasNext()) {
                    sb.append(separator);
                }
            }
        }
        return sb.toString();
    }

    /**
     * Applies the function {@code fx} to each of the {@link #entries(Map)
     * entries} within the supplied map.
     *
     * @param map Non-null {@link Map}
     * @param fx Non-null {@link EntryFunction} to apply
     */
    public static <K, V> void mapfx(Map<K, V> map, EntryFunction<K, V> fx) {
        Set<Entry<K, V>> entries = entries(map);
        for (final Entry<K, V> entry : entries) {
            K key = entry.getKey();
            V value = entry.getValue();
            fx.apply(key, value);
        }
    }

    /**
     * Returns {@code true} if the collection is null or empty, {@code false}
     * otherwise.
     *
     * @param c Collection, may be null
     * @return boolean
     */
    public static boolean noItems(final Collection<?> c) {
        return !hasItems(c);
    }

    /**
     * Returns {@code true} if the map is null or empty, {@code false}
     * otherwise.
     *
     * @param <K> Captured key type
     * @param <V> Captured value type
     * @param m Map of type {@code <K, V>}, may be null
     * @return boolean
     */
    public static <K, V> boolean noItems(final Map<K, V> m) {
        return !hasItems(m);
    }

    /**
     * Returns {@code true} if the array is null or has no elements,
     * {@code false} otherwise.
     *
     * @param <T> Captured array type
     * @param t Array of type {@code <T>}, may be null
     * @return boolean
     */
    public static <T> boolean noItems(final T[] t) {
        return !hasItems(t);
    }

    /**
     * Returns {@code true} if the string is null or empty, {@code false}
     * otherwise.
     *
     * @param s String, may be null
     * @return boolean
     */
    public static boolean noLength(final String s) {
        return !hasLength(s);
    }

    /**
     * Returns {@code true} if any {@link String} in <tt>strings</tt> is null
     * or
     * empty, {@code false} otherwise.
     *
     * @param strings {@code String[]}, may be null
     * @return boolean
     */
    public static boolean noLength(final String... strings) {
        if (strings == null) {
            return true;
        }

        for (final String string : strings) {
            if (!hasLength(string)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns {@code true} if no null arguments are provided, {@code false}
     * otherwise.
     *
     * @param objects Objects, may be null
     * @return boolean
     */
    public static boolean noNulls(final Object... objects) {
        if (objects == null) return false;
        for (final Object object : objects) {
            if (object == null) return false;
        }
        return true;
    }

    /**
     * Returns {@code true} if the element {@code t} is not in the array of
     * {@code T}.
     *
     * @param t Non-null element to search for
     * @param ts Array of {@code T} to search in; may contain nulls
     * @param <T>
     * @return {@code true} if {@code t} is not found in the array,
     *         {@code false} otherwise
     */
    public static <T> boolean not_in(final T t, final T[] ts) {
        if (ts == null) return true;
        return !in(t, ts);
    }

    /**
     * Returns {@code true} if the key is not in the map,
     * {@code false} otherwise.
     *
     * @param key Key
     * @param map {@link Map}
     * @return boolean
     */
    public static boolean not_in(final Object key, final Map<?, ?> map) {
        if (map == null) return true;
        return !map.containsKey(key);
    }

    /**
     * Returns {@code true} if null arguments are provided, {@code false}
     * otherwise.
     *
     * @param objects Objects, may be null
     * @return boolean
     */
    public static boolean nulls(final Object... objects) {
        if (objects == null) return true;
        for (final Object object : objects) {
            if (object == null) return true;
        }
        return false;
    }

    /**
     * Returns a hash map of type {@code K, V} optimized to the specified
     * capacity and load factor.
     * <p>
     * Use optimized hash maps when the capacity of a hash map is known to be
     * greater than sixteen (the default initial capacity) and a load factor is
     * desired to control resizing behavior. The hash map implementation will
     * automatically adjust the size to the next nearest power of two.
     * </p>
     *
     * @param <K> Formal type parameter key
     * @param <V> Formal type parameter value
     * @param s Initial hash map capacity
     * @param lf Hash map load factor
     * @return Hash map of type {@code K, V}
     */
    public static <K, V> HashMap<K, V> optimizedHashMap(final int s,
                                                        final float lf) {
        return new HashMap<K, V>(s, lf);
    }

    /**
     * Returns a hash set of type {@code T} optimized to the specified capacity
     * and load factor.
     * <p>
     * Use optimized hash sets when the capacity of a hash set is known to be
     * greater than sixteen (the default initial capacity) and a load factor is
     * desired to control resizing behavior. The hash set implementation will
     * automatically adjust the size to the next nearest power of two.
     * </p>
     *
     * @param <T> Formal type parameter
     * @param s Initial hash set capacity
     * @param lf Hash set load factor
     * @return Hash set of type {@code T}
     */
    public static <T> HashSet<T> optimizedHashSet(final int s, final float lf) {
        return new HashSet<T>(s, lf);
    }

    /**
     * Prefixes string {@code s} with the format:<br/>
     * {@code <symbolicName>: <s>}
     *
     * @param b {@link BundleContext}
     * @param s {@link String}
     * @return {@link String}
     */
    public static String prefix(final BundleContext b, final String s) {
        requireNonNull(s);
        return b.getBundle().getSymbolicName().concat(": ".concat(s));
    }

    /**
     * Returns {@code true} if every {@link File} in {@code files} is non-null
     * and can be read, {@code false} otherwise.
     *
     * @param files {@code File[]}; may be null
     * @return boolean
     */
    public static boolean readable(final File... files) {
        if (!hasItems(files)) {
            return false;
        }

        boolean readable = true;
        for (File f : files) {
            readable &= f.canRead();
        }
        return readable;
    }

    /**
     * Applies the search function {@code fx} to each of the items in the
     * {@link Iterable iterable}, returning the first found match or null.
     *
     * @param iter {@link Iterable}
     * @param fx {@link SearchFunction}
     * @return {@code <T>}
     */
    public static <T> T search(Iterable<T> iter, SearchFunction<T> fx) {
        for (final T t : iter) {
            if (fx.match(t)) return t;
        }
        return null;
    }

    /**
     * Returns a sized array list of type {@code T}.
     *
     * @param <T> Formal type parameter
     * @param size Array list size
     * @return Array list of type {@code T}
     */
    public static <T> ArrayList<T> sizedArrayList(final int size) {
        return new ArrayList<T>(size);
    }

    /**
     * Returns a hash map of type {@code K, V} with initial capacity
     * {@code size}.
     * <p>
     * Use sized hash maps when the capacity of a hash map is known to be
     * greater than sixteen (the default initial capacity). The hash map
     * implementation will automatically adjust the size to the next nearest
     * power of two.
     * </p>
     *
     * @param <K> Formal type parameter key
     * @param <V> Formal type parameter value
     * @param size Hash map initial capacity
     * @return Hash map of type {@code K, V}
     */
    public static <K, V> HashMap<K, V> sizedHashMap(final int size) {
        return new HashMap<K, V>(size);
    }

    /**
     * Returns a hash set of type {@code T} with initial capacity {@code size}.
     * <p>
     * Use sized hash sets when the capacity of a hash set is known to be
     * greater than sixteen (the default initial capacity). The hash set
     * implementation will automatically adjust the size to the next nearest
     * power of two.
     * </p>
     *
     * @param <T> Formal type parameter
     * @param size Hash set initial capacity
     * @return Hash set of type {@code T}
     */
    public static <T> HashSet<T> sizedHashSet(final int size) {
        return new HashSet<T>(size);
    }

    /**
     * Check equality of two substrings. This method does not create
     * intermediate {@link String} objects and is roughly equivalent to:
     * <p/>
     * <pre>
     * <code>
     * String sub1 = s1.substring(fromIndex1, toIndex1);
     * String sub2 = s2.substring(fromIndex2, toIndex2);
     * sub1.equals(sub2);
     * </code>
     * </pre>
     *
     * @param s1 First string
     * @param fromIndex1 Starting index within {@code s1}
     * @param toIndex1 Ending index within {@code s1}
     * @param s2 Second string
     * @param fromIndex2 Starting index within {@code s2}
     * @param toIndex2 Ending index within {@code s2}
     * @return {@code boolean}
     */
    public static boolean substringEquals(final String s1,
                                          final int fromIndex1, final int toIndex1,
                                          final String s2, final int fromIndex2, final int toIndex2) {

        if (toIndex1 < fromIndex1) {
            throw new IndexOutOfBoundsException();
        }
        if (toIndex2 < fromIndex2) {
            throw new IndexOutOfBoundsException();
        }

        final int len1 = toIndex1 - fromIndex1;
        final int len2 = toIndex2 - fromIndex2;
        if (len1 != len2) {
            return false;
        }

        for (int i = 0; i < len1; ++i) {
            if (s1.charAt(fromIndex1 + i) != s2.charAt(fromIndex2 + i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Converts into seconds and returns a string in the format
     * {@code <seconds>.<milliseconds>}.
     *
     * @param milliseconds
     * @return String
     */
    public static String timeFormat(final long milliseconds) {
        double seconds = milliseconds / 1000.0d;
        final NumberFormat fmt = new DecimalFormat("#0.000");
        return fmt.format(seconds);
    }

    /**
     * Returns the path associated with a file protocol URL.
     *
     * @param fileProtocol File protocol URL
     * @return {@link String}; path
     * @throws MalformedURLException if no protocol is specified, or an
     * unknown protocol is found, or <tt>spec</tt> is <tt>null</tt>.
     */
    public static String toPath(String fileProtocol)
            throws MalformedURLException {
        URL url = new URL(fileProtocol);
        return url.getFile();
    }

    /**
     * Captures all objects of type {@code <T>} contained in the provided list
     * as a new checked list.
     *
     * @param <T> Captured type for new checked list
     * @param objects List of objects
     * @param t Class type to capture
     * @return Checked list of type {@code T}; may be empty
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> typedList(List<?> objects, Class<T> t) {
        if (objects == null || objects.isEmpty()) {
            return emptyList();
        }
        List<T> ret = new ArrayList<T>();
        for (final Object o : objects) {
            if (o == null) {
                ret.add(null);
                continue;
            }

            Class<?> oc = o.getClass();
            if (oc == t || t.isAssignableFrom(oc)) {
                ret.add((T) o);
            }
        }
        return ret;
    }

    /**
     * Returns a new {@link Iterator} of {@link List} elements.  The type of
     * the {@link List} elements are guaranteed to be of type {@code t}.
     *
     * <p>
     * If an element of any {@link List} cannot be assigned to {@code t} then a
     * {@link RuntimeException} is thrown.
     *
     * @param it {@link Iterator} of generic {@link List} elements;
     * {@code null} returns {@link Collections#emptyIterator()}
     * @param t {@link Class} of {@link List} element type; may not be
     * {@code null}
     * @return {@link Iterator} of {@link List} containing {@code <T>} type
     * elements
     * @throws NullPointerException when {@code t} is {@code null}
     * @throws RuntimeException when a {@link List} element cannot be assigned
     * to {@code <T>} type
     */
    public static <T> Iterator<List<T>> typedListIterator(
            @SuppressWarnings("rawtypes") final Iterator<List> it,
            final Class<T> t) {
        requireNonNull(t);
        if (it == null) {
            return emptyIterator();
        }

        return new Iterator<List<T>>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }
            @Override
            public List<T> next() {
                List<?> l = it.next();
                for (Object lo : l) {
                    Class<?> loc = lo.getClass();
                    if (!t.isAssignableFrom(loc)) {
                        String msg = "expected list of type %s, got %s";
                        throw new RuntimeException(format(msg, t.getName(),
                                loc.getName()));
                    }
                }
                @SuppressWarnings("unchecked")
                List<T> typedList = (List<T>) l;
                return typedList;
            }
            @Override
            public void remove() {
                it.remove();
            }
        };
    }

    /**
     * Capture all {@code K}-keys and {@code V}-values from the provided map.
     *
     * @param map {@link Map}; may be null
     * @param <K> Map keys
     * @param <V> Map values
     * @return {@link Map} of type {@code K}, {@code V}; may be empty
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> capture(Class<K> keycls, Class<V> valcls,
                                           Map<?, ?> map) {
        Map<K, V> ret = new HashMap<K, V>(map.size());
        Set<? extends Entry<?, ?>> entries = map.entrySet();
        for (final Entry<?, ?> e : entries) {
            Object key = e.getKey();
            Object value = e.getValue();
            if (!keycls.isAssignableFrom(key.getClass())) continue;
            if (!valcls.isAssignableFrom(value.getClass())) continue;
            ret.put((K) key, (V) value);
        }
        return ret;
    }

    /**
     * Returns {@code true} if {@code file} is non-null and can be written,
     * {@code false} otherwise.
     *
     * @param file {@link File}; may be null
     * @return boolean
     */
    public static boolean writable(final File file) {
        if (file != null && file.canWrite()) return true;
        return false;
    }

    /**
     * Capitalize the first letter of each word separated by whitespace.
     * <p/>
     * <p>
     * This method instantiates a new {@link String}.
     * </p>
     *
     * @param s {@link String}
     * @return capitalized {@link String} or the original {@link String} if
     *         {@code null} or the empty {@link String ""}
     */
    public static String capitalize(String s) {
        if (noLength(s)) {
            return s;
        }

        StringBuilder buf = new StringBuilder(s.length());
        char previous = SPACE_SEPARATOR;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (isWhitespace(previous)) {
                buf.append(toTitleCase(c));
            } else {
                buf.append(c);
            }
            previous = c;
        }
        return buf.toString();
    }

    /**
     * Returns the max {@link String} length from a {@link Collection}.
     *
     * @param strings {@link Collection} of {@link String}; may not be
     * {@code null}
     * @return {@code int} max length
     */
    public static int maxLength(Collection<String> strings) {
        requireNonNull(strings, "strings is null");
        Iterator<String> i = strings.iterator();
        int max = 0;
        while (i.hasNext()) {
            String str = i.next();
            if (str != null) {
                int l = str.length();
                if (l > max) {
                    max = l;
                }
            }
        }
        return max;
    }

    /**
     * Fills out a {@link Collection} of {@code <T>} to some {@code int} new
     * length using the {@code <T> obj}.
     * <p/>
     * <p>
     * The {@link Collection} will only change when {@code newLength} is
     * greater than its current size.  Otherwise it is considered a no-op.
     * </p>
     * <p/>
     * <p>
     * The order of the objects in the {@link Collection} depends on the
     * implementation.  To ensure sequential ordering of fills try using
     * {@link Utility#fillOut(List, Object, int)}.
     * </p>
     *
     * @param col {@link Collection}
     * @param obj {@link Object} of type {@code <T>}; may be {@code null}
     * @param newLength {@code int}
     */
    public static <T> void fillOut(Collection<T> col, T obj, int newLength) {
        requireNonNull(col, "col is null");
        int len = col.size();
        if (len < newLength) {
            int remainder = newLength - len;
            col.addAll(Collections.nCopies(remainder, obj));
        }
    }

    /**
     * Fills out a {@link List} of {@code <T>} to some {@code int} new
     * length using the {@code <T> obj}.  The fills will be sequentially added
     * starting at the {@link List}'s current size.
     * <p/>
     * <p>
     * The {@link List} will only change when {@code newLength} is
     * greater than its current size.  Otherwise it is considered a no-op.
     * </p>
     * <p/>
     * <p>
     * If sequential ordering of fills is not important then try using
     * {@link Utility#fillOut(Collection, Object, int)}.
     * </p>
     *
     * @param list {@link List}
     * @param obj {@link Object} of type {@code <T>}; may be {@code null}
     * @param newLength {@code int}
     */
    public static <T> void fillOut(List<T> list, T obj, int newLength) {
        requireNonNull(list, "list is null");
        int len = list.size();
        if (len < newLength) {
            int remainder = newLength - len;
            list.addAll(len, Collections.nCopies(remainder, obj));
        }
    }

    /**
     * Maps a {@link Collection} of type {@code <T>} to a {@link Collection} of
     * type {@code <U>} by applying the {@link MapFunction} to each element.
     *
     * @param o {@link Collection} of type {@code <T>}
     * @param fx {@link MapFunction}; may not be {@code null}
     * @return {@link Collection} of type {@code <U>},
     *         {@link Collections#emptyList()} if {@code o} was {@code null} or
     *         empty
     * @throws NullPointerException when {@code fx} is {@code null} and
     * {@code o} has items
     */
    public static <T, U> Collection<U> map(final Collection<T> o,
                                           MapFunction<T, U> fx) {
        if (noItems(o)) {
            return emptyList();
        }
        requireNonNull(fx, "fx is null");
        List<U> l = sizedArrayList(o.size());
        Iterator<T> i = o.iterator();
        while (i.hasNext()) {
            l.add(fx.map(i.next()));
        }
        return l;
    }

    /**
     * Folds a {@link List} of type {@code <T>} from the left starting with an
     * initial value of type {@code <U>}.  The result is the final accumulated
     * value of type {@code <U>} but will be the initial value if {@code o} is
     * {@code null} or empty.
     *
     * @param o {@link Collection}
     * @param fx {@link FoldFunction}; may not be {@code null}
     * @return {@code <U>}; the accumulated value or the initial value if
     *         {@code o} is {@code null} or empty
     */
    public static <T, U> U fold(final Collection<T> o, FoldFunction<T, U> fx) {
        requireNonNull(fx, "fx is null");
        U accumulated = fx.initial();
        if (noItems(o)) return accumulated;
        for (T item : o) {
            accumulated = fx.fold(item, accumulated);
        }
        return accumulated;
    }

    /**
     * Creates a new {@link ThreadFactory thread factory} that creates
     * {@link Thread threads} based on your desired configuration.
     * <p/>
     * <p>
     * The {@code threadName} will be set on each create {@link Thread thread}
     * and cannot be {@code null}.
     * </p>
     * <p/>
     * <p/>
     * The {@link Boolean daemonize} parameter can be {@code null} meaning this
     * {@link Thread thread} will inherit the daemon status from its parent.
     * {@link Thread thread} is a daemon if {@link Boolean#TRUE true} otherwise
     * it is not a daemon.
     * <p/>
     * <p/>
     * <p/>
     * The {@link UncaughtExceptionHandler exception handler} can be set on
     * each new {@link Thread thread} allowing uncaught
     * {@link Exception exceptions} to be handled in a specific way.
     *
     * @param threadName {@link String}; may not be {@code null}
     * @param daemonize {@link Boolean}; can be {@code null}
     * @param exHndlr {@link UncaughtExceptionHandler}; can be {@code null}
     * @return new {@link ThreadFactory} configured with {@code threadName},
     *         {@code daemonize}, and {@code exHndlr}
     * @throws NullPointerException when {@code threadName} is {@code null}
     */
    public static ThreadFactory threadFactory(final String threadName,
                                              final Boolean daemonize, final UncaughtExceptionHandler exHndlr) {
        requireNonNull(threadName, "threadName is null");
        return new ThreadFactory() {
            /**
             * {@inheritDoc}
             * <br><br>
             * Creates a daemon {@link Thread} named {@code threadName}.
             */
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, threadName);
                if (daemonize != null) t.setDaemon(daemonize);
                if (exHndlr != null) t.setUncaughtExceptionHandler(exHndlr);
                return t;
            }
        };
    }

    /**
     * Return a {@link Collection} of all lowest-valued {@code T}.
     *
     * <p>
     * For example a list containing: <pre>[7, 1, 5, 1, 3]</pre>
     * Will return: <pre>[1, 1]</pre>
     *
     * @param col {@link Collection} of type {@code T}
     * @return {@link Collection} of lowest-valued {@code T} or {@code null} if
     * {@code col} is {@code null} or empty
     */
    public static <T extends Comparable<T>> Collection<T> min(Collection<T> col) {
        if (col == null || col.isEmpty())
            return null;

        Collection<T> result = sizedArrayList(col.size());
        Iterator<T> it = col.iterator();
        T min = it.next();
        result.add(min);
        while (it.hasNext()) {
            T next = it.next();
            int comp = next.compareTo(min);
            if (comp < 0) {
                min = next;
                result = new ArrayList<T>();
                result.add(next);
            } else if (comp == 0) {
                result.add(next);
            }
        }
        return result;
    }

    /**
     * Return a {@link Collection} of all lowest-valued {@code T}.
     *
     * <p>
     * For example a list containing: <pre>[7, 1, 5, 1, 3]</pre>
     * Will return: <pre>[1, 1]</pre>
     *
     * @param col {@link Collection} of type {@code T}
     * @param c {@link Comparator} of type {@code T}
     * @return {@link Collection} of lowest-valued {@code T} or {@code null} if
     * {@code col} is {@code null} or empty
     */
    public static <T> Collection<T> min(Collection<T> col, Comparator<T> c) {
        if (col == null || col.isEmpty())
            return null;

        Collection<T> result = sizedArrayList(col.size());
        Iterator<T> it = col.iterator();
        T min = it.next();
        result.add(min);
        while (it.hasNext()) {
            T next = it.next();
            int comp = c.compare(next, min);
            if (comp < 0) {
                min = next;
                result = new ArrayList<T>();
                result.add(next);
            } else if (comp == 0) {
                result.add(next);
            }
        }
        return result;
    }

    /**
     * Return a {@link Collection} of all highest-valued {@code T}.
     *
     * <p>
     * For example a list containing: <pre>[1, 3, 5, 1, 1]</pre>
     * Will return: <pre>[1, 1, 1]</pre>
     *
     * @param col {@link Collection} of type {@code T}
     * @return {@link Collection} of highest-valued {@code T} or {@code null} if
     * {@code col} is {@code null} or empty
     */
    public static <T extends Comparable<T>> Collection<T> max(Collection<T> col) {
        if (col == null || col.isEmpty())
            return null;

        Collection<T> result = sizedArrayList(col.size());
        Iterator<T> it = col.iterator();
        T max = it.next();
        result.add(max);
        while (it.hasNext()) {
            T next = it.next();
            int comp = next.compareTo(max);
            if (comp > 0) {
                max = next;
                result = new ArrayList<T>();
                result.add(next);
            } else if (comp == 0) {
                result.add(next);
            }
        }
        return result;
    }

    /**
     * Return a {@link Collection} of all highest-valued {@code T}.
     *
     * <p>
     * For example a list containing: <pre>[1, 3, 5, 1, 1]</pre>
     * Will return: <pre>[1, 1, 1]</pre>
     *
     * @param col {@link Collection} of type {@code T}
     * @param c {@link Comparator} of type {@code T}
     * @return {@link Collection} of highest-valued {@code T} or {@code null}
     * if {@code col} is {@code null} or empty
     */
    public static <T> Collection<T> max(Collection<T> col, Comparator<T> c) {
        if (col == null || col.isEmpty())
            return null;

        Collection<T> result = sizedArrayList(col.size());
        Iterator<T> it = col.iterator();
        T max = it.next();
        result.add(max);
        while (it.hasNext()) {
            T next = it.next();
            int comp = c.compare(next, max);
            if (comp > 0) {
                max = next;
                result = new ArrayList<T>();
                result.add(next);
            } else if (comp == 0) {
                result.add(next);
            }
        }
        return result;
    }

    /**
     * Partitions a {@link Collection} of {@code T} using a
     * {@link FilterFunction}.  The first partition contains {@code T} that
     * matched the {@link FilterFunction} and the second partition contains
     * {@code T} that did not.
     *
     * <p>
     * For example a list containing: <pre>[true, true, false, false]</pre>
     * Will return a {@link Pair} of collections:
     * <pre>[true, true] and [false, false]</pre>
     * For a {@link FilterFunction} that selects by truthiness.
     *
     * @param col {@link Collection} of type {@code T}
     * @param partitionFx {@link FilterFunction} of type {@code T}; may not be
     * {@code null}
     * @return {@link Pair} of partitioned {@link Collection} of type
     * {@code T}, {@code null} if {@code col} was {@code null} or empty, or
     * a {@link Pair} containing the first original {@link Collection} if the
     * {@code partitionFx} was {@code null}
     * @throws NullPointerException when {@code partitionFx} is {@code null}
     */
    public static <T> Pair<Collection<T>, Collection<T>> partition(
            Collection<T> col, FilterFunction<T> partitionFx) {
        requireNonNull(partitionFx);
        if (col == null || col.isEmpty())
            return null;
        if (partitionFx == null)
            return new Pair<Collection<T>, Collection<T>>(col, null);

        // initialize with maximal size
        Collection<T> partition1 = sizedArrayList(col.size());
        Collection<T> partition2 = sizedArrayList(col.size());
        for (T t : col) {
            if (partitionFx.match(t))
                partition1.add(t);
            else
                partition2.add(t);
        }
        return new Pair<Collection<T>, Collection<T>>(partition1, partition2);
    }

    /**
     * Buckets a {@link Collection} of {@code T} using two
     * {@link FilterFunction}.  The first partition contains {@code T} that
     * matched the first {@link FilterFunction} and the second partition
     * contains {@code T} that matched the second {@link FilterFunction}.  Any
     * {@code T} that do not match either {@link FilterFunction} are
     * excluded from both partitions.
     *
     * @param col {@link Collection} of type {@code T}
     * @param partitionFx1 {@link FilterFunction} of type {@code T}; may not be
     * {@code null}
     * @param partitionFx2 {@link FilterFunction} of type {@code T}; may not be
     * {@code null}
     * @return {@link Pair} of bucketed {@link Collection} of type {@code T},
     * {@code null} if {@code col} was {@code null} or empty
     * @throws NullPointerException when {@code partitionFx1} or
     * {@code partitionFx2} is {@code null}
     */
    public static <T> Pair<Collection<T>, Collection<T>> bucket2(
            Collection<T> col, FilterFunction<T> partitionFx1,
            FilterFunction<T> partitionFx2) {
        requireNonNull(partitionFx1);
        requireNonNull(partitionFx2);
        if (col == null || col.isEmpty())
            return null;

        // initialize with maximal size
        Collection<T> partition1 = sizedArrayList(col.size());
        Collection<T> partition2 = sizedArrayList(col.size());
        for (T t : col) {
            if (partitionFx1.match(t))
                partition1.add(t);
            else if (partitionFx2.match(t))
                partition2.add(t);
        }
        return new Pair<Collection<T>, Collection<T>>(partition1, partition2);
    }

    /**
     * Buckets a {@link Collection} of {@code T} using three
     * {@link FilterFunction}.  The first partition contains {@code T} that
     * matched the first {@link FilterFunction}, the second partition
     * contains {@code T} that matched the second {@link FilterFunction}, and
     * the third partition contains {@code T} that matched the third
     * {@link FilterFunction}.  Any {@code T} that do not match either
     * {@link FilterFunction} are excluded from all partitions.
     *
     * @param col {@link Collection} of type {@code T}
     * @param partitionFx1 {@link FilterFunction} of type {@code T}; may not be
     * {@code null}
     * @param partitionFx2 {@link FilterFunction} of type {@code T}; may not be
     * {@code null}
     * @param partitionFx3 {@link FilterFunction} of type {@code T}; may not be
     * {@code null}
     * @return {@link Triple} of bucketed {@link Collection} of type {@code T},
     * {@code null} if {@code col} was {@code null} or empty
     * @throws NullPointerException when {@code partitionFx1},
     * {@code partitionFx2}, or {@code partitionFx3} is {@code null}
     */
    public static <T> Triple<Collection<T>, Collection<T>, Collection<T>> bucket3(
            Collection<T> col, FilterFunction<T> partitionFx1,
            FilterFunction<T> partitionFx2, FilterFunction<T> partitionFx3) {
        requireNonNull(partitionFx1);
        requireNonNull(partitionFx2);
        requireNonNull(partitionFx3);
        if (col == null || col.isEmpty())
            return null;

        // initialize with maximal size
        Collection<T> partition1 = sizedArrayList(col.size());
        Collection<T> partition2 = sizedArrayList(col.size());
        Collection<T> partition3 = sizedArrayList(col.size());
        for (T t : col) {
            if (partitionFx1.match(t))
                partition1.add(t);
            else if (partitionFx2.match(t))
                partition2.add(t);
            else if (partitionFx3.match(t))
                partition3.add(t);
        }
        return new Triple<Collection<T>, Collection<T>, Collection<T>>(
                partition1, partition2, partition3);
    }

    /**
     * Returns an {@link Iterator} that iterates objects of type {@code <U>}
     * that are mapped from lines in a {@link File}.  A {@link MapFunction} is
     * used to map the {@link String} line to an object of type {@code <U>}.
     * <p/>
     * <p>
     * The returned {@link Iterator} is expected to be closed explicitly by the
     * caller. The iterator implements {@link Closeable} which allows usage of
     * {@code try-with-resources}.
     * </p>
     *
     * @param f {@link File}; may not be {@code null}
     * @param fx {@link MapFunction}; may not be {@code null}
     * @return {@link FileIterator} iterating objects of type {@code <U>}
     * @throws NullPointerException when {@code f} or {@code fx} is
     * {@code null}
     */
    public static <U> FileIterator<U> fileIterator(final File f,
                                                   final MapFunction<String, U> fx) throws IOException {
        requireNonNull(f, "f is null");
        requireNonNull(fx, "fx is null");
        if (!readable(f)) {
            throw new IllegalArgumentException("f is unreadable");
        }
        return new FileIterator<U>(f, fx);
    }

    /**
     * Returns a {@link FileIterable} that can iterate objects of type
     * {@code <U>} that are mapped from lines in a {@link File}.  A
     * {@link MapFunction} is used to map the {@link String} line to an object
     * of type {@code <U>}.
     * <p/>
     * <p>
     * The returned {@link Iterable} is expected to be closed explicitly by the
     * caller.  The iterator implements {@link Closeable} which allows usage of
     * {@code try-with-resources}.
     * </p>
     *
     * @param f {@link File}; may not be {@code null}
     * @param fx {@link MapFunction}; may not be {@code null}
     * @return {@link FileIterable} can iterate objects of type {@code <U>}
     * @throws NullPointerException when {@code f} or {@code fx} is
     * {@code null}
     */
    public static <U> FileIterable<U> fileIterable(final File f,
                                                   final MapFunction<String, U> fx) {
        requireNonNull(f, "f is null");
        requireNonNull(fx, "fx is null");
        if (!readable(f)) {
            throw new IllegalArgumentException("f is unreadable");
        }
        return new FileIterable<U>(f, fx);
    }

    /**
     * Requires a {@link String key} to exist in a {@link Map map} and be of a
     * certain {@link Class type}.  If either of these conditions are not met
     * a {@link RuntimeException} is thrown.
     *
     * <p>
     * The typed value is casted safely if it can be assigned from
     * {@link Class cls}.
     *
     * @param key {@link String}; may be {@code null} implying enforce
     * {@link Map} contains check but not value type
     * @param map {@link Map}; may not be {@code null}
     * @param valueClass {@link Class} {@code <T>}; may not be {@code null}
     * @return safely-casted type {@code <T>}
     * @throws NullPointerException when {@code map} or {@code valueClass} is
     * {@code null}
     * @throws RuntimeException when {@code key} is missing in {@code map} or
     * the key's value is assignable from {@code valueClass}
     */
    public static <T> T requireKey(String key, Map<?, ?> map,
            Class<T> valueClass) {
        requireNonNull(map);
        if (!map.containsKey(key)) {
            String msg = "key '%s': missing, expected type %s";
            throw new RuntimeException(format(msg, key, valueClass.getName()));
        }
        Object o = map.get(key);
        if (o != null && !valueClass.isAssignableFrom(o.getClass())) {
            String msg = "key '%s': type %s, expected %s";
            throw new RuntimeException(format(msg, key, o.getClass()
                    .getName(), valueClass.getName()));
        }
        return valueClass.cast(o);
    }

    private static final long[] createLookupTable() {
        long[] byteTable = new long[256];
        long h = 0x544B2FBACAAF1684L;
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 31; j++) {
                h = (h >>> 7) ^ h;
                h = (h << 11) ^ h;
                h = (h >>> 10) ^ h;
            }
            byteTable[i] = h;
        }
        return byteTable;
    }

    /**
     * FileIterator iterates a {@link File} converting each {@link String} line
     * to type {@code U}.  This iterator implements {@link Closeable} so it is
     * intended to be used with {@code try-with-resources}.
     *
     * @param <U> type {@code U} to convert file line to
     */
    public static final class FileIterator<U> implements Iterator<U>,
            Closeable {

        private final BufferedReader r;
        private final MapFunction<String, U> fx;
        private String line;

        private FileIterator(File f, MapFunction<String, U> fx) {
            requireNonNull(f, "f is null");
            requireNonNull(fx, "fx is null");
            if (!readable(f)) {
                throw new IllegalArgumentException("f is unreadable");
            }

            try {
                r = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                // readable check above mostly guarentees the file exists
                // and is readable, throw unchecked anyway
                throw new IllegalStateException(e);
            }

            this.fx = fx;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            try {
                line = r.readLine();
            } catch (IOException e) {
                return false;
            }

            return line != null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public U next() {
            return fx.map(line);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "Cannot remove line from file.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws IOException {
            if (r != null) {
                r.close();
            }
        }
    }

    /**
     * FileIterable provides a {@link FileIterator} that can iterable objects
     * of type {@code <U>}.
     *
     * @param <U> type {@code U} to convert file line to
     */
    public static final class FileIterable<U> implements Iterable<U> {

        private final File f;
        private final MapFunction<String, U> fx;

        private FileIterable(final File f, MapFunction<String, U> fx) {
            requireNonNull(f, "f is null");
            if (!readable(f)) {
                throw new IllegalArgumentException("f is not readable");
            }
            this.f = f;
            this.fx = fx;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Iterator<U> iterator() {
            return new FileIterator<U>(f, fx);
        }
    }
}
