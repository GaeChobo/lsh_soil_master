package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import kr.movements.smv2.entity.SiteManagerEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@ApiModel("현장관리자 회원가입 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteManagerSaveDto {

    @ApiModelProperty(value = "회원명", example = "윤대훈")
    private String userName;
    @ApiModelProperty(value = "회원 연락처", example = "010-1234-4567")
    private String userPhone;
    @ApiModelProperty(value = "회원 이메일", example = "mv.asd@movements.kr")
    private String userEmail;
    @ApiModelProperty(value = "현장관리자 계근대여부")
    private boolean weighBridgeType;
    @ApiModelProperty(value = "현장관리자 현장명" , example = "현장명")
    private String siteName;
    @ApiModelProperty(value = "현장관리자 현장주소" , example = "현장주소")
    private String address;
    @ApiModelProperty(value = "현장관리자 지번"  , example = "현장지번")
    private String zipCode;
    @ApiModelProperty(value = "현장관리자 현장구분"  , example = "2010(상차지) / 2020(하차지)")
    private String siteTypeCode;
    @ApiModelProperty(name = "현장관리자 도로명", example = "현장 도로명")
    private String roadAddress;
    @ApiModelProperty(value = "현장관리자 인증PW" , example = "'123456'")
    private String siteCertificationPw;
    @ApiModelProperty(value = "현장관리자 위도", example = "0.5")
    @DecimalMin(value = "-90.0", message = "위도는 -90보다 크거나 같아야 합니다.")
    @DecimalMax(value = "90.0", message = "위도는 90보다 작거나 같아야 합니다.")
    private Double latitude;
    @ApiModelProperty(value = "현장관리자 경도", example = "0.5")
    @DecimalMin(value = "-180.0", message = "경도는 -180보다 크거나 같아야 합니다.")
    @DecimalMax(value = "180.0", message = "경도는 180보다 작거나 같아야 합니다.")
    private Double longitude;
    @ApiModelProperty(value = "현장관리자 상세주소" , example = "현장상세주소")
    private String addressDetail;
}
