package com.github.joostvdg.jpb.core;


import com.github.joostvdg.jpb.api.GitChangeSetParser;

import java.util.*;

public class ParseChangeList implements GitChangeSetParser {

    @Override
    public String[] extractChangeSetFolders(String gitChangeSet, String changeRoot) {
        if (gitChangeSet == null || gitChangeSet.trim().equals("")) {
            return new String[0];
        }

        Set<String> changeSetFolders = new HashSet<>();
        gitChangeSet = gitChangeSet.replace("\\n", "XXX");
        String[] changeSetItems = gitChangeSet.split("XXX");

        for (String changeSetItem : changeSetItems) {
            if (!changeSetItem.startsWith(changeRoot)) {
                continue; // it's not within scope
            }
            changeSetItem = changeSetItem.replace(changeRoot, "");
            if (!changeSetItem.contains("/")) {
                continue; // it's likely it was a change to the folder's permissions, no file
            }
            changeSetItem = changeSetItem.substring(0, changeSetItem.indexOf("/"));
            changeSetFolders.add(changeSetItem);
        }
        String[] changeSetFoldersArray = new String[changeSetFolders.size()];
        changeSetFolders.toArray(changeSetFoldersArray);
        Arrays.sort(changeSetFoldersArray);
        return changeSetFoldersArray;
    }
}
