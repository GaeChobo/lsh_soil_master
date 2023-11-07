package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@ApiModel("운송기사 송장 등록 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverWaybillSaveDto {
    @NotNull(message = "운송기사 userid 필요")
    @ApiModelProperty(value = "운송기사 user id", example = "1682494483598064")
    private Long userId;
    @ApiModelProperty(value = "송장번호 자동부여 여부")
    private boolean waybillNumAuto;
    @ApiModelProperty(value = "송장번호", example = "110001")
    private String waybillNum;
    @NotNull(message = "차량번호는 필수입니다. ex.서울87사7373")
    @ApiModelProperty(value = "차량번호", example = "서울87사7373")
    private String  carNumber;
    @NotNull(message = "상차지 id를 확인해주세요")
    @ApiModelProperty(value = "상차지 id", example = "1681706602207903")
    private Long startSiteId;
    @NotNull(message = "상차지명을 확인해주세요")
    @ApiModelProperty(value = "상차지명", example = "현장명")
    private String  startSiteName;
    @NotNull(message = "하차지 id를 확인해주세요")
    @ApiModelProperty(value = "하차지 id", example = "1682056494979939")
    private Long endSiteId;
    @NotNull(message = "하차지명을 확인해주세요")
    @ApiModelProperty(value = "하차지명", example = "현장관리자테스트3")
    private String  endSiteName;
    @NotNull(message = "자재 코드를 확인해주세요")
    @ApiModelProperty(value = "자재종류 코드", example = "5010")
    private String  materialTypeCode;
    @NotNull(message = "운송량은 필수 입력입니다")
    @ApiModelProperty(value = "운송량", example = "13")
    private Integer transportVolume;
    @NotNull(message = "gps 허용 여부를 확인해주세요")
    @ApiModelProperty(value = "gps 허용여부")
    private boolean gpaAgree;
}
