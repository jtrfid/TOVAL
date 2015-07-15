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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Static class to access the Windows Registry. The Windows Registry is the
 * configuration catalogue from Microsoft Windows.
 * </p>
 * <p>
 * It is divided into different <i>hives</i> for different purposes
 * (<code>HKEY_CLASSES_ROOT</code>, <code>HKEY_CURRENT_USER</code>,
 * <code>HKEY_LOCAL_MACHINE</code>, etc.), which contain hierarchically
 * structured <i>keys</i>. Each key can contain <i>values</i>, which have a name
 * and a content.
 * </p>
 * <p>
 * This class contains methods to read and write keys and values in different
 * hives.
 * </p>
 *
 * @version 1.0
 * @author Adrian Lange <lange@iig.uni-freiburg.de>
 */
public final class WindowsRegistry {

    public static final char REG_PATH_SEPARATOR = '\\';
    public static final String REG_PATH_SEPARATOR_REGEX = "\\\\";

    private static final int KEY_READ = 0x20019;
    private static final int KEY_WRITE = 0x20006;

    private static final int ERROR_ACCESS_DENIED = 5;
    private static final int ERROR_FILE_NOT_FOUND = 2;
    private static final int ERROR_SUCCESS = 0;

    private static Throwable initError;

    private WindowsRegistry() {
    }

    private static void checkError(int e) {
        if (e == ERROR_SUCCESS) {
            return;
        }
        if (e == ERROR_FILE_NOT_FOUND) {
            throw new RegistryException("Key not found");
        } else {
            if (e == ERROR_ACCESS_DENIED) {
                throw new RegistryException("Access denied");
            } else {
                throw new RegistryException("Error number " + e, null);
            }
        }
    }

    /**
     * Creates a key. Parent keys in the path will also be created if necessary.
     * This method returns without error if the key already exists.
     *
     * @param keyName Key name (i.a. with parent keys) to be created.
     */
    public static void createKey(String keyName) {
        int[] info = invoke(Methods.REG_CREATE_KEY_EX.get(), keyParts(keyName));
        checkError(info[InfoIndex.INFO_ERROR_CODE.get()]);
        invoke(Methods.REG_CLOSE_KEY.get(), info[InfoIndex.INFO_HANDLE.get()]);
    }

    /**
     * Deletes a key and all values within it. If the key has subkeys, an
     * "Access denied" error will be thrown. Subkeys must be deleted separately.
     *
     * @param keyName Key name to delete.
     */
    public static void deleteKey(String keyName) {
        checkError(invoke(Methods.REG_DELETE_KEY.get(), keyParts(keyName)));
    }

    /**
     * Deletes a value within a key.
     *
     * @param keyName Name of the key, which contains the value to delete.
     * @param valueName Name of the value to delete.
     */
    public static void deleteValue(String keyName, String valueName) {
        try (Key key = Key.open(keyName, KEY_WRITE)) {
            checkError(invoke(Methods.REG_DELETE_VALUE.get(), key.id, toByteArray(valueName)));
        }
    }

    /**
     * Checks if a given key exists.
     *
     * @param keyName Key name to check for existence.
     * @return <code>true</code> if the key exists, otherwise
     * <code>false</code>.
     */
    public static boolean existsKey(String keyName) {
        String[] keyNameParts = keyName.split(REG_PATH_SEPARATOR_REGEX);

        // first part must be valid hive
        if (Hive.getHive(keyNameParts[0]) == null) {
            return false;
        }

        for (int i = 1; i < keyNameParts.length; i++) {
            // build path
            StringBuilder path = new StringBuilder();
            for (int j = 0; j < i; j++) {
                path.append(keyNameParts[j]);
                if (j < i) {
                    path.append(REG_PATH_SEPARATOR);
                }
            }

            // check if next element in path exists
            List<String> subkeys = readSubkeys(path.toString());
            if (!subkeys.contains(keyNameParts[i])) {
                return false;
            }
        }
        return true;
    }

