package kr.movements.smv2.dto;

public interface SiteManagerInfo {

    Long getsiteId();
    String getsiteName();
    String getsiteTypeCode();
    String getaddress();
    String getuserName();
    String getuserPhone();
    String getuserEmail();
    boolean isWeighBridgeType();;
    String getsiteCertificationPw();
//    boolean isFirstLogin();
}
