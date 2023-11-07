package kr.movements.smv2.dto;

public interface AppWaybillList {

    String getCreateDate();

    Long getWaybillId();

    String getStartSiteName();

    String getEndSiteName();

    int getWaybillNum();

    boolean isGpsAgree();

    String getWaybillStatusCode();

    String getMaterialTypeCode();
}
