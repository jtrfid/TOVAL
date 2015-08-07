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

import static de.invation.code.toval.os.OSUtils.getUserHomeDirectory;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p>
 * Class to represent a Linux program starter, which is indicated by the file
 * extension ".desktop". A program starter creates an icon in the application
 * menu and links it with the application.
 * </p>
 * <p>
 * To install the program starter, it must be put into
 * <code>~/.local/share/applications/</code> (user) or
 * <code>/usr/share/applications/</code> (system) and, depending on the
 * operating system, must be made executable.
 * </p>
 * <p>
 * To create a desktop icon, the program starter must also be put on the
 * desktop.
 * </p>
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public class LinuxProgramStarter {

    private String fileName = null;

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
    private final Set<LinuxUtils.LinuxDesktopEnvironments> attrOnlyShowIn = new HashSet<>();
    // NotShowIn
    private final Set<LinuxUtils.LinuxDesktopEnvironments> attrNotShowIn = new HashSet<>();
    // MimeType
    private final Set<String> attrMimeType = new HashSet<>();
    // StartupNotify
    private boolean attrStartupNotify = false;
    // StartupWMClass
    private String attrStartupWMClass = null;

    /**
     * Creates a new program starter.
     *
     * @param fileName Name of the program starter file, e.g.
     * <code><i>fileName</i>.desktop</code>
     * @param type Type of the program starter
     * @param name Name of the program to start
     * @param exec Command to execute to start the program
     */
    public LinuxProgramStarter(String fileName, ProgramStarterType type, String name, String exec) {
        setFileName(fileName);
        setType(type);
        setName(name);
        setExec(exec);
    }

    /**
     * Installs the program starter to <code>~/.local/share/applications/</code>
     * and makes it executable.
     *
     * @param overwrite Set <code>true</code> if an already existing program
     * starter with the same name should be overwritten, otherwise
     * <code>false</code>.
     * @throws OSException
     */
    public final void install(boolean overwrite) throws OSException {
        File programStarterDirectory = new File(getUserHomeDirectory() + "/.local/share/applications/");
        if (!programStarterDirectory.exists() || !programStarterDirectory.canWrite()) {
            throw new OSException("Can't write to directory \"" + programStarterDirectory.getAbsolutePath() + "\".");
        }
        File programStarterFile = new File(programStarterDirectory.getAbsolutePath() + "/" + getFileName() + ".desktop");
        if (programStarterFile.exists()) {
            if (overwrite) {
                programStarterFile.delete();
            } else {
                throw new OSException("File \"" + programStarterFile.getName() + "\" already exists.");
            }
        }

        try {
            // write program starter
            PrintWriter out;
            out = new PrintWriter(programStarterFile.getAbsolutePath());
            out.print(this.toString());
            out.close();

            // make it executable
            Runtime.getRuntime().exec("chmod a+x " + programStarterFile.getAbsolutePath());
        } catch (IOException ex) {
            throw new OSException(ex);
        }
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the attrType
     */
    public ProgramStarterType getType() {
        return attrType;
    }

    /**
     * @return the attrExec
     */
    public String getExec() {
        return attrExec;
    }

    /**
     * @return the attrTryExec
     */
    public String getTryExec() {
        return attrTryExec;
    }

    /**
     * @return the attrIcon
     */
    public String getIcon() {
        return attrIcon;
    }

    /**
     * @return the attrCategories
     */
    public Set<ProgramStarterCategories> getCategories() {
        return attrCategories;
    }

    /**
     * @return the attrName
     */
    public String getName() {
        return attrName;
    }

    /**
     * @return the attrNameLang
     */
    public Map<String, String> getNameLang() {
        return attrNameLang;
    }

    /**
     * @return the attrGenericName
     */
    public String getGenericName() {
        return attrGenericName;
    }

    /**
     * @return the attrGenericNameLang
     */
    public Map<String, String> getGenericNameLang() {
        return attrGenericNameLang;
    }

    /**
     * @return the attrComment
     */
    public String getComment() {
        return attrComment;
    }

    /**
     * @return the attrCommentLang
     */
    public Map<String, String> getCommentLang() {
        return attrCommentLang;
    }

    /**
     * @return the attrTerminal
     */
    public boolean isTerminal() {
        return attrTerminal;
    }

    /**
     * @return the attrNoDisplay
     */
    public boolean isNoDisplay() {
        return attrNoDisplay;
    }

    /**
     * @return the attrPath
     */
    public String getPath() {
        return attrPath;
    }

    /**
     * @return the attrKeywords
     */
    public Set<String> getKeywords() {
        return attrKeywords;
    }

    /**
     * @return the attrOnlyShowIn
     */
    public Set<LinuxUtils.LinuxDesktopEnvironments> getOnlyShowIn() {
        return attrOnlyShowIn;
    }

    /**
     * @return the attrNotShowIn
     */
    public Set<LinuxUtils.LinuxDesktopEnvironments> getNotShowIn() {
        return attrNotShowIn;
    }

    /**
     * @return the attrMimeType
     */
    public Set<String> getMimeType() {
        return attrMimeType;
    }

    /**
     * @return the attrStartupNotify
     */
    public boolean isStartupNotify() {
        return attrStartupNotify;
    }

    /**
     * @return the attrStartupWMClass
     */
    public String getStartupWMClass() {
        return attrStartupWMClass;
    }

    /**
     * The file name must start with any word character, followed by arbitrary
     * word characters and digits: <code>[a-z]+[a-z0-9]*</code>.
     *
     * @param fileName the fileName to set
     */
    public final void setFileName(String fileName) {
        Validate.notNull(fileName);

        if (Pattern.matches("[a-z]+[a-z0-9]*", fileName)) {
            this.fileName = fileName;
        } else {
            throw new ParameterException("");
        }
    }

    /**
     * @param attrType the attrType to set
     */
    public final void setType(ProgramStarterType attrType) {
        Validate.notNull(attrType);
        this.attrType = attrType;
    }

    /**
     * @param attrExec the attrExec to set
     */
    public final void setExec(String attrExec) {
        Validate.notNull(attrExec);
        Validate.notEmpty(attrExec);
        this.attrExec = attrExec;
    }

    /**
     * @param attrTryExec the attrTryExec to set
     */
    public void setTryExec(String attrTryExec) {
        this.attrTryExec = attrTryExec;
    }

    /**
     * @param attrIcon the attrIcon to set
     */
    public void setIcon(String attrIcon) {
        this.attrIcon = attrIcon;
    }

    /**
     * @param category the category to add
     */
    public void addCategory(ProgramStarterCategories category) {
        attrCategories.add(category);
    }

    /**
     * @param attrName the attrName to set
     */
    public final void setName(String attrName) {
        Validate.notNull(attrName);
        Validate.notEmpty(attrName);
        this.attrName = attrName;
    }

    /**
     * @param langIdentificator the language identificator (de, en, ...)
     * @param name the name to set
     * @throws OSException
     */
    public void addNameLang(String langIdentificator, String name) throws OSException {
        langIdentificator = sanitizeLangIdentificator(langIdentificator);
        attrNameLang.put(langIdentificator, name);
    }

    /**
     * @param attrGenericName the attrGenericName to set
     */
    public void setGenericName(String attrGenericName) {
        this.attrGenericName = attrGenericName;
    }

    /**
     * @param langIdentificator the language identificator (de, en, ...)
     * @param name the name to add
     * @throws OSException
     */
    public void addGenericNameLang(String langIdentificator, String name) throws OSException {
        langIdentificator = sanitizeLangIdentificator(langIdentificator);
        attrGenericNameLang.put(langIdentificator, name);
    }

    /**
     * @param attrComment the attrComment to set
     */
    public void setComment(String attrComment) {
        this.attrComment = attrComment;
    }

    /**
     * @param langIdentificator the language identificator (de, en, ...)
     * @param comment the comment to set
     * @throws OSException
     */
    public void addCommentLang(String langIdentificator, String comment) throws OSException {
        langIdentificator = sanitizeLangIdentificator(langIdentificator);
        attrCommentLang.put(langIdentificator, comment);
    }

    /**
     * @param attrTerminal the attrTerminal to set
     */
    public void setTerminal(boolean attrTerminal) {
        this.attrTerminal = attrTerminal;
    }

    /**
     * @param attrNoDisplay the attrNoDisplay to set
     */
    public void setNoDisplay(boolean attrNoDisplay) {
        this.attrNoDisplay = attrNoDisplay;
    }

    /**
     * @param attrPath the attrPath to set
     */
    public void setPath(String attrPath) {
        this.attrPath = attrPath;
    }

    /**
     * @param attrKeyword the keyword to add
     */
    public void addKeywords(String attrKeyword) {
        attrKeywords.add(attrKeyword);
    }

    /**
     * @param linuxDesktopEnvironment the desktop environment to add
     */
    public void addOnlyShowIn(LinuxUtils.LinuxDesktopEnvironments linuxDesktopEnvironment) {
        attrOnlyShowIn.add(linuxDesktopEnvironment);
    }

    /**
     * @param linuxDesktopEnvironment the desktop environment to add
     */
    public void addNotShowIn(LinuxUtils.LinuxDesktopEnvironments linuxDesktopEnvironment) {
        attrNotShowIn.add(linuxDesktopEnvironment);
    }

    /**
     * @param mimeType the MIME type to add
     * @throws OSException
     */
    public void addMimeType(String mimeType) throws OSException {
        if (!Pattern.matches(LinuxMIMEDatabase.MIME_PATTERN.pattern(), mimeType)) {
            throw new OSException("Invalid MIME type \"" + mimeType + "\".");
        }
        attrMimeType.add(mimeType);
    }

    /**
     * @param attrStartupNotify the attrStartupNotify to set
     */
    public void setStartupNotify(boolean attrStartupNotify) {
        this.attrStartupNotify = attrStartupNotify;
    }

    /**
     * @param attrStartupWMClass the attrStartupWMClass to set
     */
    public void setStartupWMClass(String attrStartupWMClass) {
        this.attrStartupWMClass = attrStartupWMClass;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("[Desktop Entry]").append(LinuxUtils.LINUX_LINE_SEPARATOR);

        // type
        s.append("Type=").append(getType().name).append(LinuxUtils.LINUX_LINE_SEPARATOR);

        // name
        s.append("Name=").append(getName()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
        for (Map.Entry<String, String> name : getNameLang().entrySet()) {
            s.append("Name[").append(name.getKey()).append("]=").append(name.getValue()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // exec
        s.append("Exec=").append(getExec()).append(LinuxUtils.LINUX_LINE_SEPARATOR);

        // icon
        if (getIcon() != null) {
            s.append("Icon=").append(getIcon()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // Comment
        if (getComment() != null) {
            s.append("Comment=").append(getComment()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }
        if (getCommentLang().size() > 0) {
            for (Map.Entry<String, String> comment : getCommentLang().entrySet()) {
                s.append("Comment[").append(comment.getKey()).append("]=").append(comment.getValue()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
            }
        }

        // Categories
        if (getCategories().size() > 0) {
            s.append("Categories=");
            for (ProgramStarterCategories cat : getCategories()) {
                s.append(cat.name).append(";");
            }
            s.append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // Path
        if (getPath() != null) {
            s.append("Path=").append(getPath()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // Keywords
        if (getKeywords().size() > 0) {
            s.append("Keywords=");
            for (String keyword : getKeywords()) {
                s.append(keyword).append(";");
            }
            s.append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // TryExec
        if (getTryExec() != null) {
            s.append("TryExec=").append(getTryExec()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // Terminal
        s.append("Terminal=").append(isTerminal()).append(LinuxUtils.LINUX_LINE_SEPARATOR);

        // GenericName
        if (getGenericName() != null) {
            s.append("GenericName=").append(getGenericName()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }
        if (getGenericNameLang().size() > 0) {
            for (Map.Entry<String, String> name : getGenericNameLang().entrySet()) {
                s.append("GenericName[").append(name.getKey()).append("]=").append(name.getValue()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
            }
            s.append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // NoDisplay
        s.append("NoDisplay=").append(isNoDisplay()).append(LinuxUtils.LINUX_LINE_SEPARATOR);

        // OnlyShowIn
        if (getOnlyShowIn().size() > 0) {
            s.append("OnlyShowIn=");
            for (LinuxUtils.LinuxDesktopEnvironments env : getOnlyShowIn()) {
                s.append(env.name).append(";");
            }
            s.append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // NotShowIn
        if (getNotShowIn().size() > 0) {
            s.append("NotShowIn=");
            for (LinuxUtils.LinuxDesktopEnvironments env : getNotShowIn()) {
                s.append(env.name).append(";");
            }
            s.append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // MimeType
        if (getMimeType().size() > 0) {
            s.append("MimeType=");
            for (String type : getMimeType()) {
                s.append(type).append(";");
            }
            s.append(LinuxUtils.LINUX_LINE_SEPARATOR);
        }

        // StartupNotify
        s.append("StartupNotify=").append(isStartupNotify()).append(LinuxUtils.LINUX_LINE_SEPARATOR);

        // StartupWMClass
        if (getStartupWMClass() != null) {
            s.append("StartupWMClass=").append(getStartupWMClass()).append(LinuxUtils.LINUX_LINE_SEPARATOR);
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
     * <p>
     * Enum to represent the different program categories. The categories have
     * three different types:
     * </p>
     * <ol>
     * <li><i>Main Categories</i> consists of those categories that every
     * conforming desktop environment must support.</li>
     * <li><i>Additional Categories</i> provides categories that can be used to
     * provide more fine grained information about the application.</li>
     * <li><i>Reserved Categories</i> containes categories that have a
     * desktop-specific meaning.</li>
     * </ol>
     *
     * @version 1.0
     * @author Adrian Lange <lange@iig.uni-freiburg.de>
     */
    public static enum ProgramStarterCategories {

        AUDIOVIDEO(1, "AudioVideo"), AUDIO(1, "Audio"), VIDEO(1, "Video"),
        DEVELOPMENT(1, "Development"), EDUCATION(1, "Education"),
        GAME(1, "Game"), GRAPHICS(1, "Graphics"), NETWORK(1, "Networks"),
        OFFICE(1, "Office"), SCIENCE(1, "Science"), SETTINGS(1, "Settings"),
        SYSTEM(1, "System"), UTILITY(1, "Utility"),
        BUILDING(2, "Building"), DEBUGGER(2, "Debugger"), IDE(2, "IDE"),
        GUIDESIGNER(2, "GUIDesigner"), PROFILING(2, "Profiling"),
        REVISIONCONTROL(2, "RevisionControl"), TRANSLATION(2, "Translation"),
        CALENDAR(2, "Calendar"), CONTACTMANAGEMENT(2, "ContactManagement"),
        DATABASE(2, "Database"), DICTIONARY(2, "Dictionary"), CHART(2, "Chart"),
        EMAIL(2, "Email"), FINANCE(2, "Finance"), FLOWCHART(2, "FlowChart"),
        PDA(2, "PDA"), PROJECTMANAGEMENT(2, "ProjectManagement"),
        PRESENTATION(2, "Presentation"), SPREADSHEET(2, "Spreadsheet"),
        WORDPROCESSOR(2, "WordProcessor"), TWODGRAPHICS(2, "2DGraphics"),
        VECTORGRAPHICS(2, "VectorGraphics"), RASTERGRAPHICS(2, "RasterGraphics"),
        THREEDGRAPHICS(2, "3DGraphics"), SCANNING(2, "Scanning"), OCR(2, "OCR"),
        PHOTOGRAPHY(2, "Photography"), PUBLISHING(2, "Publishing"),
        VIEWER(2, "Viewer"), TEXTTOOLS(2, "TextTools"),
        DESKTOPSETTINGS(2, "DesktopSettings"),
        HARDWARESETTINGS(2, "HardwareSettings"), PRINTING(2, "Printing"),
        PACKAGEMANAGER(2, "PackageManager"), DIALUP(2, "Dialup"),
        INSTANTMESSAGING(2, "InstantMessaging"), CHAT(2, "Chat"),
        IRCCLIENT(2, "IRCClient"), FEED(2, "Feed"),
        FILETRANSFER(2, "FileTransfer"), HAMRADIO(2, "HamRadio"),
        NEWS(2, "News"), P2P(2, "P2P"), REMOTEACCESS(2, "RemoteAccess"),
        TELEPHONY(2, "Telephony"), TELEPHONYTOOLS(2, "TelephonyTools"),
        VIDEOCONFERENCE(2, "VideoConference"), WEBBROWSER(2, "WebBrowser"),
        WEBDEVELOPMENT(2, "WebDevelopment"), MIDI(2, "Midi"), MIXER(2, "Mixer"),
        SEQUENCER(2, "Sequencer"), TUNER(2, "Tuner"), TV(2, "TV"),
        AUDIOVIDEOEDITING(2, "AudioVideoEditing"), PLAYER(2, "Player"),
        RECORDER(2, "Recorder"), DISCBURNING(2, "DiscBurning"),
        ACTIONGAME(2, "ActionGame"), ADVENTUREGAME(2, "AdventureGame"),
        ARCADEGAME(2, "ArcadeGame"), BOARDGAME(2, "BoardGame"),
        BLOCKSGAME(2, "BlocksGame"), CARDGAME(2, "CardGame"),
        KIDSGAME(2, "KidsGame"), LOGICGAME(2, "LogicGame"),
        ROLEPLAYING(2, "RolePlaying"), SHOOTER(2, "Shooter"),
        SIMULATION(2, "Simulation"), SPORTSGAME(2, "SportsGame"),
        STRATEGYGAME(2, "StrategyGame"), ART(2, "Art"),
        CONSTRUCTION(2, "Construction"), MUSIC(2, "Music"),
        LANGUAGES(2, "Languages"),
        ARTIFICIALINTELLIGENCE(2, "ArtificialIntelligence"),
        ASTRONOMY(2, "Astronomy"), BIOLOGY(2, "Biology"),
        CHEMISTRY(2, "Chemistry"), COMPUTERSCIENCE(2, "ComputerScience"),
        DATAVISUALIZATION(2, "DataVisualization"), ECONOMY(2, "Economy"),
        ELECTRICITY(2, "Electricity"), GEOGRAPHY(2, "Geography"),
        GEOLOGY(2, "Geology"), GEOSCIENCE(2, "Geoscience"),
        HISTORY(2, "History"), HUMANITIES(2, "Humanities"),
        IMAGEPROCESSING(2, "ImageProcessing"), LITERATURE(2, "Literature"),
        MAPS(2, "Maps"), MATH(2, "Math"),
        NUMERICALANALYSIS(2, "NumericalAnalysis"),
        MEDICALSOFTWARE(2, "MedicalSoftware"), PHYSICS(2, "Physics"),
        ROBOTICS(2, "Robotics"), SPIRITUALITY(2, "Spirituality"),
        SPORTS(2, "Sports"), PARALLELCOMPUTING(2, "ParallelComputing"),
        AMUSEMENT(2, "Amusement"), ARCHIVING(2, "Archiving"),
        COMPRESSION(2, "Compression"), ELECTRONICS(2, "Electronics"),
        EMULATOR(2, "Emulator"), ENGINEERING(2, "Engineering"),
        FILETOOLS(2, "FileTools"), FILEMANAGER(2, "FileManager"),
        TERMINALEMULATOR(2, "TerminalEmulator"), FILESYSTEM(2, "Filesystem"),
        MONITOR(2, "Monitor"), SECURITY(2, "Security"),
        ACCESSIBILITY(2, "Accessibility"), CALCULATOR(2, "Calculator"),
        CLOCK(2, "Clock"), TEXTEDITOR(2, "TextEditor"),
        DOCUMENTATION(2, "Documentation"), ADULT(2, "Adult"), CORE(2, "Core"),
        KDE(2, "KDE"), GNOME(2, "GNOME"), XFCE(2, "XFCE"), GTK(2, "GTK"),
        QT(2, "Qt"), MOTIF(2, "Motif"), JAVA(2, "Java"),
        CONSOLEONLY(2, "ConsoleOnly"),
        SCREENSAVER(3, "Screensaver"), TRAYICON(3, "TrayIcon"),
        APPLET(3, "Applet"), SHELL(3, "Shell");

        public final int catTypeId;
        public final String name;

        private ProgramStarterCategories(int catTypeId, String name) {
            this.catTypeId = catTypeId;
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

        APPLICATION(1, "Application"), LINK(2, "Link"), DIRECTORY(3, "Directory");

        public final int typeId;
        public final String name;

        private ProgramStarterType(int typeId, String name) {
            this.typeId = typeId;
            this.name = name;
        }
    }
}
