package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@ApiModel("운송기사 list dto")
@NoArgsConstructor
@Data
public class DriverListDto {
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
    @ApiModelProperty(value = "하차 건수")
    private Long unloadCnt;
    @ApiModelProperty(value = "불통과 건수")
    private Long returnCnt;

    @QueryProjection
    public DriverListDto(Long userId, Long driverId, String userName, String userPhone, String driverCarNumber, Long unloadCnt, Long returnCnt) {
        this.userId = userId;
        this.driverId = driverId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.driverCarNumber = driverCarNumber;
        this.unloadCnt = unloadCnt;
        this.returnCnt = returnCnt;
    }
}
