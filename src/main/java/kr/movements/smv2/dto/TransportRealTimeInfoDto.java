package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

@ApiModel("운송기사 상세정보 dto")
@Getter
@Builder
public class TransportRealTimeInfoDto {
//    @ApiModelProperty(value = "순서")
//    private Integer num;
    @ApiModelProperty(value = "위도")
    private Double lat;
    @ApiModelProperty(value = "경도")
    private Double lon;

    @QueryProjection
    public TransportRealTimeInfoDto(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
