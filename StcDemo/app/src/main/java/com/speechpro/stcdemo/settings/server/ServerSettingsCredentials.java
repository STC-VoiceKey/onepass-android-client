package com.speechpro.stcdemo.settings.server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Alexander Grigal on 07.02.18.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class ServerSettingsCredentials {

    @JsonProperty(value = "url", required = true)
    public final String url;

    @JsonProperty(value = "username", required = true)
    public final String username;

    @JsonProperty(value = "password", required = true)
    public final String password;

    @JsonProperty(value = "domainId", required = true)
    public final String domainId;

    @JsonCreator
    public ServerSettingsCredentials(@JsonProperty(value = "url", required = true) String url,
                                     @JsonProperty(value = "username", required = true) String username,
                                     @JsonProperty(value = "password", required = true) String password,
                                     @JsonProperty(value = "domainId", required = true) String domainId) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.domainId = domainId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerSettingsCredentials that = (ServerSettingsCredentials) o;

        if (!url.equals(that.url)) return false;
        if (!username.equals(that.username)) return false;
        if (!password.equals(that.password)) return false;
        return domainId.equals(that.domainId);
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + domainId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ServerSettingsCredentials{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", domainId='" + domainId + '\'' +
                '}';
    }
}
