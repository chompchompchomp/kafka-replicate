package xyz.matt.replicate.core.people;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "number",
        "street"
})
public class Address {

    @JsonProperty("number")
    private Integer number;
    @JsonProperty("street")
    private String street;

    /**
     * @return The number
     */
    @JsonProperty("number")
    public Integer getNumber() {
        return number;
    }

    /**
     * @param number The number
     */
    @JsonProperty("number")
    public void setNumber(Integer number) {
        this.number = number;
    }

    public Address withNumber(Integer number) {
        this.number = number;
        return this;
    }

    /**
     * @return The street
     */
    @JsonProperty("street")
    public String getStreet() {
        return street;
    }

    /**
     * @param street The street
     */
    @JsonProperty("street")
    public void setStreet(String street) {
        this.street = street;
    }

    public Address withStreet(String street) {
        this.street = street;
        return this;
    }
}