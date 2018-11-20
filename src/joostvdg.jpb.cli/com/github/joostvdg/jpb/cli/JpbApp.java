package com.github.joostvdg.jpb.cli;


import com.github.joostvdg.jpb.api.GitChangeSetParser;

import java.util.ServiceLoader;

public class JpbApp {

    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            System.out.println("Not enough parameters, exiting");
            return;
        }


        String command = args[0];
        switch (command) {
            case "GitChangeListToFolder":
                GitChangeSetParser gitChangeSetParser = retrieveGitChangeSetParser();
                if (args.length < 3) {
                    System.out.println("Not enough parameters for GitChangeListToFolder, exiting");
                    System.exit(1);
                }
                String changeSetList = args[1];
                String changeRoot = args[2];
                String[] parseResponse = gitChangeSetParser.extractChangeSetFolders(changeSetList, changeRoot);
                StringBuilder response = new StringBuilder();
                boolean isFirst = true;
                for (String item : parseResponse) {
                    if(isFirst) {
                        isFirst = false;
                    } else {
                        response.append(",");
                    }
                    response.append(item);
                }
                System.out.println(response.toString());
                break;
            default:
                System.out.println("No such command: " + command);
                System.exit(1);
        }
    }

    private static GitChangeSetParser retrieveGitChangeSetParser() {
        ServiceLoader<GitChangeSetParser> parsers = ServiceLoader.load(GitChangeSetParser.class);
        GitChangeSetParser parser = parsers.findFirst().isPresent() ? parsers.findFirst().get() : null;
        if (parser == null) {
            System.err.println("Did not find any parsers, quiting");
            System.exit(1);
        }
        return parser;
    }
}
