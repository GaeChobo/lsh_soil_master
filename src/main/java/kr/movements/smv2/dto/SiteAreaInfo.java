package kr.movements.smv2.dto;

public interface SiteAreaInfo {

    Long getsiteId();
    String getaddress();
    String getsiteName();
    String getzipCode();
    String getroadAddress();

    String getaddressDetail();

    Double getlatitude();

    Double getlongitude();

    Double getdistance();
}
