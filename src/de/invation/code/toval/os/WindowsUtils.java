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

import de.invation.code.toval.os.WindowsRegistry.Hive;
import java.io.File;
import java.util.List;

/**
 * Utils class for Windows operating system regarding functionalities and
 * properties.
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public final class WindowsUtils extends OSUtils {

    /**
     * Default Windows line separator
     */
    public static final String WINDOWS_LINE_SEPARATOR = "\r\n";

    private static final String SOFTWARE_CLASSES = "\\Software\\Classes\\";
    private static final String SHELL_OPEN_COMMAND = "\\shell\\open\\command";
    private static final String DEFAULT_KEY_NAME = "";

    /**
     * Checks if the given extension is already registered.
     *
     * @param fileTypeExtension File extension with leading dot, e.g.
     * <code>.bar</code>.
     * @return <code>true</code> if extension is registered, <code>false</code>
     * otherwise.
     */
    public static boolean isFileExtensionRegistered(String fileTypeExtension) {
        if (!isWindows()) {
            return false;
        }

        // sanitize file extension
        if (!fileTypeExtension.substring(0, 1).equals(".")) {
            fileTypeExtension = "." + fileTypeExtension;
        }

        String[] hives = {Hive.HKEY_CURRENT_USER + SOFTWARE_CLASSES, Hive.HKEY_LOCAL_MACHINE + SOFTWARE_CLASSES, Hive.HKEY_CLASSES_ROOT + "\\"};

        for (String h : hives) {
            List<String> subkeys = WindowsRegistry.readSubkeys(h);
            if (subkeys.contains(fileTypeExtension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given extension is already registered.
     *
     * @param fileTypeExtension File extension with leading dot, e.g.
     * <code>.bar</code>.
     * @return <code>true</code> if extension is registered, <code>false</code>
     * otherwise.
     */
    public static boolean getFileExtension(String fileTypeExtension) {
        if (!isWindows()) {
            return false;
        }

        // sanitize file extension
        if (!fileTypeExtension.substring(0, 1).equals(".")) {
            fileTypeExtension = "." + fileTypeExtension;
        }

        Hive[] hives = {Hive.HKEY_CURRENT_USER, Hive.HKEY_LOCAL_MACHINE};

        for (Hive h : hives) {
            List<String> subkeys = WindowsRegistry.readSubkeys(h.getName() + SOFTWARE_CLASSES);
            if (subkeys.contains(fileTypeExtension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if current OS is Windows and this class can be
     * used.
     *
     * @return <code>true</code> if OS is Windows, <code>false</code> otherwise.
     */
    public static boolean isWindows() {
        return getCurrentOS() == OSType.OS_WINDOWS;
    }

    /**
     * Registers a new file extension in the Windows registry.
     *
     * @param fileTypeName Name of the file extension. Must be atomic, e.g.
     * <code>foocorp.fooapp.v1</code>.
     * @param fileTypeExtension File extension with leading dot, e.g.
     * <code>.bar</code>.
     * @param application Path to the application, which should open the new
     * file extension.
     * @param userOnly Set <code>true</code> if file association should only be
     * created for the current user.
     * @return <code>true</code> if registration was successful,
     * <code>false</code> otherwise.
     */
    public static boolean registerFileExtension(String fileTypeName, String fileTypeExtension, File application, boolean userOnly) {
        if (!isWindows()) {
            return false;
        }

        // sanitize file extension
        if (!fileTypeExtension.substring(0, 1).equals(".")) {
            fileTypeExtension = "." + fileTypeExtension;
        }

        // create path to linked application
        String applicationPath = toWindowsPath(application.getAbsolutePath());

        // Set registry hive
        WindowsRegistry.Hive hive = userOnly ? WindowsRegistry.Hive.HKEY_CURRENT_USER : WindowsRegistry.Hive.HKEY_LOCAL_MACHINE;

        // create programmatic identifier
        WindowsRegistry.createKey(hive.getName() + SOFTWARE_CLASSES + fileTypeName);

        // create verb to open file type
        WindowsRegistry.createKey(hive.getName() + SOFTWARE_CLASSES + fileTypeName + SHELL_OPEN_COMMAND);
        WindowsRegistry.writeValue(hive.getName() + SOFTWARE_CLASSES + fileTypeName + SHELL_OPEN_COMMAND, DEFAULT_KEY_NAME, "\"" + applicationPath + "\" \"%1\"");

        // associate with file extension
        WindowsRegistry.createKey(hive.getName() + SOFTWARE_CLASSES + fileTypeExtension);
        WindowsRegistry.writeValue(hive.getName() + SOFTWARE_CLASSES + fileTypeExtension, DEFAULT_KEY_NAME, fileTypeName);
        return true;
    }

    /**
     * Replaces all <code>/</code> in a given path by a <code>\</code>.
     *
     * @param path Path to convert to the Windows format
     * @return Given path in the Windows path format
     */
    public static String toWindowsPath(String path) {
        return path.replaceAll("/", "\\\\");
    }
}
