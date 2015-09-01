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
 * <p>
 * Static class to access the Linux MIME database.
 * </p>
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public class LinuxMIMEDatabase {

    /*
     * Array of possible file names for the extension list. Both should be writable.
     */
    private static final String[] MIME_APPS_LISTS = {OSUtils.getUserHomeDirectory().getAbsolutePath() + "/.local/share/applications/defaults.list", OSUtils.getUserHomeDirectory().getAbsolutePath() + "/.local/share/applications/mimeapps.list", "/usr/share/applications/defaults.list", "/usr/share/applications/mimeapps.list", "/usr/share/applications/mimeinfo.cache", OSUtils.getUserHomeDirectory().getAbsolutePath() + "/.local/share/applications/mimeinfo.cache"};

    /**
     * matches a MIME type
     */
    protected final static Pattern MIME_PATTERN = Pattern.compile("[\\w\\d-]+\\/[\\w\\d-\\+\\.]+", Pattern.CASE_INSENSITIVE);

    /*
     * Matches lists of MIME to list of application associations:
     * application/xml=brasero.desktop;mount-archive.desktop;
     */
    private static final Pattern MIME_TYPE_APPS_PATTERN = Pattern.compile("([\\w\\d-]+\\/[\\w\\d-+\\.]+)\\=|([\\w\\d- \\.]+\\.desktop)", Pattern.CASE_INSENSITIVE);

    /*
     * Matches file extension specifications in MIME type files (see MIME_TYPE_FILES)
     */
    private static final Pattern MIME_TYPE_FILE_EXTENSION_PATTERN = Pattern.compile("<glob\\s+pattern=\\\"\\*?(\\.[\\w\\d]+)\\\"\\s*(?:\\/>|>(?:(?!<\\/glob>).)*<\\/glob>)", Pattern.CASE_INSENSITIVE);
    /*
     * Matches MIME types declarations in MIME type files (see MIME_TYPE_FILES)
     */
    private static final Pattern MIME_TYPE_FILE_MIME_PATTERN = Pattern.compile("type=\\\"([\\w]+\\/[\\w\\d\\\\+-\\\\.]+)+\\\"", Pattern.CASE_INSENSITIVE);

    /*
     * List of directories with MIME-type files with aliases and extension associations
     */
    private static final String[] MIME_TYPE_FILES = {"/usr/share/mime/application/"};

    /*
     * List of MIME-types with boolean value indicating if list is writable
     */
    private static final String[] MIME_TYPE_LISTS = {"/usr/share/mime/types", OSUtils.getUserHomeDirectory().getAbsolutePath() + "/.local/share/mime/types"};

    private Set<String> mimeTypes = null;
    private Map<String, Set<String>> mimeApps = null;
    private Map<String, Set<String>> extensionMime = null;
    private Map<String, Set<String>> mimeExtension = null;

    /* singleton instance */
    private static LinuxMIMEDatabase instance = null;

    private LinuxMIMEDatabase() {
    }

    /**
     * Creates a map with file extensions as keys pointing on sets of MIME types
     * associated with them.
     *
     * @return Map of file extensions pointing on MIME types
     * @throws OSException
     */
    public final Map<String, Set<String>> getExtensionMimeMap() throws OSException {
        initializeMimeExtensionArrays();
        return Collections.unmodifiableMap(extensionMime);
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
                            Matcher extMatcher = MIME_TYPE_APPS_PATTERN.matcher(line);
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
     * Creates a map with MIME types as keys pointing on sets of associated file
     * extensions.
     *
     * @return Map of MIME types pointing on sets of file extensions
     * @throws OSException
     */
    public final Map<String, Set<String>> getMimeExtensionMap() throws OSException {
        initializeMimeExtensionArrays();
        return Collections.unmodifiableMap(mimeExtension);
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

                                Matcher extMatcher = MIME_TYPE_FILE_EXTENSION_PATTERN.matcher(content);
                                while (extMatcher.find()) {
                                    tmpExtensions.add(extMatcher.group(1).toLowerCase());
                                }

                                Matcher mimeMatcher = MIME_TYPE_FILE_MIME_PATTERN.matcher(content);
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
     * Returns singleton instance of {@link LinuxMIMEDatabase}.
     *
     * @return instance
     */
    public static synchronized LinuxMIMEDatabase instance() {
        // lazy initialization
        if (instance == null) {
            instance = new LinuxMIMEDatabase();
        }
        return instance;
    }
}
