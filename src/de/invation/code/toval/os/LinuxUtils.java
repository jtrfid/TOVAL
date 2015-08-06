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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Static class to represent a Linux program starter, which is indicated by
     * the file extension ".desktop".
     *
     * @version 1.0
     * @author Adrian Lange <lange@iig.uni-freiburg.de>
     */
    public static class LinuxProgramStarter {

        // Type
        private ProgramStarterType attrType = ProgramStarterType.APPLICATION;
        // Exec
        private String attrExec = null;
        // TryExec
        private String attrTryExec = null;
        // Icon
        private String attrIcon = null;
        // Categories
        private final Set<ProgramStarterCategories> attrCategories = new HashSet<>();
        // Name
        private String attrName = null;
        private final Map<String, String> attrNameLang = new HashMap<>();
        // Generic name
        private String attrGenericName = null;
        private final Map<String, String> attrGenericNameLang = new HashMap<>();
        // Comment
        private String attrComment = null;
        private final Map<String, String> attrCommentLang = new HashMap<>();
        // Terminal
        private boolean attrTerminal = false;
        // Terminal
        private boolean attrNoDisplay = false;
        // Path
        private String attrPath = null;
        // Keywords
        private final Set<String> attrKeywords = new HashSet<>();
        // OnlyShowIn
        private final Set<LinuxDesktopEnvironments> attrOnlyShowIn = new HashSet<>();
        // NotShowIn
        private final Set<LinuxDesktopEnvironments> attrNotShowIn = new HashSet<>();
        // MimeType
        private final Set<String> attrMimeType = new HashSet<>();
        // StartupNotify
        private boolean attrStartupNotify = false;
        // StartupWMClass
        private String attrStartupWMClass = null;

        public LinuxProgramStarter(ProgramStarterType type, String name, String exec) {
            this.attrType = type;
            this.attrName = name;
            this.attrExec = exec;
        }

        /**
         * @return the attrType
         */
        public ProgramStarterType getAttrType() {
            return attrType;
        }

        /**
         * @return the attrExec
         */
        public String getAttrExec() {
            return attrExec;
        }

        /**
         * @return the attrTryExec
         */
        public String getAttrTryExec() {
            return attrTryExec;
        }

        /**
         * @return the attrIcon
         */
        public String getAttrIcon() {
            return attrIcon;
        }

        /**
         * @return the attrCategories
         */
        public Set<ProgramStarterCategories> getAttrCategories() {
            return attrCategories;
        }

        /**
         * @return the attrName
         */
        public String getAttrName() {
            return attrName;
        }

        /**
         * @return the attrNameLang
         */
        public Map<String, String> getAttrNameLang() {
            return attrNameLang;
        }

        /**
         * @return the attrGenericName
         */
        public String getAttrGenericName() {
            return attrGenericName;
        }

        /**
         * @return the attrGenericNameLang
         */
        public Map<String, String> getAttrGenericNameLang() {
            return attrGenericNameLang;
        }

        /**
         * @return the attrComment
         */
        public String getAttrComment() {
            return attrComment;
        }

        /**
         * @return the attrCommentLang
         */
        public Map<String, String> getAttrCommentLang() {
            return attrCommentLang;
        }

        /**
         * @return the attrTerminal
         */
        public boolean isAttrTerminal() {
            return attrTerminal;
        }

        /**
         * @return the attrNoDisplay
         */
        public boolean isAttrNoDisplay() {
            return attrNoDisplay;
        }

        /**
         * @return the attrPath
         */
        public String getAttrPath() {
            return attrPath;
        }

        /**
         * @return the attrKeywords
         */
        public Set<String> getAttrKeywords() {
            return attrKeywords;
        }

        /**
         * @return the attrOnlyShowIn
         */
        public Set<LinuxDesktopEnvironments> getAttrOnlyShowIn() {
            return attrOnlyShowIn;
        }

        /**
         * @return the attrNotShowIn
         */
        public Set<LinuxDesktopEnvironments> getAttrNotShowIn() {
            return attrNotShowIn;
        }

        /**
         * @return the attrMimeType
         */
        public Set<String> getAttrMimeType() {
            return attrMimeType;
        }

        /**
         * @return the attrStartupNotify
         */
        public boolean isAttrStartupNotify() {
            return attrStartupNotify;
        }

        /**
         * @return the attrStartupWMClass
         */
        public String getAttrStartupWMClass() {
            return attrStartupWMClass;
        }

        /**
         * @param attrType the attrType to set
         */
        public void setAttrType(ProgramStarterType attrType) {
            this.attrType = attrType;
        }

        /**
         * @param attrExec the attrExec to set
         */
        public void setAttrExec(String attrExec) {
            this.attrExec = attrExec;
        }

        /**
         * @param attrTryExec the attrTryExec to set
         */
        public void setAttrTryExec(String attrTryExec) {
            this.attrTryExec = attrTryExec;
        }

        /**
         * @param attrIcon the attrIcon to set
         */
        public void setAttrIcon(String attrIcon) {
            this.attrIcon = attrIcon;
        }

        /**
         * @param category the category to add
         */
        public void addAttrCategory(ProgramStarterCategories category) {
            attrCategories.add(category);
        }

        /**
         * @param attrName the attrName to set
         */
        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }

        /**
         * @param langIdentificator the language identificator (de, en, ...)
         * @param name the name to set
         * @throws OSException
         */
        public void addAttrNameLang(String langIdentificator, String name) throws OSException {
            langIdentificator = sanitizeLangIdentificator(langIdentificator);
            attrNameLang.put(langIdentificator, name);
        }

        /**
         * @param attrGenericName the attrGenericName to set
         */
        public void setAttrGenericName(String attrGenericName) {
            this.attrGenericName = attrGenericName;
        }

        /**
         * @param langIdentificator the language identificator (de, en, ...)
         * @param name the name to set
         * @throws OSException
         */
        public void setAttrGenericNameLang(String langIdentificator, String name) throws OSException {
            langIdentificator = sanitizeLangIdentificator(langIdentificator);
            attrGenericNameLang.put(langIdentificator, name);
        }

        /**
         * @param attrComment the attrComment to set
         */
        public void setAttrComment(String attrComment) {
            this.attrComment = attrComment;
        }

        /**
         * @param langIdentificator the language identificator (de, en, ...)
         * @param comment the comment to set
         * @throws OSException
         */
        public void setAttrCommentLang(String langIdentificator, String comment) throws OSException {
            langIdentificator = sanitizeLangIdentificator(langIdentificator);
            attrCommentLang.put(langIdentificator, comment);
        }

        /**
         * @param attrTerminal the attrTerminal to set
         */
        public void setAttrTerminal(boolean attrTerminal) {
            this.attrTerminal = attrTerminal;
        }

        /**
         * @param attrNoDisplay the attrNoDisplay to set
         */
        public void setAttrNoDisplay(boolean attrNoDisplay) {
            this.attrNoDisplay = attrNoDisplay;
        }

        /**
         * @param attrPath the attrPath to set
         */
        public void setAttrPath(String attrPath) {
            this.attrPath = attrPath;
        }

        /**
         * @param attrKeyword the keyword to add
         */
        public void addAttrKeywords(String attrKeyword) {
            attrKeywords.add(attrKeyword);
        }

        /**
         * @param linuxDesktopEnvironment the desktop environment to add
         */
        public void setAttrOnlyShowIn(LinuxDesktopEnvironments linuxDesktopEnvironment) {
            attrOnlyShowIn.add(linuxDesktopEnvironment);
        }

        /**
         * @param linuxDesktopEnvironment the desktop environment to add
         */
        public void setAttrNotShowIn(LinuxDesktopEnvironments linuxDesktopEnvironment) {
            attrNotShowIn.add(linuxDesktopEnvironment);
        }

        /**
         * @param mimeType the attrMimeType to set
         * @throws OSException
         */
        public void setAttrMimeType(String mimeType) throws OSException {
            if (!Pattern.matches(LinuxMIMEDatabase.MIME_PATTERN.pattern(), mimeType)) {
                throw new OSException("Invalid MIME type \"" + mimeType + "\".");
            }
            attrMimeType.add(mimeType);
        }

        /**
         * @param attrStartupNotify the attrStartupNotify to set
         */
        public void setAttrStartupNotify(boolean attrStartupNotify) {
            this.attrStartupNotify = attrStartupNotify;
        }

        /**
         * @param attrStartupWMClass the attrStartupWMClass to set
         */
        public void setAttrStartupWMClass(String attrStartupWMClass) {
            this.attrStartupWMClass = attrStartupWMClass;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();

            s.append("[Desktop Entry]").append(LINUX_LINE_SEPARATOR);
            // type
            s.append("Type=").append(getAttrType().name()).append(LINUX_LINE_SEPARATOR);
            // name
            s.append("Name=").append(getAttrName()).append(LINUX_LINE_SEPARATOR);
            for (Map.Entry<String, String> name : getAttrNameLang().entrySet()) {
                s.append("Name[").append(name.getKey()).append("]=").append(name.getValue()).append(LINUX_LINE_SEPARATOR);
            }
            // exec
            s.append("Exec=").append(getAttrExec()).append(LINUX_LINE_SEPARATOR);
            // icon
            if (getAttrIcon() != null) {
                s.append("Icon=").append(getAttrIcon()).append(LINUX_LINE_SEPARATOR);
            }
            // Comment
            if (getAttrComment() != null) {
                s.append("Comment=").append(getAttrComment()).append(LINUX_LINE_SEPARATOR);
            }
            if (getAttrCommentLang().size() > 0) {
                for (Map.Entry<String, String> comment : getAttrCommentLang().entrySet()) {
                    s.append("Comment[").append(comment.getKey()).append("]=").append(comment.getValue()).append(LINUX_LINE_SEPARATOR);
                }
            }
            // Categories
            if (getAttrCategories().size() > 0) {
                s.append("Categories=");
                for (ProgramStarterCategories cat : getAttrCategories()) {
                    s.append(cat.name).append(";");
                }
                s.append(LINUX_LINE_SEPARATOR);
            }
            // Path
            if (getAttrPath() != null) {
                s.append("Path=").append(getAttrPath()).append(LINUX_LINE_SEPARATOR);
            }
            // Keywords
            if (getAttrKeywords().size() > 0) {
                s.append("Keywords=");
                for (String keyword : getAttrKeywords()) {
                    s.append(keyword).append(";");
                }
                s.append(LINUX_LINE_SEPARATOR);
            }
            // TryExec
            if (getAttrTryExec() != null) {
                s.append("TryExec=").append(getAttrTryExec()).append(LINUX_LINE_SEPARATOR);
            }
            // Terminal
            s.append("Terminal=").append(isAttrTerminal()).append(LINUX_LINE_SEPARATOR);
            // GenericName
            if (getAttrGenericName() != null) {
                s.append("GenericName=").append(getAttrGenericName()).append(LINUX_LINE_SEPARATOR);
            }
            if (getAttrGenericNameLang().size() > 0) {
                for (Map.Entry<String, String> name : getAttrGenericNameLang().entrySet()) {
                    s.append("GenericName[").append(name.getKey()).append("]=").append(name.getValue()).append(LINUX_LINE_SEPARATOR);
                }
                s.append(LINUX_LINE_SEPARATOR);
            }
            // NoDisplay
            s.append("NoDisplay=").append(isAttrNoDisplay()).append(LINUX_LINE_SEPARATOR);
            // OnlyShowIn
            if (getAttrOnlyShowIn().size() > 0) {
                s.append("OnlyShowIn=");
                for (LinuxDesktopEnvironments env : getAttrOnlyShowIn()) {
                    s.append(env.name).append(";");
                }
                s.append(LINUX_LINE_SEPARATOR);
            }
            // NotShowIn
            if (getAttrNotShowIn().size() > 0) {
                s.append("NotShowIn=");
                for (LinuxDesktopEnvironments env : getAttrNotShowIn()) {
                    s.append(env.name).append(";");
                }
                s.append(LINUX_LINE_SEPARATOR);
            }
            // MimeType
            if (getAttrMimeType().size() > 0) {
                s.append("MimeType=");
                for (String type : getAttrMimeType()) {
                    s.append(type).append(";");
                }
                s.append(LINUX_LINE_SEPARATOR);
            }
            // StartupNotify
            s.append("StartupNotify=").append(isAttrStartupNotify()).append(LINUX_LINE_SEPARATOR);
            // StartupWMClass
            if (getAttrStartupWMClass() != null) {
                s.append("StartupWMClass=").append(getAttrStartupWMClass()).append(LINUX_LINE_SEPARATOR);
            }

            return s.toString();
        }

        private String sanitizeLangIdentificator(String langIdentifier) throws OSException {
            langIdentifier = langIdentifier.trim().toLowerCase();
            if (langIdentifier.length() > 3 || langIdentifier.length() < 2) {
                throw new OSException("Invalid language identificator \"" + langIdentifier + "\".");
            }
            return langIdentifier;
        }

        /**
         * Enum to represent the different program categories.
         *
         * @version 1.0
         * @author Adrian Lange <lange@iig.uni-freiburg.de>
         */
        public static enum ProgramStarterCategories {

            // TODO add categories of type 2 and 3 (http://standards.freedesktop.org/menu-spec/latest/apas02.html)
            AUDIOVIDEO(1, "AudioVideo"), AUDIO(1, "Audio"), VIDEO(1, "Video"), DEVELOPMENT(1, "Development"), EDUCATION(1, "Education"), GAME(1, "Game"), GRAPHICS(1, "Graphics"), NETWORK(1, "Networks"), OFFICE(1, "Office"), SCIENCE(1, "Science"), SETTINGS(1, "Settings"), SYSTEM(1, "System"), UTILITY(1, "Utility");

            public final int typeId;
            public final String name;

            private ProgramStarterCategories(int typeId, String name) {
                this.typeId = typeId;
                this.name = name;
            }
        }

        /**
         * Enum to represent the different program starter types.
         *
         * @version 1.0
         * @author Adrian Lange <lange@iig.uni-freiburg.de>
         */
        public static enum ProgramStarterType {

            APPLICATION(1), LINK(2), DIRECTORY(3);

            public final int typeId;

            private ProgramStarterType(int typeId) {
                this.typeId = typeId;
            }
        }
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
