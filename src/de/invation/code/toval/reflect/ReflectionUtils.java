package de.invation.code.toval.reflect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
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
     * Pattern to parse a JAR path like
     * <code>jar:file:/path/to/file.jar!/de/invation/code/toval</code>.
     */
    public static final Pattern JAR_PATH_PATTERN = Pattern.compile("^jar:file:([^!]*)!(.*)$", Pattern.CASE_INSENSITIVE);

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
     * Returns a {@link Set} of all classes in a specified package. Since the
     * result is returned as a {@link Set}, there won't be any duplicates.
     *
     * @param packageName
     * @param recursive
     * @return
     * @throws ReflectionException
     */
    public static Set<Class<?>> getClassesInPackage(String packageName, boolean recursive) throws ReflectionException {
        Validate.notNull(packageName);
        Validate.notEmpty(packageName);

        try {
            String packagePath = packageName.replace(PACKAGE_SEPARATOR, PACKAGE_PATH_SEPARATOR);
            URL packageURL = Thread.currentThread().getClass().getResource("/" + packagePath);
            InputStream is = packageURL.openStream();

            Set<Class<?>> classes = new HashSet<>();
            try {
                if (Pattern.matches(JAR_PATH_PATTERN.pattern(), packageURL.toString())) {
                    Matcher jarPathMatcher = JAR_PATH_PATTERN.matcher(packageURL.toString());
                    JarFile jarFile = null;
                    String innerPath = null;
                    while(jarPathMatcher.find()) {
                        jarFile = new JarFile(jarPathMatcher.group(1));
                        innerPath = jarPathMatcher.group(2);
                    }
                    if (jarFile == null || innerPath == null) {
                        throw new ReflectionException("Couldn't match JAR path pattern on \"" + packageURL.toString() + "\".");
                    }

                    Enumeration<JarEntry> e = jarFile.entries();
                    URL[] packageURLs = {packageURL};
                    URLClassLoader cl = URLClassLoader.newInstance(packageURLs);
                    while (e.hasMoreElements()) {
                        JarEntry je = e.nextElement();
                        if (je.isDirectory() || !je.getName().endsWith(CLASS_FILE_SUFFIX)) {
                            continue;
                        }
                        // -6 because of .class
                        String className = je.getName().substring(0, je.getName().length() - 6);
                        className = className.replace('/', '.');
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
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
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
     * as a {@link Set}, there won't be any duplicates.
     *
     * @param packageNames
     * @param recursive
     * @return
     * @throws ReflectionException
     */
    public static Set<Class<?>> getClassesInPackages(List<String> packageNames, boolean recursive) throws ReflectionException {
        Validate.notNull(packageNames);

        Set<Class<?>> classes = new HashSet<>();
        for (String packageName : packageNames) {
            classes.addAll(getClassesInPackage(packageName, recursive));
        }
        return classes;
    }

    /**
     * <p>
     * Returns a {@link List} of {@link Class} objects containing all classes of
     * a specified package (including subpackages) which extend the given class.
     * </p>
     * <p>
     * Example:
     * </p>
     *
     * <pre>
     * String pack = &quot;de.uni.freiburg.iig.telematik.sepia&quot;;
     * Class&lt;?&gt; superclass = AbstractPlace.class;
     * List&lt;Class&lt;?&gt;&gt; classes = ReflectionUtils.getSubclasses(superclass, pack);
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
     * @param recursive
     * @return {@link List} of {@link Class} objects extending the given class
     * in the specified package.
     * @throws ReflectionException
     */
    public static Set<Class<?>> getSubclassesInPackage(Class<?> clazz, String packageName, boolean recursive) throws ReflectionException {
        Validate.notNull(clazz);
        if (clazz.isInterface() || clazz.isEnum()) {
            throw new ParameterException("Parameter is not a class");
        }

        Set<Class<?>> classesInPackage = getClassesInPackage(packageName, recursive);

        try {
            Set<Class<?>> subClassesInPackage = new HashSet<>();
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
     * Returns a {@link Set} of all subpackages of a specified package. Since
     * the result is returned as a {@link Set}, there won't be any duplicates.
     *
     * @param packageName
     * @param recursive
     * @return
     * @throws ReflectionException
     */
    public static Set<String> getSubpackages(String packageName, boolean recursive) throws ReflectionException {
        Validate.notNull(packageName);
        Validate.notEmpty(packageName);

        try {
            String packagePath = packageName.replace(PACKAGE_SEPARATOR, PACKAGE_PATH_SEPARATOR);
            URL packageURL = Thread.currentThread().getContextClassLoader().getResource(packagePath); // TODO

            Set<String> subpackages = new HashSet<>();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(packageURL.openStream()));
                String line;

                while ((line = in.readLine()) != null) {
                    if (line.matches(PACKAGE_NAME_PATTERN)) { // package
                        // recursive call to add classes in the package
                        subpackages.add(packageName + PACKAGE_SEPARATOR + line);
                        if (recursive) {
                            subpackages.addAll(getSubpackages(packageName + PACKAGE_SEPARATOR + line, recursive));
                        }
                    }
                }
            } catch (IOException e) {
                throw new ReflectionException("Cannot access package directory", e);
            }
            return subpackages;
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    /**
     * <p>
     * Returns a {@link Set} of {@link Class} objects containing all classes of
     * a specified package (including subpackages) which implement the given
     * interface.
     * </p>
     * <p>
     * Example:
     * </p>
     *
     * <pre>
     * String pack = &quot;de.uni.freiburg.iig.telematik.sepia&quot;;
     * Class&lt;?&gt; interf = PNParserInterface.class;
     * Set&lt;Class&lt;?&gt;&gt; classes = ReflectionUtils.getInterfaceImplementations(interf, pack);
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
     * @param recursive
     * @return {@link Set} of {@link Class} objects implementing the given
     * interface in the specified package.
     * @throws ReflectionException
     */
    public static Set<Class<?>> getInterfaceImplementationsInPackage(Class<?> interfaze, String packageName, boolean recursive) throws ReflectionException {
        Validate.notNull(interfaze);
        if (!interfaze.isInterface()) {
            throw new ParameterException("Parameter is not an interface");
        }

        Set<Class<?>> classesInPackage = getClassesInPackage(packageName, recursive);
        try {
            Set<Class<?>> interfaceImplementationsInPackage = new HashSet<>();
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
     * is returned as a {@link Set}, there won't be any duplicates.
     *
     * @param interfaze
     * @param packageNames
     * @param recursive
     * @return
     * @throws ReflectionException
     */
    public static Set<Class<?>> getInterfaceImplementationsInPackages(Class<?> interfaze, List<String> packageNames, boolean recursive) throws ReflectionException {
        Validate.notNull(interfaze);
        Validate.notNull(packageNames);

        if (!interfaze.isInterface()) {
            throw new ParameterException("Parameter is not an interface");
        }

        Set<Class<?>> classes = new HashSet<>();

        for (String packageName : packageNames) {
            classes.addAll(getInterfaceImplementationsInPackage(interfaze, packageName, recursive));
        }
        return classes;
    }

    /**
     * Returns all superclasses of the given class ordered top down. The last
     * element is always {@link java.lang.Object}.
     *
     * @param clazz
     * @return
     * @throws ReflectionException
     */
    public static List<Class<?>> getSuperclasses(Class<?> clazz) throws ReflectionException {
        Validate.notNull(clazz);

        try {
            List<Class<?>> clazzes = new ArrayList<>();
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
     * Returns all implemented interfaces of the given class.
     *
     * @param clazz
     * @return
     * @throws ReflectionException
     */
    public static Set<Class<?>> getInterfaces(Class<?> clazz) throws ReflectionException {
        Validate.notNull(clazz);

        try {
            Set<Class<?>> interfaces = new HashSet<>();
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            List<Class<?>> superclasses = getSuperclasses(clazz);
            for (Class<?> superclass : superclasses) {
                interfaces.addAll(Arrays.asList(superclass.getInterfaces()));
            }
            return interfaces;
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }
}
