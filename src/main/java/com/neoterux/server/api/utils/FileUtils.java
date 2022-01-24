package com.neoterux.server.api.utils;

import java.io.File;

public final class FileUtils {


    public static boolean hasExtension(File file, String... exts) {
        String[] s = file.getName().split("\\.");
        int len = s.length;
        if (len > 1)
            for (String ext : exts) {
                if (s[len - 1].equalsIgnoreCase(ext))
                    return true;
            }

        return false;
    }
}