    private static String fromByteArray(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] chars = new char[bytes.length - 1];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) ((int) bytes[i] & 0xFF);
        }
        return new String(chars);
    }

    private static <T> T invoke(Method method, Object... args) {
        if (initError != null) {
            throw new RegistryException("Registry methods are not available", initError);
        }
        try {
            return (T) method.invoke(null, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RegistryException(null, e);
        }
    }

    /**
     * Tells if the Windows registry functions are available.
     *
     * @return <code>true</code> if the Windows Registry is avalable,
     * <code>false</code> otherwise.
     */
    public static boolean isAvailable() {
        return initError == null && WindowsUtils.isWindows();
    }

    /**
     * Splits a path such as HKEY_LOCAL_MACHINE\Software\Microsoft into a pair
     * of values used by the underlying API: An integer hive constant and a byte
     * array of the key path within that hive.
     *
     * @param fullKeyName Key name to split in its single keys.
     * @return Array with the hive key as first element and a following byte
     * array for each key name as second element.
     */
    private static Object[] keyParts(String fullKeyName) {
        int x = fullKeyName.indexOf(REG_PATH_SEPARATOR);
        String hiveName = x >= 0 ? fullKeyName.substring(0, x) : fullKeyName;
        String keyName = x >= 0 ? fullKeyName.substring(x + 1) : "";
        if (Hive.getHive(hiveName) == null) {
            throw new RegistryException("Unknown registry hive: " + hiveName, null);
        }
        Integer hkey = Hive.getHive(hiveName).getId();
        return new Object[]{hkey, toByteArray(keyName)};
    }

    /**
     * Returns a list of the names of all the subkeys of a key.
     *
     * @param keyName Key name to read all subkeys from.
     * @return {@link List} of key names directly contained in the specified
     * key.
     */
    public static List<String> readSubkeys(String keyName) {
        try (Key key = Key.open(keyName, KEY_READ)) {
            int[] info = invoke(Methods.REG_QUERY_INFO_KEY.get(), key.id);
            checkError(info[InfoIndex.INFO_ERROR_CODE.get()]);
            int count = info[InfoIndex.INFO_COUNT_KEYS.get()];
            int maxlen = info[InfoIndex.INFO_MAX_KEY_LENGTH.get()] + 1;
            List<String> subkeys = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                subkeys.add(fromByteArray(invoke(Methods.REG_ENUM_KEY_EX.get(), key.id, i, maxlen)));
            }
            return subkeys;
        }
    }

    /**
     * Reads a string value from the given key and value name.
     *
     * @param keyName Name of the key, which contains the value to read.
     * @param valueName Name of the value to read.
     * @return Content of the specified value.
     */
    public static String readValue(String keyName, String valueName) {
        try (Key key = Key.open(keyName, KEY_READ)) {
            return fromByteArray(invoke(Methods.REG_QUERY_VALUE_EX.get(), key.id, toByteArray(valueName)));
        }
    }

    /**
     * Returns a map of all the name-value pairs in the given key.
     *
     * @param keyName Name of the key to read all values from.
     * @return {@link Map} of value name and value content pairs.
     */
    public static Map<String, String> readValues(String keyName) {
        try (Key key = Key.open(keyName, KEY_READ)) {
            int[] info = invoke(Methods.REG_QUERY_INFO_KEY.get(), key.id);
            checkError(info[InfoIndex.INFO_ERROR_CODE.get()]);
            int count = info[InfoIndex.INFO_COUNT_VALUES.get()];
            int maxlen = info[InfoIndex.INFO_MAX_VALUE_LENGTH.get()] + 1;
            Map<String, String> values = new HashMap<>();
            for (int i = 0; i < count; i++) {
                String valueName = fromByteArray(invoke(Methods.REG_ENUM_VALUE.get(), key.id, i, maxlen));
                values.put(valueName, readValue(keyName, valueName));
            }
            return values;
        }
    }

    /**
     * Conversion of strings to/from null-terminated byte arrays.
     *
     * @param str String to convert to null-terminated byte array.
     * @return Null-terminated byte array.
     */
    private static byte[] toByteArray(String str) {
        byte[] bytes = new byte[str.length() + 1];
        for (int i = 0; i < str.length(); i++) {
            bytes[i] = (byte) str.charAt(i);
        }
        return bytes;
    }

    /**
     * Writes a string value with a given key and value name.
     *
     * @param keyName Name of the key to write the value in.
     * @param valueName Name of the value.
     * @param value Content of the value.
     */
    public static void writeValue(String keyName, String valueName, String value) {
        try (Key key = Key.open(keyName, KEY_WRITE)) {
            checkError(invoke(Methods.REG_SET_VALUE_EX.get(), key.id, toByteArray(valueName), toByteArray(value)));
        }
    }

    /**
     * Mapping of hive names to constants from winreg.h.
     */
    public static enum Hive {

        HKEY_CLASSES_ROOT(0x80000000),
        HKCR(0x80000000),
        HKEY_CURRENT_USER(0x80000001),
        HKCU(0x80000001),
        HKEY_LOCAL_MACHINE(0x80000002),
        HKLM(0x80000002),
        HKEY_USERS(0x80000003),
        HKU(0x80000003),
        HKEY_CURRENT_CONFIG(0x80000005),
        HKCC(0x80000005);

        private final Integer id;

        private Hive(Integer id) {
            this.id = id;
        }

        public static Integer get(Hive hive) {
            return hive.getId();
        }

        public static Hive getHive(String name) {
            for (Hive h : Hive.values()) {
                if (h.toString().toLowerCase().equals(name.toLowerCase())) {
                    return h;
                }
            }
            return null;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return this.toString();
        }
    }

    /**
     * Enumeration type encapsulating info array indexes.
     */
    private static enum InfoIndex {

        INFO_COUNT_KEYS(0),
        INFO_COUNT_VALUES(2),
        INFO_ERROR_CODE(1),
        INFO_HANDLE(0),
        INFO_MAX_KEY_LENGTH(3),
        INFO_MAX_VALUE_LENGTH(4);

        private int num;

        private InfoIndex(int num) {
            this.num = num;
        }

        public int get() {
            return num;
        }
    }

    /**
     * Enumeration type for the different methods to access the Windows
     * Registry.
     */
    private static enum Methods {

        REG_CLOSE_KEY("WindowsRegCloseKey", int.class),
        REG_CREATE_KEY_EX("WindowsRegCreateKeyEx", int.class, byte[].class),
        REG_DELETE_KEY("WindowsRegDeleteKey", int.class, byte[].class),
        REG_DELETE_VALUE("WindowsRegDeleteValue", int.class, byte[].class),
        REG_ENUM_KEY_EX("WindowsRegEnumKeyEx", int.class, int.class, int.class),
        REG_ENUM_VALUE("WindowsRegEnumValue", int.class, int.class, int.class),
        REG_OPEN_KEY("WindowsRegOpenKey", int.class, byte[].class, int.class),
        REG_QUERY_VALUE_EX("WindowsRegQueryValueEx", int.class, byte[].class),
        REG_QUERY_INFO_KEY("WindowsRegQueryInfoKey", int.class),
        REG_SET_VALUE_EX("WindowsRegSetValueEx", int.class, byte[].class, byte[].class);

        private Method method;

        private Methods(String methodName, Class<?>... parameterTypes) {
            try {
                Method m = java.util.prefs.Preferences.systemRoot().getClass().getDeclaredMethod(methodName, parameterTypes);
                m.setAccessible(true);
                method = m;
            } catch (NoSuchMethodException | SecurityException t) {
                initError = t;
            }
        }

        /**
         * Returns the method.
         *
         * @return The method
         */
        public Method get() {
            return method;
        }
    }

    /**
     * Type encapsulating a native handle to a registry key.
     */
    private static class Key implements AutoCloseable {

        final int id;

        private Key(int id) {
            this.id = id;
        }

        static Key open(String keyName, int accessMode) {
            Object[] keyParts = keyParts(keyName);
            int[] ret = invoke(Methods.REG_OPEN_KEY.get(), keyParts[0], keyParts[1], accessMode);
            checkError(ret[InfoIndex.INFO_ERROR_CODE.get()]);
            return new Key(ret[InfoIndex.INFO_HANDLE.get()]);
        }

        @Override
        public void close() {
            invoke(Methods.REG_CLOSE_KEY.get(), id);
        }
    }

    /**
     * The exception type that will be thrown if a registry operation fails.
     */
    public static class RegistryException extends RuntimeException {

        public RegistryException(String message) {
            super(message);
        }

        public RegistryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
