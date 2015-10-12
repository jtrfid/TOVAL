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

import de.invation.code.toval.validate.Validate;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A class that monitors the read progress of an input stream.
 *
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 * @since 1.0.2
 * @version 1.0.2
 */
public class MonitoredInputStream extends FilterInputStream {

        /**
         * Default step size for triggering a progress change. By default 1MB.
         */
        public static final int DEFAULT_STEP_SIZE = 1024 * 1024;

        private volatile long mark = 0;
        private volatile long lastTriggeredLocation = 0;
        private volatile long loc = 0;
        private final long fileSize;
        private final int stepSize;
        private final List<ChangeListener> listeners = new ArrayList<>();

        /**
         * Creates a MonitoredInputStream with a specified step size.
         *
         * @param in Underlying input stream.
         * @param fileSize File size of the underlying input.
         * @param stepSize Progress change (in byte) to trigger change event.
         */
        public MonitoredInputStream(InputStream in, long fileSize, int stepSize) {
                super(in);
                Validate.notNull(in);
                Validate.positive(fileSize);

                this.fileSize = fileSize;
                if (stepSize < 0) {
                        this.stepSize = DEFAULT_STEP_SIZE;
                } else {
                        this.stepSize = stepSize;
                }
        }

        /**
         * Creates a MonitoredInputStream with default step size.
         *
         * @param in Underlying input stream.
         * @param fileSize File size of the underlying input.
         */
        public MonitoredInputStream(InputStream in, long fileSize) {
                super(in);
                Validate.notNull(in);
                Validate.positive(fileSize);

                this.fileSize = fileSize;
                this.stepSize = DEFAULT_STEP_SIZE;
        }

        /**
         * Adds a {@link ChangeListener} to the input stream.
         *
         * @param l Listener to add.
         */
        public void addChangeListener(ChangeListener l) {
                if (!listeners.contains(l)) {
                        listeners.add(l);
                }
        }

        /**
         * Removes a listener.
         *
         * @param l Listener to remove.
         */
        public void removeChangeListener(ChangeListener l) {
                listeners.remove(l);
        }

        /**
         * Returns the current location.
         *
         * @return
         */
        public long getLocation() {
                return loc;
        }

        /**
         * Returns the current progress.
         *
         * @return Progress as integer value in the interval [0 ; 100].
         */
        public int getProgress() {
                return (int) ((double) loc / (double) fileSize * 100D);
        }

        private void triggerChanged(final long location) {
                if (stepSize > 0 && Math.abs(location - lastTriggeredLocation) < stepSize) {
                        return;
                }
                lastTriggeredLocation = location;
                if (listeners.size() <= 0) {
                        return;
                }
                try {
                        final ChangeEvent evt = new ChangeEvent(this);
                        for (ChangeListener l : listeners) {
                                l.stateChanged(evt);
                        }
                } catch (ConcurrentModificationException e) {
                        triggerChanged(location);
                }
        }

        @Override
        public int read() throws IOException {
                final int i = super.read();
                if (i != -1) {
                        triggerChanged(loc++);
                }
                return i;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
                final int i = super.read(b, off, len);
                if (i > 0) {
                        triggerChanged(loc += i);
                }
                return i;
        }

        @Override
        public long skip(long n) throws IOException {
                final long i = super.skip(n);
                if (i > 0) {
                        triggerChanged(loc += i);
                }
                return i;
        }

        @Override
        public void mark(int readlimit) {
                super.mark(readlimit);
                mark = loc;
        }

        @Override
        public void reset() throws IOException {
                super.reset();
                if (loc != mark) {
                        triggerChanged(loc = mark);
                }
        }
}
