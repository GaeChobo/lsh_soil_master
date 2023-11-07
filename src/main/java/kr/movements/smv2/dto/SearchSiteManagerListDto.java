package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("현장관리자 검색 리스트 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSiteManagerListDto {

    @ApiModelProperty(value = "검색타입", example = "name, phoneNumber, siteName")
    private String searchType;
    @ApiModelProperty(value = "검색값", example = "윤대훈")
    private String keyWord;
}
