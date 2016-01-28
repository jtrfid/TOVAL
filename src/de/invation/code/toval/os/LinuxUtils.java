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

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utils class for Linux operating system regarding functionalities and
 * properties.
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public final class LinuxUtils extends OSUtils<Set<String>> {

    /**
     * Default Windows line separator
     */
    public static final String LINUX_LINE_SEPARATOR = "\n";

    /* singleton instance */
    private static LinuxUtils instance = null;

    private LinuxUtils() {
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
        try {
            getFileExtension(fileTypeExtension);
            return true;
        } catch (OSException e) {
            return false;
        }
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
    public Set<String> getFileExtension(String fileTypeExtension) throws OSException {
        fileTypeExtension = sanitizeFileExtension(fileTypeExtension);

        if (!LinuxMIMEDatabase.instance().getExtensionMimeMap().containsKey(fileTypeExtension)) {
            throw new OSException("File type extension \"" + fileTypeExtension + "\" is not registered.");
        }

        Set<String> apps = new HashSet<>();
        Set<String> allMimeTypes = LinuxMIMEDatabase.instance().getExtensionMimeMap().get(fileTypeExtension);
        Map<String, Set<String>> mimeApplications = LinuxMIMEDatabase.instance().getMimeApps();
        for (String mimeType : allMimeTypes) {
            if (mimeApplications.containsKey(mimeType) && mimeApplications.get(mimeType) != null) {
                apps.addAll(mimeApplications.get(mimeType));
            }
        }
        return apps;
    }

    /**
     * Returns singleton instance of {@link LinuxUtils}.
     *
     * @return instance
     */
    public static synchronized LinuxUtils instance() {
        if (instance == null) {
            instance = new LinuxUtils();
        }
        return instance;
    }

    /**
     * Returns <code>true</code> if current OS is Linux and this class can be
     * used.
     *
     * @return <code>true</code> if OS is Linux, <code>false</code> otherwise.
     */
    @Override
    public boolean isApplicable() {
        return getCurrentOS() == OSType.OS_LINUX;
    }

    /**
     * Registers a new file extension.
     *
     * @param mimeTypeName MIME type of the file extension. Must be atomic, e.g.
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
    public boolean registerFileExtension(String mimeTypeName, String fileTypeExtension, String application) throws OSException {
        // TODO create mime-info in /usr/share/mime/packages
        // TODO add MIME type to .desktop file
        // not possible due to missing permissions?
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void runCommand(String[] command, GenericHandler<BufferedReader> inputHandler, GenericHandler<BufferedReader> errorHandler) throws OSException {
        String[] cmd = new String[command.length + 2];
        cmd[0] = "/bin/sh";
        cmd[1] = "-c";
        System.arraycopy(command, 0, cmd, 2, command.length);
        super.runCommand(cmd, inputHandler, errorHandler);
    }

    /**
     * Enum to represent the different desktop environments.
     *
     * @version 1.0
     * @author Adrian Lange <lange@iig.uni-freiburg.de>
     */
    public static enum LinuxDesktopEnvironments {

        GNOME("GNOME"), KDE("KDE"), LXDE("LXDE"), MATE("MATE"), RAZOR("Razor"), ROX("ROX"), TDE("TDE"), UNITY("Unity"), XFCE("XFCE"), EDE("EDE"), CINNAMON("Cinnamon"), OLD("Old");

        public final String name;

        private LinuxDesktopEnvironments(String name) {
            this.name = name;
        }
    }
}
