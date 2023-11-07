package kr.movements.smv2.dto;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@ApiModel("코드 dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeValue {

    private String code;
    private String value;
}
