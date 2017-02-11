package org.codelogger.homepage.bean;

import static org.codelogger.utils.ExceptionUtils.iae;
import static org.codelogger.utils.StringUtils.isBlank;

public class Version implements Comparable<Version> {

    private Long[] versionArray;

    public Version(String version) {

        iae.throwIfTrue(isBlank(version), "Argument version can not be blank.");
        String[] stringVersionArrays = version.split("\\.");
        versionArray = new Long[stringVersionArrays.length];
        for (int i = 0; i < stringVersionArrays.length; i++) {
            versionArray[i] = Long.valueOf(stringVersionArrays[i]);
        }
    }

    public int compareTo(Version version) {

        iae.throwIfNull(version, "Argument version can not be blank.");
        Long[] o2Versions = version.versionArray;
        int versionLength = versionArray.length;
        iae.throwIfTrue(versionLength != o2Versions.length, "o1 and o2 version pattern not matched.");
        for (int i = 0; i < versionLength; i++) {
            if (versionArray[i].equals(o2Versions[i])) {
                continue;
            }
            return versionArray[i].compareTo(o2Versions[i]);
        }
        return 0;
    }

}
