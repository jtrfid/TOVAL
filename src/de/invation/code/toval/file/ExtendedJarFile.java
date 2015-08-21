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

import de.invation.code.toval.misc.Transformable;
import de.invation.code.toval.misc.Filterable;
import de.invation.code.toval.validate.Validate;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import javax.swing.SortOrder;

/**
 * <p>
 * The ExtendedJarFile class is used to read the contents of a jar file from any
 * file that can be opened with java.io.RandomAccessFile. It extends the class
 * {@link JarFile} by the option to filter Jar entries.
 * </p>
 *
 * <p>
 * Unless otherwise noted, passing a null argument to a constructor or method in
 * this class will cause a NullPointerException to be thrown.
 * </p>
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public class ExtendedJarFile extends JarFile {

    private final List<Filterable<JarEntry>> filters = new ArrayList<>();
    private final List<Transformable<Enumeration<JarEntry>>> transformations = new ArrayList<>();

    /**
     * Creates a new JarFile to read from the specified file name.
     *
     * @param string File name.
     * @throws IOException
     */
    public ExtendedJarFile(String string) throws IOException {
        super(string);
    }

    /**
     * Creates a new JarFile to read from the specified file name.
     *
     * @param string File name.
     * @param verify
     * @throws IOException
     */
    public ExtendedJarFile(String string, boolean verify) throws IOException {
        super(string, verify);
    }

    /**
     * Creates a new JarFile to read from the specified File object.
     *
     * @param file Target file.
     * @throws IOException
     */
    public ExtendedJarFile(File file) throws IOException {
        super(file);
    }

    /**
     * Creates a new JarFile to read from the specified File object.
     *
     * @param file Target file.
     * @param verify
     * @throws IOException
     */
    public ExtendedJarFile(File file, boolean verify) throws IOException {
        super(file, verify);
    }

    /**
     * Creates a new JarFile to read from the specified File object in the
     * specified mode.
     *
     * @param file Target file.
     * @param verify
     * @param mode
     * @throws IOException
     */
    public ExtendedJarFile(File file, boolean verify, int mode) throws IOException {
        super(file, verify, mode);
    }

    /**
     * Adds a new filter as {@link Filterable}.
     *
     * @param filter Filter to add.
     */
    public final void addFilter(Filterable<JarEntry> filter) {
        filters.add(filter);
    }

    /**
     * Adds a new {@link Transformable}.
     *
     * @param transformation Transformation to add.
     */
    public final void addTransformation(Transformable<Enumeration<JarEntry>> transformation) {
        transformations.add(transformation);
    }

    @Override
    public Enumeration<JarEntry> entries() {
        if (filters.isEmpty() && transformations.isEmpty()) {
            return entries();
        }

        // Filters
        Enumeration<JarEntry> allEntries = super.entries();
        Vector<JarEntry> filteredEntries = new Vector<>();
        while (allEntries.hasMoreElements()) {
            JarEntry entry = allEntries.nextElement();
            for (Filterable<JarEntry> filter : filters) {
                if (filter.accept(entry)) {
                    filteredEntries.add(entry);
                }
            }
        }

        // Transformations
        Enumeration<JarEntry> filteredEnumeration = filteredEntries.elements();
        for (Transformable<Enumeration<JarEntry>> transformation : transformations) {
            filteredEnumeration = transformation.transform(filteredEnumeration);
        }

        return filteredEnumeration;
    }

    /**
     * Returns all filters.
     *
     * @return List of filters.
     */
    public List<Filterable> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    /**
     * Returns all transformations.
     *
     * @return List of transformations.
     */
    public List<Transformable<Enumeration<JarEntry>>> getTransformations() {
        return Collections.unmodifiableList(transformations);
    }

    @Override
    public Stream<JarEntry> stream() {
        Stream.Builder<JarEntry> builder = Stream.builder();
        Enumeration<JarEntry> entries = entries();
        while (entries.hasMoreElements()) {
            builder.add(entries.nextElement());
        }
        return builder.build();
    }

    /**
     * Sorts an {@link Enumeration} of {@link JarEntry} objects by their names.
     */
    public static class SortNameTransformation implements Transformable<Enumeration<JarEntry>> {

        public final SortOrder sortOrder;

        /**
         * Creates a new sort transformation with ascending order.
         */
        public SortNameTransformation() {
            this(SortOrder.ASCENDING);
        }

        /**
         * Creates a new sort transformation with the specified order.
         *
         * @param sortOrder
         */
        public SortNameTransformation(SortOrder sortOrder) {
            Validate.notNull(sortOrder);

            this.sortOrder = sortOrder;
        }

        @Override
        public Enumeration<JarEntry> transform(Enumeration<JarEntry> entries) {
            Comparator<JarEntry> comparator = new Comparator<JarEntry>() {
                @Override
                public int compare(JarEntry firstEntry, JarEntry secondEntry) {
                    return firstEntry.getName().compareTo(secondEntry.getName());
                }
            };

            Vector<JarEntry> list = new Vector<>(Collections.list(entries));
            if (sortOrder == SortOrder.ASCENDING) {
                Collections.sort(list, comparator);
            } else if (sortOrder == SortOrder.DESCENDING) {
                Collections.sort(list, Collections.reverseOrder(comparator));
            }

            return list.elements();
        }
    }
}
