package de.invation.code.toval.types;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.invation.code.toval.validate.ParameterException.ErrorCode;

public enum DataUsage {

        READ("R"), WRITE("W"), CREATE("C"), DELETE("D");

        private final String abbreviation;

        private DataUsage(String abbreviation) {
                this.abbreviation = abbreviation;
        }

        public final String abbreviation() {
                return abbreviation;
        }

        public static DataUsage fromAbbreviation(String abbreviation) {
                switch (abbreviation) {
                        case "R":
                                return READ;
                        case "W":
                                return WRITE;
                        case "C":
                                return CREATE;
                        case "D":
                                return DELETE;
                        default:
                                return null;
                }
        }

        public static DataUsage parse(String dataUsageString) throws ParameterException {
                Validate.notNull(dataUsageString);
                Validate.notEmpty(dataUsageString);

                for (DataUsage usageMode : DataUsage.values()) {
                        if (dataUsageString.toUpperCase().equals(usageMode.toString())) {
                                return usageMode;
                        }
                }

                throw new ParameterException(ErrorCode.INCOMPATIBILITY);
        }

}
