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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * <p>
 The ExtendedJarFile class is used to read the contents of a jar file from any
 file that can be opened with java.io.RandomAccessFile. It extends the class
 {@link JarFile} by the option to filter Jar entries.
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

    private final Set<JarEntryFilter> filters = new HashSet<>();

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
     * Adds a new {@link JarEntryFilter}.
     *
     * @param filter Filter to add.
     */
    public final void addFilter(JarEntryFilter filter) {
        filters.add(filter);
    }

    @Override
    public Enumeration<JarEntry> entries() {
        if (filters.isEmpty()) {
            return entries();
        }

        // Filters
        Enumeration<JarEntry> allEntries = super.entries();
        Vector<JarEntry> filteredEntries = new Vector<>();

        while (allEntries.hasMoreElements()) {
            JarEntry entry = allEntries.nextElement();
            for (JarEntryFilter filter : filters) {
                if (filter.accept(entry)) {
                    filteredEntries.add(entry);
                }
            }
        }

        return filteredEntries.elements();
    }

    /**
     * Returns all filters.
     *
     * @return Set of filters.
     */
    public Set<JarEntryFilter> getFilters() {
        return Collections.unmodifiableSet(filters);
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
}
