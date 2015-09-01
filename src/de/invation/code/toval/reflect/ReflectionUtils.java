package de.invation.code.toval.reflect;

import de.invation.code.toval.file.ExtendedJarFile;
import de.invation.code.toval.misc.Filterable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of helpful methods for finding subclasses, superclasses, and
 * implemented interfaces for given classes and interfaces.
 *
 * @version 1.0
 * @author Adrian Lange
 */
public class ReflectionUtils {

    /**
     * Suffix for array class names: "[]"
     */
    public static final String ARRAY_SUFFIX = "[]";

    /**
     * Separator for the class name suffix: "."
     */
    public static final String CLASS_SUFFIX_SEPARATOR = ".";

    /**
     * The ".class" file suffix
     */
    public static final String CLASS_FILE_SUFFIX = CLASS_SUFFIX_SEPARATOR + "class";

    /**
     * <p>
     * Pattern that matches paths of classes and packages like:</p>
     * <ul>
     * <li>SomeClass.class</li>
     * <li>some/package/OtherClass$SubClass$1.class</li>
     * <li>some/package/</li>
     * </ul>
     */
    public static final Pattern CLASS_PATH_PATTERN = Pattern.compile("^((?:[a-z][a-z0-9-]*\\/)+)?([\\d\\w_\\-]+(?:\\$[\\d\\w_\\-]+)*\\.class)?$");

    /**
     * Pattern to parse a JAR path like
     * <code>jar:file:/path/to/file.jar!/de/invation/code/toval</code>.
     */
    public static final Pattern JAR_PATH_PATTERN = Pattern.compile("^jar:file:([^!]*)!(\\S*)$", Pattern.CASE_INSENSITIVE);

    /**
     * Name pattern for package name elements: "[a-z0-9]*"
     */
    public static final String PACKAGE_NAME_PATTERN = "[a-z0-9]*";

    /**
     * Separator for package name elements: "."
     */
    public static final String PACKAGE_SEPARATOR = ".";

    /**
     * Separator for package name elements in path form: "/"
     */
    public static final String PACKAGE_PATH_SEPARATOR = "/";

