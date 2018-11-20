module joostvdg.jpb.core {
    requires joostvdg.jpb.api;

    provides com.github.joostvdg.jpb.api.GitChangeSetParser with com.github.joostvdg.jpb.core.ParseChangeList;
    exports com.github.joostvdg.jpb.core to joostvdg.jpb.core.test;

}
