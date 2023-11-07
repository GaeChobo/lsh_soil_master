package kr.movements.smv2.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@ApiModel("현장검색 팝업 - 주소 response")
@Getter
@Builder
public class SearchAddressResponse {
    @ApiModelProperty(value = "현장 id")
    private Long siteId;
    @ApiModelProperty(value = "현장명")
    private String siteName;
    @ApiModelProperty(value = "지번 주소")
    private String address;
    @ApiModelProperty(value = "도로명 주소")
    private String roadAddress;
    @ApiModelProperty(value = "상세주소")
    private String addressDetail;
    @ApiModelProperty(value = "우편번호")
    private String zipCode;

    @QueryProjection
    public SearchAddressResponse(Long siteId, String siteName, String address, String roadAddress, String addressDetail, String zipCode) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.address = address;
        this.roadAddress = roadAddress;
        this.addressDetail = addressDetail;
        this.zipCode = zipCode;
    }
}
