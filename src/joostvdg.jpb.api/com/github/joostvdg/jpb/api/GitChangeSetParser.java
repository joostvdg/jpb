package com.github.joostvdg.jpb.api;

public interface GitChangeSetParser {
    /**
     * Will parse the change list string to extract the folders that have changed.
     * It will ignore any changed folder not starting with the changeRoot.
     *
     * Input for the changeList is expected to be file paths separated by '\n'
     * To get such a change list:
     * <pre>
     *     git diff-tree --no-commit-id --name-only -r ${GIT_COMMIT_HASH}
     * </pre>
     *
     * @param changeList git change list
     * @param changeRoot the root folder of the changes you want to parse
     * @return array of the folders inside the changeRoot that have changed
     */
    String[] extractChangeSetFolders(String changeList, String changeRoot);
}
