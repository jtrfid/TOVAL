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

/**
 * A collection of helpful methods for finding subclasses, superclasses, and
 * implemented interfaces for given classes and interfaces.
 * 
 * @version 1.0
 * @author Adrian Lange
 */
public class ReflectionUtils {

	/** Suffix for array class names: "[]" */
	public static final String ARRAY_SUFFIX = "[]";

	/** Separator for the class name suffix: "." */
	public static final String CLASS_SUFFIX_SEPARATOR = ".";

	/** The ".class" file suffix */
	public static final String CLASS_FILE_SUFFIX = CLASS_SUFFIX_SEPARATOR + "class";

	/** Name pattern for package name elements: "[a-z0-9]*" */
	public static final String PACKAGE_NAME_PATTERN = "[a-z0-9]*";

	/** Separator for package name elements: "." */
	public static final String PACKAGE_SEPARATOR = ".";

	/** Separator for package name elements in path form: "/" */
	public static final String PACKAGE_PATH_SEPARATOR = "/";

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
	 * @param interfaze
	 *            Interface which should be implemented.
	 * @param packageName
	 *            Package to search for classes.
	 * @return {@link Set} of {@link Class} objects implementing the given
	 *         interface in the specified package.
	 */
	public static Set<Class<?>> getInterfaceImplementations(Class<?> interfaze, String packageName) {
		Validate.notNull(interfaze);
		Validate.notNull(packageName);
		Validate.notEmpty(packageName);

		if (!interfaze.isInterface()) {
			throw new ParameterException("Parameter is not an interface");
		}

		String packagePath = PACKAGE_PATH_SEPARATOR + packageName.replaceAll("[" + PACKAGE_SEPARATOR + "]", PACKAGE_PATH_SEPARATOR);
		URL packageURL = interfaze.getResource(packagePath);

		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(packageURL.openStream()));
			String line = null;

			while ((line = in.readLine()) != null) {
				if (line.endsWith(CLASS_FILE_SUFFIX)) { // class, enum, or
														// interface
					try {
						Class<?> currentClass = Class.forName(packageName + PACKAGE_SEPARATOR + line.substring(0, line.lastIndexOf(CLASS_SUFFIX_SEPARATOR)));

						if (getInterfaces(currentClass).contains(interfaze) && interfaze != currentClass) {
							classes.add(currentClass);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace(); // shouldn't be reached
					}
				} else if (line.matches(PACKAGE_NAME_PATTERN)) { // package
					// recursive call to add classes in the package
					classes.addAll(getInterfaceImplementations(interfaze, packageName + PACKAGE_SEPARATOR + line));
				}
			}
		} catch (IOException e) {
			e.printStackTrace(); // shouldn't happen
		}

		return classes;
	}

	/**
	 * The same method as
	 * {@link ReflectionUtils#getInterfaceImplementations(Class, String)}, but
	 * with the possibility to search in multiple packages. Since the result is
	 * returned as a {@link Set}, there won't be any duplicates.
	 */
	public static Set<Class<?>> getInterfaceImplementations(Class<?> interfaze, List<String> packageNames) {
		Validate.notNull(interfaze);
		Validate.notNull(packageNames);

		if (!interfaze.isInterface()) {
			throw new ParameterException("Parameter is not an interface");
		}

		Set<Class<?>> classes = new HashSet<Class<?>>();

		for (String packageName : packageNames) {
			classes.addAll(getInterfaceImplementations(interfaze, packageName));
		}

		return classes;
	}

	/**
	 * <p>
	 * Returns a {@link Set} of {@link Class} objects containing all classes of
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
	 * // class
	 * // de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.abstr.AbstractIFNetPlace
	 * // class de.uni.freiburg.iig.telematik.sepia.petrinet.ifnet.IFNetPlace
	 * // class de.uni.freiburg.iig.telematik.sepia.petrinet.pt.abstr.AbstractPTPlace
	 * // class de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTPlace
	 * </pre>
	 * 
	 * @param clazz
	 *            Class which should be extended.
	 * @param packageName
	 *            Package to search for subclasses.
	 * @return {@link Set} of {@link Class} objects extending the given class in
	 *         the specified package.
	 */
	public static Set<Class<?>> getSubclasses(Class<?> clazz, String packageName) {
		Validate.notNull(clazz);
		Validate.notNull(packageName);
		Validate.notEmpty(packageName);

		if (clazz.isInterface() || clazz.isEnum()) {
			throw new ParameterException("Parameter is not a class");
		}

		String packagePath = PACKAGE_PATH_SEPARATOR + packageName.replaceAll("[" + PACKAGE_SEPARATOR + "]", PACKAGE_PATH_SEPARATOR);
		URL packageURL = clazz.getResource(packagePath);

		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(packageURL.openStream()));
			String line = null;

			while ((line = in.readLine()) != null) {
				if (line.endsWith(CLASS_FILE_SUFFIX)) { // class, enum, or
														// interface
					try {
						Class<?> currentClass = Class.forName(packageName + PACKAGE_SEPARATOR + line.substring(0, line.lastIndexOf(CLASS_SUFFIX_SEPARATOR)));

						if (getSuperclasses(currentClass).contains(clazz) && clazz != currentClass) {
							classes.add(currentClass);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace(); // shouldn't be reached
					}
				} else if (line.matches(PACKAGE_NAME_PATTERN)) { // package
					// recursive call to add classes in the package
					classes.addAll(getSubclasses(clazz, packageName + PACKAGE_SEPARATOR + line));
				}
			}
		} catch (IOException e) {
			e.printStackTrace(); // shouldn't happen
		}

		return classes;
	}

	/**
	 * The same method as {@link ReflectionUtils#getSubclasses(Class, String)},
	 * but with the possibility to search in multiple packages. Since the result
	 * is returned as a {@link Set}, there won't be any duplicates.
	 */
	public static Set<Class<?>> getSubclasses(Class<?> clazz, List<String> packageNames) {
		Validate.notNull(clazz);
		Validate.notNull(packageNames);

		if (clazz.isInterface() || clazz.isEnum()) {
			throw new ParameterException("Parameter is not a class");
		}

		Set<Class<?>> classes = new HashSet<Class<?>>();

		for (String packageName : packageNames) {
			classes.addAll(getSubclasses(clazz, packageName));
		}

		return classes;
	}

	/**
	 * Returns all superclasses of the given class ordered top down. The last
	 * element is always {@link java.lang.Object}.
	 */
	public static List<Class<?>> getSuperclasses(Class<?> clazz) {
		Validate.notNull(clazz);

		List<Class<?>> clazzes = new ArrayList<Class<?>>();

		if (clazz.getSuperclass() != null) {
			clazzes.add(clazz.getSuperclass());
			clazzes.addAll(getSuperclasses(clazz.getSuperclass()));
		}

		return clazzes;
	}

	/**
	 * Returns all implemented interfaces of the given class.
	 */
	public static Set<Class<?>> getInterfaces(Class<?> clazz) {
		Validate.notNull(clazz);

		Set<Class<?>> interfaces = new HashSet<Class<?>>();
		interfaces.addAll(Arrays.asList(clazz.getInterfaces()));

		List<Class<?>> superclasses = getSuperclasses(clazz);
		for (Class<?> superclass : superclasses) {
			interfaces.addAll(Arrays.asList(superclass.getInterfaces()));
		}

		return interfaces;
	}
}
