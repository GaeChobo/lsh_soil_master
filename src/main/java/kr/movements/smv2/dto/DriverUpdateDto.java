package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ApiModel("운송기사 정보 수정 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverUpdateDto {

    @NotNull
    @NotBlank
    @ApiModelProperty(value = "회원명")
    private String userName;
    @Pattern(regexp = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})$", message = "핸드폰 번호 형식으로 입력해야 합니다.")
    @ApiModelProperty(value = "회원 연락처")
    private String userPhone;
    @ApiModelProperty(value = "차량번호")
    private String driverCarNumber;
    @ApiModelProperty(value = "운송회사명")
    private String driverCompanyName;
}
