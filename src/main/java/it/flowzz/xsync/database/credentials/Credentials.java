package it.flowzz.xsync.database.credentials;

import lombok.Builder;
import lombok.Data;

/**
 * Class representing credentials
 */
@Data
@Builder
public class Credentials {

    private String hostname;
    private String username;
    private String password;
    private String database;
    private int port;
}
