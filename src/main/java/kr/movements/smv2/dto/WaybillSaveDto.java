package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import kr.movements.smv2.entity.code.CommonCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@ApiModel("현장관리자 송장등록 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaybillSaveDto {

    @ApiModelProperty(value = "송장번호 자동부여 여부")
    private boolean waybillNumAuto;
    @NotNull(message = "송장상태를 확인해주세요")
    @ApiModelProperty(value = "송장상태 코드", example = "3040")
    private String waybillStatusCode;
    @ApiModelProperty(value = "송장번호", example = "110001")
    private String waybillNum;
    @NotNull(message = "상차지 id를 확인해주세요")
    @ApiModelProperty(value = "상차지 id", example = "1681110585248410")
    private Long startSiteId;
    @NotNull(message = "하차지 id를 확인해주세요")
    @ApiModelProperty(value = "하차지 id", example = "1681192216862991")
    private Long endSiteId;
    @NotNull(message = "자재종류코드를 확인해주세요")
    @ApiModelProperty(value = "자재종류 코드", example = "5010")
    private String  materialTypeCode;
    @NotNull(message = "운송량은 필수 입력입니다")
    @Max(13)
    @ApiModelProperty(value = "운송량", example = "13")
    private Integer transportVolume;
    @NotNull(message = "운송기사 id를 확인해주세요")
    @ApiModelProperty(value = "운송기사 user id", example = "1681111201444877")
    private Long userId;
    @NotNull(message = "차량번호를 입력해주세요")
    @Pattern(regexp = "^[서울|부산|대구|인천|대전|광주|울산|제주|경기|강원|충남|전남|전북|경남|경북|세종]{2}(8([0-7])|9(8|9))[아바사자]\\d{4}", message = "차량번호 형식을 확인해주세요")
    @ApiModelProperty(value = "차량번호", example = "서울85누1234")
    private String  carNumber;
    @NotNull(message = "출발일시를 입력해주세요")
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "출발일시", example = "2023-04-18 14:45")
    private LocalDateTime departureTime;
    @NotNull(message = "도착일시를 입력해주세요")
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "도착일시", example = "2023-04-18 19:23")
    private LocalDateTime arriveTime;
    @ApiModelProperty(value = "비정상 하차 사유 코드")
    private String  reasonTypeCode;
    @Size(max = 200)
    @ApiModelProperty(value = "비정상 하차 사유")
    private String  reason;
    @ApiModelProperty(value = "gps 허용여부")
    private boolean gpsAgree;
}
