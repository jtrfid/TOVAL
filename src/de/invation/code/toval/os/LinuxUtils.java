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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /*
     * Matches lists of MIME to list of application associations:
     * application/xml=brasero.desktop;mount-archive.desktop;
     */
    private static final Pattern MIME_TYPE_APPS = Pattern.compile("([a-z0-9-]+\\/[a-z0-9-+\\.]+)\\=|([a-z0-9- \\.]+\\.desktop)", Pattern.CASE_INSENSITIVE);

    /*
     * Matches file extension specifications in MIME type files (see MIME_TYPE_FILES)
     */
    private static final Pattern MIME_TYPE_FILE_EXTENSION = Pattern.compile("<glob\\s+pattern=\\\"\\*?(\\.[a-z0-9]+)\\\"\\s*(?:\\/>|>[\\d\\w\\s]*<\\/glob>)", Pattern.CASE_INSENSITIVE);
    /*
     * Matches MIME types declarations in MIME type files (see MIME_TYPE_FILES)
     */
    private static final Pattern MIME_TYPE_FILE_MIME = Pattern.compile("type=\\\"([a-zA-Z]+\\/[a-zA-Z0-9\\\\+-\\\\.]+)+\\\"", Pattern.CASE_INSENSITIVE);

    /*
     * Array of possible file names for the extension list. Both should be writable.
     */
    private static final String[] MIME_APPS_LISTS = {getUserHomeDirectory().getAbsolutePath() + "/.local/share/applications/defaults.list", getUserHomeDirectory().getAbsolutePath() + "/.local/share/applications/mimeapps.list", "/usr/share/applications/defaults.list", "/usr/share/applications/mimeapps.list", "/usr/share/applications/mimeinfo.cache", getUserHomeDirectory().getAbsolutePath() + "/.local/share/applications/mimeinfo.cache"};

    /*
     * List of MIME-types with boolean value indicating if list is writable
     */
    private static final String[] MIME_TYPE_LISTS = {"/usr/share/mime/types", getUserHomeDirectory().getAbsolutePath() + "/.local/share/mime/types"};

    /*
     * List of directories with MIME-type files with aliases and extension associations
     */
    private static final String[] MIME_TYPE_FILES = {"/usr/share/mime/application/"};

    private static LinuxUtils instance = null;

    private Set<String> mimeTypes = null;
    private Map<String, Set<String>> mimeApps = null;
    private Map<String, Set<String>> extensionMime = null;
    private Map<String, Set<String>> mimeExtension = null;

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
        initializeMimeExtensionArrays();
        fileTypeExtension = sanitizeFileExtension(fileTypeExtension);

        if (!extensionMime.containsKey(fileTypeExtension)) {
            throw new OSException("File type extension \"" + fileTypeExtension + "\" is not registered.");
        }

        Set<String> apps = new HashSet<>();
        Set<String> allMimeTypes = extensionMime.get(fileTypeExtension);
        Map<String, Set<String>> mimeApplications = getMimeApps();
        for (String mimeType : allMimeTypes) {
            if (mimeApplications.containsKey(mimeType) && mimeApplications.get(mimeType) != null) {
                apps.addAll(mimeApplications.get(mimeType));
            }
        }
        return apps;
    }

    /**
     * Creates a map of MIME types pointing on the associated application.
     *
     * @return Key-value-pairs of MIME types and the associated application.
     * @throws OSException
     */
    public Map<String, Set<String>> getMimeApps() throws OSException {
        // lazy initialization
        if (mimeApps == null) {
            mimeApps = new HashMap<>();

            for (String path : MIME_APPS_LISTS) {
                File mimeAppListFile = new File(path);
                if (mimeAppListFile.exists() && mimeAppListFile.isFile() && mimeAppListFile.canRead()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(mimeAppListFile))) {
                        for (String line; (line = br.readLine()) != null;) {
                            String mimeType = null;
                            Set<String> apps = new HashSet<>();
                            Matcher extMatcher = MIME_TYPE_APPS.matcher(line);
                            while (extMatcher.find()) {
                                if (extMatcher.group(1) != null) {
                                    mimeType = extMatcher.group(1);
                                }
                                if (extMatcher.group(2) != null) {
                                    apps.add(extMatcher.group(2));
                                }
                            }

                            if (mimeType != null && apps.size() > 0) {
                                mimeApps.put(mimeType, apps);
                            }
                        }
                    } catch (IOException e) {
                        throw new OSException(e);
                    }
                }
            }
        }
        return Collections.unmodifiableMap(mimeApps);
    }

    /**
     * Create a {@link Set} of all registered MIME types.
     *
     * @return Set of MIME types as String values
     * @throws OSException
     */
    public Set<String> getMimeTypes() throws OSException {
        // lazy initialization
        if (mimeTypes == null) {
            mimeTypes = new HashSet<>();

            for (String path : MIME_TYPE_LISTS) {
                File mimeListFile = new File(path);
                if (mimeListFile.isFile() && mimeListFile.canRead()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(mimeListFile))) {
                        for (String line; (line = br.readLine()) != null;) {
                            line = line.replaceAll("\\s+", "");
                            if (MIME_PATTERN.matcher(line).matches()) {
                                mimeTypes.add(line);
                            }
                        }
                    } catch (IOException e) {
                        throw new OSException(e);
                    }
                }
            }
        }
        return Collections.unmodifiableSet(mimeTypes);
    }

    /**
     * Returns singleton instance of {@link OSUtils}.
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
     * Reads all associations of MIME types with file extensions and fills the
     * arrays {@link #extensionMime} and {@link #mimeExtension}.
     *
     * @throws OSException
     */
    private void initializeMimeExtensionArrays() throws OSException {
        // lazy initialization
        if (extensionMime == null || mimeExtension == null) {
            extensionMime = new HashMap<>();
            mimeExtension = new HashMap<>();

            // list all files in all directories in MIME_TYPE_FILES
            for (String dir : MIME_TYPE_FILES) {
                File d = new File(dir);
                if (d.exists() && d.isDirectory()) {
                    for (final File fileEntry : d.listFiles()) {
                        if (fileEntry.isFile() && fileEntry.canRead()) {
                            Set<String> tmpExtensions = new HashSet<>();
                            Set<String> tmpMimeTypes = new HashSet<>();
                            try {
                                String content = new String(Files.readAllBytes(fileEntry.toPath()));

                                Matcher extMatcher = MIME_TYPE_FILE_EXTENSION.matcher(content);
                                while (extMatcher.find()) {
                                    tmpExtensions.add(extMatcher.group(1).toLowerCase());
                                }

                                Matcher mimeMatcher = MIME_TYPE_FILE_MIME.matcher(content);
                                while (mimeMatcher.find()) {
                                    tmpMimeTypes.add(mimeMatcher.group(1).toLowerCase());
                                }
                            } catch (IOException ex) {
                                throw new OSException(ex);
                            }

                            // fill arrays
                            for (String ext : tmpExtensions) {
                                if (extensionMime.containsKey(ext)) {
                                    Set<String> tmp = extensionMime.get(ext);
                                    tmp.addAll(tmpMimeTypes);
                                    extensionMime.put(ext, tmp);
                                } else {
                                    extensionMime.put(ext, tmpMimeTypes);
                                }
                            }
                            for (String mime : tmpMimeTypes) {
                                if (mimeExtension.containsKey(mime)) {
                                    Set<String> tmp = mimeExtension.get(mime);
                                    tmp.addAll(tmpExtensions);
                                    mimeExtension.put(mime, tmp);
                                } else {
                                    mimeExtension.put(mime, tmpExtensions);
                                }
                            }
                        }
                    }
                }
            }
        }
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
