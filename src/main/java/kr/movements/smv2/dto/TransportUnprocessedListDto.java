package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@ApiModel("운송 미처리 내역 list dto")
@Getter
@Builder
public class TransportUnprocessedListDto {
    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
    @ApiModelProperty(value = "차량번호")
    private String carNumber;
    @ApiModelProperty(value = "연락처")
    private String userPhone;
    @ApiModelProperty(value = "운송기사 userId")
    private Long driverUserId;
    @ApiModelProperty(value = "회원(운송기사)명")
    private String driverName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "출발 일시")
    private LocalDateTime departureTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "강제운송종료 일시")
    private LocalDateTime forceQuitTime;

    @QueryProjection

    public TransportUnprocessedListDto(Long waybillId, String waybillNum, String carNumber, String userPhone, Long driverUserId, String driverName, LocalDateTime departureTime, LocalDateTime forceQuitTime) {
        this.waybillId = waybillId;
        this.waybillNum = waybillNum;
        this.carNumber = carNumber;
        this.userPhone = userPhone;
        this.driverUserId = driverUserId;
        this.driverName = driverName;
        this.departureTime = departureTime;
        this.forceQuitTime = forceQuitTime;
    }
}
