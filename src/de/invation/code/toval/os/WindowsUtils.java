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
import de.invation.code.toval.os.WindowsRegistry.RegistryException;
import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils class for Windows operating system regarding functionalities and
 * properties.
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public final class WindowsUtils extends OSUtils<String> {

    private static WindowsUtils instance = null;

    /**
     * Default Windows line separator
     */
    public static final String WINDOWS_LINE_SEPARATOR = "\r\n";

    private static final String SOFTWARE = "Software";
    private static final String CLASSES = "Classes";
    private static final String SHELL = "shell";
    private static final String OPEN = "open";
    private static final String COMMAND = "command";

    private static final String SOFTWARE_CLASSES_PATH = WindowsRegistry.REG_PATH_SEPARATOR + SOFTWARE + WindowsRegistry.REG_PATH_SEPARATOR + CLASSES + WindowsRegistry.REG_PATH_SEPARATOR;
    private static final String SHELL_PATH = WindowsRegistry.REG_PATH_SEPARATOR + SHELL;
    private static final String SHELL_OPEN_PATH = SHELL_PATH + WindowsRegistry.REG_PATH_SEPARATOR + OPEN;
    private static final String SHELL_OPEN_COMMAND_PATH = SHELL_OPEN_PATH + WindowsRegistry.REG_PATH_SEPARATOR + COMMAND;

    private static final Pattern PATTERN_TEXT_BETWEEN_QUOTES = Pattern.compile("\"([^\"]*)\"");

    private WindowsUtils() {
    }

    /**
     * Returns the associated application for a given extension.
     *
     * @param fileTypeExtension File extension with leading dot, e.g.
     * <code>.bar</code>.
     * @return {@link String} with path to associated application or
     * <code>null</code> if file extension is not registered or can't be read.
     * @throws RegistryException If Registry can't be read.
     */
    @Override
    public String getFileExtension(String fileTypeExtension) throws RegistryException, OSException {
        if (!isApplicable() || !isFileExtensionRegistered(fileTypeExtension)) {
            return null;
        }

        // sanitize file extension
        fileTypeExtension = sanitizeFileExtension(fileTypeExtension);

        Hive[] hives = {Hive.HKEY_CURRENT_USER, Hive.HKEY_LOCAL_MACHINE};

        // get fileTypeName
        String fileTypeName = null;
        for (Hive h : hives) {
            if (WindowsRegistry.existsKey(h.getName() + SOFTWARE_CLASSES_PATH + fileTypeExtension)) {
                fileTypeName = WindowsRegistry.readValue(h.getName() + SOFTWARE_CLASSES_PATH + fileTypeExtension, WindowsRegistry.DEFAULT_KEY_NAME);
            }
        }

        // get file association
        if (fileTypeName != null) {
            for (Hive h : hives) {
                if (WindowsRegistry.existsKey(h.getName() + SOFTWARE_CLASSES_PATH + fileTypeName + SHELL_OPEN_COMMAND_PATH)) {
                    String fileTypeAssociation = WindowsRegistry.readValue(h.getName() + SOFTWARE_CLASSES_PATH + fileTypeName + SHELL_OPEN_COMMAND_PATH, WindowsRegistry.DEFAULT_KEY_NAME);
                    Matcher m = PATTERN_TEXT_BETWEEN_QUOTES.matcher(fileTypeAssociation);

                    // If exists return first string between quotes
                    while (m.find()) {
                        return m.group(1);
                    }

                    // otherwise return complete file association string
                    return fileTypeAssociation;
                }
            }
        }
        return null;
    }

    /**
     * Returns singleton instance of {@link OSUtils}.
     *
     * @return instance
     */
    public static synchronized WindowsUtils instance() {
        if (instance == null) {
            instance = new WindowsUtils();
        }
        return instance;
    }

    /**
     * Returns <code>true</code> if current OS is Windows and this class can be
     * used.
     *
     * @return <code>true</code> if OS is Windows, <code>false</code> otherwise.
     */
    @Override
    public boolean isApplicable() {
        return getCurrentOS() == OSType.OS_WINDOWS;
    }

    /**
     * Checks if the given extension is already registered.
     *
     * @param fileTypeExtension File extension with leading dot, e.g.
     * <code>.bar</code>.
     * @return <code>true</code> if extension is registered, <code>false</code>
     * otherwise.
     * @throws RegistryException
     */
    @Override
    public boolean isFileExtensionRegistered(String fileTypeExtension) throws RegistryException, OSException {
        if (!isApplicable()) {
            return false;
        }

        // sanitize file extension
        fileTypeExtension = sanitizeFileExtension(fileTypeExtension);

        String[] hives = {Hive.HKEY_CURRENT_USER + SOFTWARE_CLASSES_PATH, Hive.HKEY_LOCAL_MACHINE + SOFTWARE_CLASSES_PATH, Hive.HKEY_CLASSES_ROOT + "\\"};

        for (String h : hives) {
            List<String> subkeys = WindowsRegistry.readSubkeys(h);
            if (subkeys.contains(fileTypeExtension)) {
                return true;
            }
        }
        return false;
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
     * @return <code>true</code> if registration was successful,
     * <code>false</code> otherwise.
     * @throws RegistryException If keys can't be created or values can't be
     * written.
     */
    @Override
    public boolean registerFileExtension(String fileTypeName, String fileTypeExtension, String application) throws RegistryException, OSException {
        if (!isApplicable()) {
            return false;
        }

        // sanitize file extension
        fileTypeExtension = sanitizeFileExtension(fileTypeExtension);

        // create path to linked application
        String applicationPath = toWindowsPath(new File(application).getAbsolutePath());

        // Set registry hive
        WindowsRegistry.Hive hive = WindowsRegistry.Hive.HKEY_CURRENT_USER;

        // create programmatic identifier
        WindowsRegistry.createKey(hive.getName() + SOFTWARE_CLASSES_PATH + fileTypeName);

        // create verb to open file type
        WindowsRegistry.createKey(hive.getName() + SOFTWARE_CLASSES_PATH + fileTypeName + SHELL_OPEN_COMMAND_PATH);
        WindowsRegistry.writeValue(hive.getName() + SOFTWARE_CLASSES_PATH + fileTypeName + SHELL_OPEN_COMMAND_PATH, WindowsRegistry.DEFAULT_KEY_NAME, "\"" + applicationPath + "\" \"%1\"");

        // associate with file extension
        WindowsRegistry.createKey(hive.getName() + SOFTWARE_CLASSES_PATH + fileTypeExtension);
        WindowsRegistry.writeValue(hive.getName() + SOFTWARE_CLASSES_PATH + fileTypeExtension, WindowsRegistry.DEFAULT_KEY_NAME, fileTypeName);
        return true;
    }

    @Override
    public void runCommand(String[] command, GenericHandler<BufferedReader> inputHandler, GenericHandler<BufferedReader> errorHandler) throws OSException {
        String[] cmd = new String[command.length + 2];
        cmd[0] = "cmd";
        cmd[1] = "/C";
        System.arraycopy(command, 0, cmd, 2, command.length);
        super.runCommand(cmd, inputHandler, errorHandler);
    }

    /**
     * Replaces all <code>/</code> in a given path by a <code>\</code>.
     *
     * @param path Path to convert to the Windows format
     * @return Given path in the Windows path format
     */
    public String toWindowsPath(String path) {
        return path.replaceAll("/", WindowsRegistry.REG_PATH_SEPARATOR_REGEX);
    }
}
