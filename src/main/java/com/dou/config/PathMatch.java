package com.dou.config;

public class PathMatch {
    private final String path;
    private final String[] dirNames;
    private boolean winFileSeparator;

    public PathMatch(String path) {
        this.path = path;
        if (path.contains("/")) {
            dirNames = path.split("/");
        } else if (path.contains("\\")) {
            winFileSeparator = true;
            dirNames = path.split("\\\\");
        } else {
            dirNames = new String[0];
        }
    }

    public boolean isMatch(String path, boolean isDir) {
        String[] paths;
        if (winFileSeparator) {
            paths = path.split("\\\\");
        } else {
            paths = path.split("/");
        }
        return isMatch(paths, isDir);
    }

    private boolean isMatch(String[] paths, boolean isDir) {
        if (dirNames == null || paths == null) {
            return false;
        }
        if (dirNames.length == 0 || paths.length == 0) {
            return false;
        }
        if (isDir && dirNames.length <= paths.length) {
            return false;
        }
        if (!isDir && dirNames.length != paths.length) {
            return false;
        }
        for (int i = 0; i < paths.length; i++) {
            if (i == paths.length - 1 && i == dirNames.length - 1) {
                if (dirNames[i].contains("*")) {
                    String sub = dirNames[i].substring(dirNames[i].lastIndexOf("*") + 1);
                    return paths[i].endsWith(sub);
                }
            }
            if (dirNames[i].contains("*")) {
                continue;
            }
            if (!dirNames[i].equals(paths[i])) {
                return false;
            }
        }
        return true;
    }
}
