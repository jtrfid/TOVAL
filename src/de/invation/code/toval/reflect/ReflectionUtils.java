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
	
	
	public static Set<Class<?>> getClassesInPackage(String packageName, boolean recursive) throws ReflectionException{
		Validate.notNull(packageName);
		Validate.notEmpty(packageName);
		
		try {
			String packagePath = packageName.replace(PACKAGE_SEPARATOR, PACKAGE_PATH_SEPARATOR);
			URL packageURL = Thread.currentThread().getContextClassLoader().getResource(packagePath);

			Set<Class<?>> classes = new HashSet<Class<?>>();
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(packageURL.openStream()));
				String line = null;

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
			} catch (IOException e) {
				throw new ReflectionException("Cannot access package directory", e);
			}
			return classes;
		} catch (Exception e) {
			throw new ReflectionException(e);
		}
	}
	
	/**
	 * The same method as {@link ReflectionUtils#getSubclasses(Class, String)},
	 * but with the possibility to search in multiple packages. Since the result
	 * is returned as a {@link Set}, there won't be any duplicates.
	 * @throws ReflectionException 
	 */
	public static Set<Class<?>> getClassesInPackages(List<String> packageNames, boolean recursive) throws ReflectionException {
		Validate.notNull(packageNames);

		Set<Class<?>> classes = new HashSet<Class<?>>();
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
	 * @return {@link List} of {@link Class} objects extending the given class
	 *         in the specified package.
	 * @throws ReflectionException 
	 */
	public static Set<Class<?>> getSubclassesInPackage(Class<?> clazz, String packageName, boolean recursive) throws ReflectionException {
		Validate.notNull(clazz);
		if (clazz.isInterface() || clazz.isEnum()) {
			throw new ParameterException("Parameter is not a class");
		}
	
		Set<Class<?>> classesInPackage = getClassesInPackage(packageName, recursive);
		
		try{
			Set<Class<?>> subClassesInPackage = new HashSet<Class<?>>();
			for (Class<?> classInPackage : classesInPackage) {
				if (getSuperclasses(classInPackage).contains(clazz) && clazz != classInPackage) {
					subClassesInPackage.add(classInPackage);
				}
			}
			return subClassesInPackage;
		} catch(Exception e){
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
	 * @param interfaze
	 *            Interface which should be implemented.
	 * @param packageName
	 *            Package to search for classes.
	 * @return {@link Set} of {@link Class} objects implementing the given
	 *         interface in the specified package.
	 * @throws ReflectionException 
	 */
	public static Set<Class<?>> getInterfaceImplementationsInPackage(Class<?> interfaze, String packageName, boolean recursive) throws ReflectionException {
		Validate.notNull(interfaze);
		if (!interfaze.isInterface()) {
			throw new ParameterException("Parameter is not an interface");
		}
	
		Set<Class<?>> classesInPackage = getClassesInPackage(packageName, recursive);
		try{
			Set<Class<?>> interfaceImplamantationsInPackage = new HashSet<Class<?>>();
			for (Class<?> classInPackage : classesInPackage) {
				if (getInterfaces(classInPackage).contains(interfaze)) {
					interfaceImplamantationsInPackage.add(classInPackage);
				}
			}
			return interfaceImplamantationsInPackage;
		} catch(Exception e){
			throw new ReflectionException(e);
		}
	}

	/**
	 * The same method as
	 * {@link ReflectionUtils#getInterfaceImplementations(Class, String)}, but
	 * with the possibility to search in multiple packages. Since the result is
	 * returned as a {@link Set}, there won't be any duplicates.
	 * @throws ReflectionException 
	 */
	public static Set<Class<?>> getInterfaceImplementationsInPackages(Class<?> interfaze, List<String> packageNames, boolean recursive) throws ReflectionException {
		Validate.notNull(interfaze);
		Validate.notNull(packageNames);

		if (!interfaze.isInterface()) {
			throw new ParameterException("Parameter is not an interface");
		}

		Set<Class<?>> classes = new HashSet<Class<?>>();

		for (String packageName : packageNames) {
			classes.addAll(getInterfaceImplementationsInPackage(interfaze, packageName, recursive));
		}
		return classes;
	}

	/**
	 * Returns all superclasses of the given class ordered top down. The last
	 * element is always {@link java.lang.Object}.
	 */
	public static List<Class<?>> getSuperclasses(Class<?> clazz) throws ReflectionException {
		Validate.notNull(clazz);

		try {
			List<Class<?>> clazzes = new ArrayList<Class<?>>();
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
	 */
	public static Set<Class<?>> getInterfaces(Class<?> clazz) throws ReflectionException {
		Validate.notNull(clazz);
		
		try{
			Set<Class<?>> interfaces = new HashSet<Class<?>>();
			interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
			List<Class<?>> superclasses = getSuperclasses(clazz);
			for (Class<?> superclass : superclasses) {
				interfaces.addAll(Arrays.asList(superclass.getInterfaces()));
			}
			return interfaces;
		} catch(Exception e){
			throw new ReflectionException(e);
		}
	}
}
