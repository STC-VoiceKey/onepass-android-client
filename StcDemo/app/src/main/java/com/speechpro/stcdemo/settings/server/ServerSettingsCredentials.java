package com.speechpro.stcdemo.settings.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created by Alexander Grigal on 07.02.18.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class ServerSettingsCredentials {

    @JsonProperty(value = "serverUrl", required = true)
    public final String serverUrl;

    @JsonProperty(value = "sessionUrl", required = true)
    public final String sessionUrl;

    @JsonProperty(value = "username", required = true)
    public final String username;

    @JsonProperty(value = "password", required = true)
    public final String password;

    @JsonProperty(value = "domainId", required = true)
    public final String domainId;

    @JsonCreator
    public ServerSettingsCredentials(@JsonProperty(value = "serverUrl", required = true) String serverUrl,
                                     @JsonProperty(value = "sessionUrl", required = true) String sessionUrl,
                                     @JsonProperty(value = "username", required = true) String username,
                                     @JsonProperty(value = "password", required = true) String password,
                                     @JsonProperty(value = "domainId", required = true) String domainId) {
        this.serverUrl = serverUrl;
        this.sessionUrl = sessionUrl;
        this.username = username;
        this.password = password;
        this.domainId = domainId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerSettingsCredentials that = (ServerSettingsCredentials) o;
        return Objects.equals(serverUrl, that.serverUrl) &&
                Objects.equals(sessionUrl, that.sessionUrl) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(domainId, that.domainId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serverUrl, sessionUrl, username, password, domainId);
    }

    @Override
    public String toString() {
        return "ServerSettingsCredentials{" +
                "serverUrl='" + serverUrl + '\'' +
                ", sessionUrl='" + sessionUrl + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", domainId='" + domainId + '\'' +
                '}';
    }
}
