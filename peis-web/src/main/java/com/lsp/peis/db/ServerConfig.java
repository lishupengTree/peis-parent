package com.lsp.peis.db;

import org.aeonbits.owner.Config;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/6 16:37
 */
@Config.Sources({"classpath:db.properties" })
public interface ServerConfig extends Config{

    @Key("db.driverClassName")
    String driverClassName() ;

    @Key("db.url")
    String url();

    @Key("db.username")
    String username();

    @Key("db.password")
    String password();
}
