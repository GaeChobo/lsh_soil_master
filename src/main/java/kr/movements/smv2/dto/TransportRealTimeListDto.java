package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("실시간 운송 list dto")
@Getter
@Builder
public class TransportRealTimeListDto {
    @ApiModelProperty(value = "송장 id")
    private Long waybillId;
    @ApiModelProperty(value = "송장번호")
    private String waybillNum;
    @ApiModelProperty(value = "차량번호")
    private String carNumber;
    @ApiModelProperty(value = "운송기사 userId")
    private Long driverUserId;
    @ApiModelProperty(value = "회원(운송기사)명")
    private String driverName;
    @JsonFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "출발일시")
    private LocalDateTime departureTime;

    @QueryProjection
    public TransportRealTimeListDto(Long waybillId, String waybillNum, String carNumber, Long driverUserId, String driverName, LocalDateTime departureTime) {
        this.waybillId = waybillId;
        this.waybillNum = waybillNum;
        this.carNumber = carNumber;
        this.driverUserId = driverUserId;
        this.driverName = driverName;
        this.departureTime = departureTime;
    }
}
