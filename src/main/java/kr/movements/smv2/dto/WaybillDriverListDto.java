package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("현장관리자 - 운송기사 list dto")
@NoArgsConstructor
@Getter
public class WaybillDriverListDto {
    @ApiModelProperty(value = "회원정보 id")
    private Long userId;
    @ApiModelProperty(value = "운송기사 id")
    private Long driverId;
    @ApiModelProperty(value = "회원명")
    private String driverName;
    @ApiModelProperty(value = "회원 연락처")
    private String driverPhone;
    @ApiModelProperty(value = "차량번호")
    private String driverCarNumber;

    @QueryProjection
    public WaybillDriverListDto(Long userId, Long driverId, String driverName, String driverPhone, String driverCarNumber) {
        this.userId = userId;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverCarNumber = driverCarNumber;
    }
}
