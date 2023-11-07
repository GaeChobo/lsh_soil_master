package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@ApiModel("송장 상세정보 dto")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class WaybillInfoDto {
    @ApiModelProperty(value = "송장id")
    private Long waybillId;
    @ApiModelProperty(value = "송장상태 코드")
    private String waybillStatusCode;
    @ApiModelProperty(value = "송장상태")
    private String waybillStatus;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
    @ApiModelProperty(value = "상차지명")
    private String startSiteName;
    @ApiModelProperty(value = "하차지명")
    private String endSiteName;
    @ApiModelProperty(value = "자재종류 코드")
    private String materialTypeCode;
    @ApiModelProperty(value = "자재종류")
    private String materialType;
    @ApiModelProperty(value = "운송량")
    private Integer transportVolume;
    @ApiModelProperty(value = "운송기사 user id")
    private Long userId;
    @ApiModelProperty(value = "운송기사명")
    private String driverName;
    @ApiModelProperty(value = "차량번호")
    private String carNumber;
    @ApiModelProperty(value = "gps허용 여부")
    private boolean gpsAgree;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    @ApiModelProperty(value = "출발일시")
    private String  departureTime;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    @ApiModelProperty(value = "도착일시")
    private String  arriveTime;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    @ApiModelProperty(value = "검수대기일시")
    private String waitTime;
    @ApiModelProperty(value = "사유")
    private String reason;
    @ApiModelProperty(value = "파일id")
    private Long fileId;
    @ApiModelProperty(value = "섬네일 이미지")
    private byte[] thumbnail;

//    public byte[] setThumbnail(byte[] img) {
//        return thumbnail;
//    }
}
