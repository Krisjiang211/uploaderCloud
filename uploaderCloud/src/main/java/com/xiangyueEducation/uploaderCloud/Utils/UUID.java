package com.xiangyueEducation.uploaderCloud.Utils;

public class UUID {
    public static String getUUID() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        String uuidString = uuid.toString(); // Get the UUID as a string
        return uuidString;
    }
}
