package kr.movements.smv2.dto;

import software.amazon.ion.Decimal;

import java.math.BigDecimal;

public interface SiteAreaMapInfo {

    Long getsiteId();
    String getaddress();
    String getsiteName();
    String getroadAddress();
    String getzipCode();
    String getaddressDetail();
    Double getlatitude();
    Double getlongitude();
}
