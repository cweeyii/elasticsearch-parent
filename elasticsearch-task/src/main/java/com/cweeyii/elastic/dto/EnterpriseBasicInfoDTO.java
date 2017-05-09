package com.cweeyii.elastic.dto;

import com.cweeyii.annotation.ESParams;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * Created by wenyi on 17/5/9.
 * Email:caowenyi@meituan.com
 */
@ESParams(indexName = "enterprise_info_index_alias", typeName = "enterprise_basic_info")
public class EnterpriseBasicInfoDTO implements Serializable {
    private static final long serialVersionUID = -1L;
    private Long id;
    private String enterpriseName;
    private double longitude;
    private double latitude;
    private String cityName;
    private String districtName;
    private String address;
    private String phone;
    private String businessCategory;
    private String location;
    private Integer valid;

    public Integer getValid() {
        return (int) (id % 2);
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public String getLocation() {
        return latitude + "," + longitude;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
