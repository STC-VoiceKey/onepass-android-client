package com.speechpro.onepass.framework.model.data;

import com.google.common.base.Objects;

/**
 * Created by alexander on 25.09.17.
 */

public class Device {
    private String type;
    private String api;
    private String device;
    private String brand;
    private String model;
    private String product;
    private String resolution;

    public Device(String type, String api, String device, String brand, String model,
                  String product, String resolution) {
        this.type = type;
        this.api = api;
        this.device = device;
        this.brand = brand;
        this.model = model;
        this.product = product;
        this.resolution = resolution;
    }

    public String getType() {
        return type;
    }

    public String getApi() {
        return api;
    }

    public String getDevice() {
        return device;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getProduct() {
        return product;
    }

    public String getResolution() {
        return resolution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device1 = (Device) o;
        return Objects.equal(type, device1.type) &&
                Objects.equal(api, device1.api) &&
                Objects.equal(device, device1.device) &&
                Objects.equal(brand, device1.brand) &&
                Objects.equal(model, device1.model) &&
                Objects.equal(product, device1.product) &&
                Objects.equal(resolution, device1.resolution);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type, api, device, brand, model, product, resolution);
    }

    @Override
    public String toString() {
        return "Device{" +
                "type='" + type + '\'' +
                ", api='" + api + '\'' +
                ", device='" + device + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", product='" + product + '\'' +
                ", resolution='" + resolution + '\'' +
                '}';
    }
}
