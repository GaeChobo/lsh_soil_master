package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.movements.smv2.entity.UserInfoEntity;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;

@ApiModel("운송기사 상세정보 dto")
@Getter
@Builder
public class DriverInfoDto {
    @ApiModelProperty(value = "회원 id")
    private Long userId;
    @ApiModelProperty(value = "운송기사 id")
    private Long driverId;
    @ApiModelProperty(value = "회원명")
    private String userName;
    @ApiModelProperty(value = "회원 연락처")
    private String userPhone;
    @ApiModelProperty(value = "회원 이메일")
    private String userEmail;
    @ApiModelProperty(value = "차량번호")
    private String carNumber;
    @ApiModelProperty(value = "운송회사명")
    private String driverCompany;
    @ApiModelProperty(value = "총 운송 건수")
    private Long totalWaybill;
}
