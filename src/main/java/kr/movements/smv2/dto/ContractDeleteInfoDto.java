package kr.movements.smv2.dto;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@ApiModel("계약서 삭제 시 송장정보")
@Getter
@Builder
@Setter
public class ContractDeleteInfoDto {

    private String waybillStatusCode;
    private String waybillStatusValue;
    private String waybillNum;
    private String carNumber;
    private String materialCode;
    private String materialValue;
    private Integer transportVolume;
    private Long waybillId;

    public ContractDeleteInfoDto(String waybillStatusCode, String waybillStatusValue, String waybillNum, String carNumber,  String materialCode, String materialValue, Integer transportVolume, Long waybillId) {
        this.waybillStatusCode = waybillStatusCode;
        this.waybillStatusValue = waybillStatusValue;
        this.waybillNum = waybillNum;
        this.carNumber = carNumber;
        this.materialCode = materialCode;
        this.materialValue = materialValue;
        this.transportVolume = transportVolume;
        this.waybillId = waybillId;
    }
}
