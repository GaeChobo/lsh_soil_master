package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@ApiModel("운송정보 검수불통과 목록 Response")
@Getter
@Builder
public class TransportNotPassListResponse {
    @ApiModelProperty(value = "회원정보 id")
    private Long userId;
    @ApiModelProperty(value = "운송기사 id")
    private Long driverId;
    @ApiModelProperty(value = "회원명")
    private String userName;
    @ApiModelProperty(value = "회원 연락처")
    private String userPhone;
    @ApiModelProperty(value = "차량번호")
    private String driverCarNumber;
    @ApiModelProperty(value = "불통과 건수")
    private Long notPassCount;
    @ApiModelProperty(value = "총 운송 건수")
    private Long totalPassCount;

    @QueryProjection
    public TransportNotPassListResponse(Long userId, Long driverId, String userName, String userPhone, String driverCarNumber, Long notPassCount, Long totalPassCount) {
        this.userId = userId;
        this.driverId = driverId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.driverCarNumber = driverCarNumber;
        this.notPassCount = notPassCount;
        this.totalPassCount = totalPassCount;
    }
}
