package com.github.joostvdg.jpb.core.test;

import com.github.joostvdg.jpb.core.ParseChangeList;

public class ParseChangeListTest {

    private static final String CHANGE_ROOT = "cb/aws-eks/";
    private static ParseChangeList parseChangeList;

    public static void main(String[] args) {
        parseChangeList = new ParseChangeList();
        try {
            TestSingleFolder();
        } catch (Exception e) {
            System.out.println("Found error in TestSingleFolder: " + e.getMessage());
        }
        try {
            TestMultipleFolders();
        } catch (Exception e) {
            System.out.println("Found error in TestMultipleFolders: " + e.getMessage());
        }
        try {
            TestManyFolders();
        } catch (Exception e) {
            System.out.println("Found error in TestManyFolders: " + e.getMessage());
        }
    }

    public static void TestSingleFolder() {
        String single = "cb/aws-eks/cat-nip/image-values.yml";
        String expected = "cat-nip";
        String[] actual = parseChangeList.extractChangeSetFolders(single, CHANGE_ROOT);
        assert actual.length == 1;
        assert actual[0].equals(expected);
        System.out.println("TestSingleFolder - Success");
        System.out.println("Expected:" + expected +", Actual: " + actual[0]);
    }

    public static void TestMultipleFolders() {
        String multiple = "cb/aws-eks/build-namespace.yml\n" +
            "cb/aws-eks/cat-nip/certificate.yml";
        String expected = "cat-nip";

        String[] actual = parseChangeList.extractChangeSetFolders(multiple, CHANGE_ROOT);
        assert actual.length == 1;
        assert actual[0].equals(expected);
        System.out.println("TestMultipleFolders - Success");
        System.out.println("Expected:" + expected +", Actual: " + actual[0]);
    }

    public static void TestManyFolders() {
        String many = "cb/aws-eks/build-namespace.yml\n" +
            "cb/cat-nip/certificate.yml\n" +
            "cb/aws-eks/cat-nip/install.sh\n" +
            "cb/aws-eks/cat-nip/values.yml\n" +
            "cb/aws-eks/help/cat-namespace.yml\n" +
            "cb/aws-eks/cat-ns/install.sh\n" +
            "cb/aws-eks/fakeleintermediatex1.pem\n" +
            "cb/aws-eks/issuer.yml\n" +
            "cb/aws-eks/prod.yml";
        String[] expected = new String[]{"cat-nip", "cat-ns"};
        String[] actual = parseChangeList.extractChangeSetFolders(many, CHANGE_ROOT);
        assert actual.length == 2;
        assert expected[0].equals(actual[0]);
        assert expected[1].equals(actual[1]);

        System.out.println("TestManyFolders - Success");

    }
}