    /**
     * Returns a {@link LinkedHashSet} of all classes in a specified package.
     * Since the result is returned as a {@link LinkedHashSet}, there won't be
     * any duplicates.
     *
     * @param packageName Package name.
     * @param recursive <code>true</code> if subpackages should be considered,
     * too.
     * @return {@link LinkedHashSet} of classes in a specified package.
     * @throws ReflectionException
     */
    public static LinkedHashSet<Class<?>> getClassesInPackage(String packageName, boolean recursive) throws ReflectionException {
        Validate.notNull(packageName);
        Validate.notEmpty(packageName);

        try {
            String packagePath = packageName.replace(PACKAGE_SEPARATOR, PACKAGE_PATH_SEPARATOR);
            URL packageURL = Thread.currentThread().getClass().getResource(PACKAGE_PATH_SEPARATOR + packagePath);
            InputStream is = packageURL.openStream();

            LinkedHashSet<Class<?>> classes = new LinkedHashSet<>();
            try {
                if (Pattern.matches(JAR_PATH_PATTERN.pattern(), packageURL.toString())) {
                    Matcher jarPathMatcher = JAR_PATH_PATTERN.matcher(packageURL.toString());
                    ExtendedJarFile jarFile = null;
                    String innerPath = null;
                    while (jarPathMatcher.find()) {
                        jarFile = new ExtendedJarFile(jarPathMatcher.group(1));
                        innerPath = jarPathMatcher.group(2);
                    }
                    if (jarFile == null || innerPath == null) {
                        throw new ReflectionException("Couldn't match JAR path pattern on \"" + packageURL.toString() + "\".");
                    }

                    jarFile.addFilter(new ClassFilter(packagePath, recursive));
                    Enumeration<JarEntry> e = jarFile.entries();
                    URL[] packageURLs = {packageURL};
                    URLClassLoader cl = URLClassLoader.newInstance(packageURLs);
                    while (e.hasMoreElements()) {
                        JarEntry je = e.nextElement();
                        if (je.isDirectory() || !je.getName().endsWith(CLASS_FILE_SUFFIX)) {
                            continue;
                        }
                        // -6 because of .class
                        String className = je.getName().substring(0, je.getName().length() - CLASS_FILE_SUFFIX.length());
                        className = className.replace(PACKAGE_PATH_SEPARATOR, PACKAGE_SEPARATOR);
                        Class<?> c = cl.loadClass(className);
                        classes.add(c);
                    }
                } else {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(isr);
                    String line;

                    while ((line = in.readLine()) != null) {
                        if (line.endsWith(CLASS_FILE_SUFFIX)) { // class, enum, or
                            // interface
                            String className = line.substring(0, line.lastIndexOf(CLASS_SUFFIX_SEPARATOR));
                            try {
                                Class<?> currentClass = Class.forName(packageName + PACKAGE_SEPARATOR + className);
                                classes.add(currentClass);
                            } catch (ClassNotFoundException e) {
                                throw new ReflectionException("Cannot locate class \"" + className + "\"", e);
                            }
                        } else if (line.matches(PACKAGE_NAME_PATTERN) && recursive) { // package
                            // recursive call to add classes in the package
                            classes.addAll(getClassesInPackage(packageName + PACKAGE_SEPARATOR + line, recursive));
                        }
                    }
                }
            } catch (IOException e) {
                throw new ReflectionException("Cannot access package directory", e);
            } catch (ClassNotFoundException ex) {
                throw new ReflectionException("Error in class loader: ", ex);
            }
            return classes;
        } catch (IOException | ReflectionException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * The same method as
     * {@link #getSubclassesInPackage(Class, String, boolean)}, but with the
     * possibility to search in multiple packages. Since the result is returned
     * as a {@link LinkedHashSet}, there won't be any duplicates.
     *
     * @param packageNames Set of package names.
     * @param recursive <code>true</code> if subpackages should be considered,
     * too.
     * @return {@link LinkedHashSet} of classes in specified packages.
     * @throws ReflectionException
     */
    public static LinkedHashSet<Class<?>> getClassesInPackages(Set<String> packageNames, boolean recursive) throws ReflectionException {
        Validate.notNull(packageNames);

        LinkedHashSet<Class<?>> classes = new LinkedHashSet<>();
        for (String packageName : packageNames) {
            classes.addAll(getClassesInPackage(packageName, recursive));
        }
        return classes;
    }

    /**
     * <p>
     * Returns a {@link LinkedHashSet} of {@link Class} objects containing all
     * classes of a specified package (including subpackages) which implement
     * the given interface.
     * </p>
     * <p>
     * Example:
     * </p>
     *
     * <pre>
     * String pack = &quot;de.uni.freiburg.iig.telematik.sepia&quot;;
     * Class&lt;?&gt; interf = PNParserInterface.class;
     * LinkedHashSet&lt;Class&lt;?&gt;&gt; classes = ReflectionUtils.getInterfaceImplementations(interf, pack);
     * for (Class&lt;?&gt; c : classes) {
     * 	System.out.println(c);
     * }
     *
     * // class de.uni.freiburg.iig.telematik.sepia.parser.petrify.PetrifyParser
     * // class de.uni.freiburg.iig.telematik.sepia.parser.pnml.PNMLParser
     * </pre>
     *
     * @param interfaze Interface which should be implemented.
     * @param packageName Package to search for classes.
     * @param recursive <code>true</code> if subpackages should be considered,
     * too.
     * @return {@link LinkedHashSet} of {@link Class} objects implementing the
     * given interface in the specified package.
     * @throws ReflectionException
     */
    public static LinkedHashSet<Class<?>> getInterfaceImplementationsInPackage(Class<?> interfaze, String packageName, boolean recursive) throws ReflectionException {
        Validate.notNull(interfaze);
        if (!interfaze.isInterface()) {
            throw new ParameterException("Parameter is not an interface");
        }

        LinkedHashSet<Class<?>> classesInPackage = getClassesInPackage(packageName, recursive);
        try {
            LinkedHashSet<Class<?>> interfaceImplementationsInPackage = new LinkedHashSet<>();
            for (Class<?> classInPackage : classesInPackage) {
                if (getInterfaces(classInPackage).contains(interfaze)) {
                    interfaceImplementationsInPackage.add(classInPackage);
                }
            }
            return interfaceImplementationsInPackage;
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * The same method as
     * {@link ReflectionUtils#getInterfaceImplementationsInPackage(Class, String, boolean)},
     * but with the possibility to search in multiple packages. Since the result
     * is returned as a {@link LinkedHashSet}, there won't be any duplicates.
     *
     * @param interfaze Interface class.
     * @param packageNames Set of package names.
     * @param recursive <code>true</code> if subpackages should be considered,
     * too.
     * @return {@link LinkedHashSet} of implementations of a given interface.
     * @throws ReflectionException
     */
    public static LinkedHashSet<Class<?>> getInterfaceImplementationsInPackages(Class<?> interfaze, Set<String> packageNames, boolean recursive) throws ReflectionException {
        Validate.notNull(interfaze);
        Validate.notNull(packageNames);

        if (!interfaze.isInterface()) {
            throw new ParameterException("Parameter is not an interface");
        }

        LinkedHashSet<Class<?>> classes = new LinkedHashSet<>();

        for (String packageName : packageNames) {
            classes.addAll(getInterfaceImplementationsInPackage(interfaze, packageName, recursive));
        }
        return classes;
    }

    /**
     * Returns all implemented interfaces of the given class.
     *
     * @param clazz Class to read interfaces from.
     * @return {@link LinkedHashSet} of interfaces.
     * @throws ReflectionException If interfaces can't be read.
     */
    public static LinkedHashSet<Class<?>> getInterfaces(Class<?> clazz) throws ReflectionException {
        Validate.notNull(clazz);

        try {
            LinkedHashSet<Class<?>> interfaces = new LinkedHashSet<>();
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            LinkedHashSet<Class<?>> superclasses = getSuperclasses(clazz);
            for (Class<?> superclass : superclasses) {
                interfaces.addAll(Arrays.asList(superclass.getInterfaces()));
            }
            return interfaces;
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * <p>
     * Returns a {@link LinkedHashSet} of {@link Class} objects containing all
     * classes of a specified package (including subpackages) which extend the
     * given class.
     * </p>
     * <p>
     * Example:
     * </p>
     *
     * <pre>
     * String pack = &quot;de.uni.freiburg.iig.telematik.sepia&quot;;
     * Class&lt;?&gt; superclass = AbstractPlace.class;
     * LinkedHashSet&lt;Class&lt;?&gt;&gt; classes = ReflectionUtils.getSubclasses(superclass, pack);
     * for (Class&lt;?&gt; c : classes) {
     * 	System.out.println(c);
     * }
     *
     * // class de.uni.freiburg.iig.telematik.sepia.petrinet.cpn.abstr.AbstractCPNPlace
     * // class de.uni.freiburg.iig.telematik.sepia.petrinet.cpn.CPNPlace
     * // class de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.abstr.AbstractIFNetPlace
     * // class de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.IFNetPlace
     * // class de.uni.freiburg.iig.telematik.sepia.petrinet.pt.abstr.AbstractPTPlace
     * // class de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace
     * </pre>
     *
     * @param clazz Class which should be extended.
     * @param packageName Package to search for subclasses.
     * @param recursive <code>true</code> if subpackages should be considered,
     * too.
     * @return {@link LinkedHashSet} of {@link Class} objects extending the
     * given class in the specified package.
     * @throws ReflectionException
     */
    public static LinkedHashSet<Class<?>> getSubclassesInPackage(Class<?> clazz, String packageName, boolean recursive) throws ReflectionException {
        Validate.notNull(clazz);
        if (clazz.isInterface() || clazz.isEnum()) {
            throw new ParameterException("Parameter is not a class");
        }

        LinkedHashSet<Class<?>> classesInPackage = getClassesInPackage(packageName, recursive);

        try {
            LinkedHashSet<Class<?>> subClassesInPackage = new LinkedHashSet<>();
            for (Class<?> classInPackage : classesInPackage) {
                if (getSuperclasses(classInPackage).contains(clazz) && clazz != classInPackage) {
                    subClassesInPackage.add(classInPackage);
                }
            }
            return subClassesInPackage;
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Returns a {@link LinkedHashSet} of all subpackages of a specified
     * package. Since the result is returned as a {@link LinkedHashSet}, there
     * won't be any duplicates.
     *
     * @param packageName Package name.
     * @param recursive <code>true</code> if subpackages of subpackages should
     * be considered, too.
     * @return {@link LinkedHashSet} of package names in a specified package.
     * @throws ReflectionException
     */
    public static LinkedHashSet<String> getSubpackages(String packageName, boolean recursive) throws ReflectionException {
        Validate.notNull(packageName);
        Validate.notEmpty(packageName);

        try {
            String packagePath = packageName.replace(PACKAGE_SEPARATOR, PACKAGE_PATH_SEPARATOR);
            URL packageURL = Thread.currentThread().getClass().getResource(PACKAGE_PATH_SEPARATOR + packagePath);
            InputStream is = packageURL.openStream();

            LinkedHashSet<String> packageNames = new LinkedHashSet<>();
            try {
                if (Pattern.matches(JAR_PATH_PATTERN.pattern(), packageURL.toString())) {
                    Matcher jarPathMatcher = JAR_PATH_PATTERN.matcher(packageURL.toString());
                    ExtendedJarFile jarFile = null;
                    String innerPath = null;
                    while (jarPathMatcher.find()) {
                        jarFile = new ExtendedJarFile(jarPathMatcher.group(1));
                        innerPath = jarPathMatcher.group(2);
                    }
                    if (jarFile == null || innerPath == null) {
                        throw new ReflectionException("Couldn't match JAR path pattern on \"" + packageURL.toString() + "\".");
                    }

                    jarFile.addFilter(new PackageFilter(packagePath, recursive));
                    Enumeration<JarEntry> e = jarFile.entries();
                    while (e.hasMoreElements()) {
                        JarEntry je = e.nextElement();
                        if (je.isDirectory() || !je.getName().endsWith(CLASS_FILE_SUFFIX)) {
                            packageNames.add(je.getName().replace(PACKAGE_PATH_SEPARATOR, PACKAGE_SEPARATOR).substring(0, je.getName().length() - 1));
                        }

                    }
                } else {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(isr);
                    String line;

                    while ((line = in.readLine()) != null) {
                        if (line.matches(PACKAGE_NAME_PATTERN)) {
                            packageNames.add(packageName + PACKAGE_SEPARATOR + line);
                            if (recursive) {
                                // recursive call to add subclasses in the package
                                packageNames.addAll(getSubpackages(packageName + PACKAGE_SEPARATOR + line, recursive));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new ReflectionException("Cannot access package directory", e);
            }
            return packageNames;
        } catch (IOException | ReflectionException e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Returns all superclasses of the given class ordered top down. The last
     * element is always {@link java.lang.Object}.
     *
     * @param clazz Class to read superclasses from.
     * @return {@link LinkedHashSet} of superclasses.
     * @throws ReflectionException If superclass can't be read.
     */
    public static LinkedHashSet<Class<?>> getSuperclasses(Class<?> clazz) throws ReflectionException {
        Validate.notNull(clazz);

        try {
            LinkedHashSet<Class<?>> clazzes = new LinkedHashSet<>();
            if (clazz.getSuperclass() != null) {
                clazzes.add(clazz.getSuperclass());
                clazzes.addAll(getSuperclasses(clazz.getSuperclass()));
            }
            return clazzes;
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * Accepts only classes.
     */
    public static class ClassFilter implements Filterable<JarEntry> {

        public final String packageName;
        public final boolean recursive;

        /**
         * Creates a new instance of the filter.
         *
         * @param packageName Name of the package with "/" as separators and
         * without trailing "/".
         * @param recursive Set <code>true</code> if subpackages should be
         * considered, too.
         */
        public ClassFilter(String packageName, boolean recursive) {
            Validate.notNull(packageName);

            this.packageName = packageName + PACKAGE_PATH_SEPARATOR;
            this.recursive = recursive;
        }

        @Override
        public boolean accept(JarEntry entry) {
            Matcher matcher = CLASS_PATH_PATTERN.matcher(entry.getName());
            while (matcher.find()) {
                String packagePath = matcher.group(1);
                String className = matcher.group(2);
                if (packagePath == null && packageName.length() > 0) {
                    return false;
                } else if (recursive && packagePath != null && !packagePath.startsWith(packageName)) {
                    return false;
                } else if (!recursive && packagePath != null && !packagePath.equals(packageName)) {
                    return false;
                }
                if (className != null && className.length() > 0) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Accepts only packages.
     */
    public static class PackageFilter implements Filterable<JarEntry> {

        public final String packageName;
        public final boolean recursive;

        /**
         * Creates a new instance of the filter.
         *
         * @param packageName Name of the package with "/" as separators and
         * without trailing "/".
         * @param recursive Set <code>true</code> if subpackages should be
         * considered, too.
         */
        public PackageFilter(String packageName, boolean recursive) {
            Validate.notNull(packageName);

            this.packageName = packageName + PACKAGE_PATH_SEPARATOR;
            this.recursive = recursive;
        }

        @Override
        public boolean accept(JarEntry entry) {
            Matcher matcher = CLASS_PATH_PATTERN.matcher(entry.getName());
            while (matcher.find()) {
                String packagePath = matcher.group(1);
                String className = matcher.group(2);
                if (className != null && className.length() > 0) {
                    return false;
                }

                boolean emptyPackage = packageName.equals(PACKAGE_PATH_SEPARATOR) && ((!recursive && packagePath == null) || recursive);
                boolean acceptablePackage = packagePath != null && packagePath.startsWith(packageName) && packagePath.length() > packageName.length() && (recursive || (!recursive && !packagePath.substring(packageName.length(), packagePath.length() - 1).contains(PACKAGE_PATH_SEPARATOR)));
                return emptyPackage || acceptablePackage;
            }
            return false;
        }
    }
}
