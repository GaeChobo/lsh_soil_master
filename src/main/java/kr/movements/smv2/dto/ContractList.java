package kr.movements.smv2.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface ContractList {

    Long getContractId();

    String getCreator();

    default String getCreateDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return getCreatedDate().format(formatter);
    }

    String getStartSiteName();

    String getEndSiteName();
    @JsonIgnore
    LocalDateTime getCreatedDate();
}