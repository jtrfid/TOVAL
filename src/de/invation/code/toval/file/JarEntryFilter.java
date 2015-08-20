/*
 * Copyright (c) 2015, Thomas Stocker
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted (subject to the limitations in the disclaimer
 * below) provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of IIG Telematics, Uni Freiburg nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY
 * THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BELIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.invation.code.toval.file;

import static de.invation.code.toval.reflect.ReflectionUtils.CLASS_PATH_PATTERN;
import static de.invation.code.toval.reflect.ReflectionUtils.PACKAGE_PATH_SEPARATOR;
import de.invation.code.toval.validate.Validate;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;

/**
 * Instances of classes that implement this interface are used to filter entries
 * in a JAR file in form of an {@link ExtendedJarFile}.
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public interface JarEntryFilter {

    /**
     * Tests if a specified {@link JarEntry} should be included in a entries
     * list.
     *
     * @param entry Entry to test.
     * @return <code>true</code> if and only if the entry should be included in
     * the entry list; false otherwise.
     */
    public boolean accept(JarEntry entry);

    /**
     * Accepts only classes.
     */
    public static class ClassFilter implements JarEntryFilter {

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
    public static class PackageFilter implements JarEntryFilter {

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
