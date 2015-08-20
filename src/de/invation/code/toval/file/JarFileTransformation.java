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

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import javax.swing.SortOrder;

/**
 * Instances of classes that implement this interface are used to transform JAR
 * files in form of an {@link ExtendedJarFile}.
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public interface JarFileTransformation {

    /**
     * Transforms a specified {@link Enumeration} of {@link JarEntry} objects.
     *
     * @param entries Enumeration of JAR entries.
     * @return Transformed enumeration of JAR entries.
     */
    public Enumeration<JarEntry> transform(Enumeration<JarEntry> entries);

    /**
     * Sorts an {@link Enumeration} of {@link JarEntry} objects by their names.
     */
    public static class SortNameTransformation implements JarFileTransformation {

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
