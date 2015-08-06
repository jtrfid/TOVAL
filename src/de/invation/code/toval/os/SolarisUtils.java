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
package de.invation.code.toval.os;

/**
 * Utils class for Solaris operating system regarding functionalities and
 * properties.
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public class SolarisUtils extends OSUtils<String> {

    private static SolarisUtils instance = null;

    /**
     * Default Windows line separator
     */
    public static final String SOLARIS_LINE_SEPARATOR = "\n";

    private SolarisUtils() {
    }

    /**
     * Returns the associated application for a given extension.
     *
     * @param fileTypeExtension File extension with leading dot, e.g.
     * <code>.bar</code>.
     * @return {@link String} with path to associated application or
     * <code>null</code> if file extension is not registered or can't be read.
     * @throws OSException
     */
    @Override
    public String getFileExtension(String fileTypeExtension) throws OSException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns singleton instance of {@link SolarisUtils}.
     *
     * @return instance
     */
    public static synchronized SolarisUtils instance() {
        if (instance == null) {
            instance = new SolarisUtils();
        }
        return instance;
    }

    /**
     * Returns <code>true</code> if current OS is Solaris and this class can be
     * used.
     *
     * @return <code>true</code> if OS is Solaris, <code>false</code> otherwise.
     */
    @Override
    public boolean isApplicable() {
        return getCurrentOS() == OSType.OS_SOLARIS;
    }

    /**
     * Checks if the given extension is already registered.
     *
     * @param fileTypeExtension File extension with leading dot, e.g.
     * <code>.bar</code>.
     * @return <code>true</code> if extension is registered, <code>false</code>
     * otherwise.
     * @throws OSException
     */
    @Override
    public boolean isFileExtensionRegistered(String fileTypeExtension) throws OSException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Registers a new file extension in the operating system.
     *
     * @param fileTypeName Name of the file extension. Must be atomic, e.g.
     * <code>foocorp.fooapp.v1</code>.
     * @param fileTypeExtension File extension with leading dot, e.g.
     * <code>.bar</code>.
     * @param application Path to the application, which should open the new
     * file extension.
     * @return <code>true</code> if registration was successful,
     * <code>false</code> otherwise.
     * @throws OSException
     */
    @Override
    public boolean registerFileExtension(String fileTypeName, String fileTypeExtension, String application) throws OSException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
