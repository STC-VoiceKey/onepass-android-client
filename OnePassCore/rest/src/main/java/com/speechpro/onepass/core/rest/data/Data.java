package com.speechpro.onepass.core.rest.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Data representation. Some data contains into byte array.
 *
 * @author volobuev
 * @since 25.09.2015.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class Data {

    @JsonProperty(value = "data", required = true)
    public final byte[] data;

    @JsonCreator
    public Data(@JsonProperty(value = "data", required = true) byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Data)) {
            return false;
        }
        Data data1 = (Data) o;
        return Objects.equal(data, data1.data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("data", data).toString();
    }
}
