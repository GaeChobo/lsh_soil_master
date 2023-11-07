package kr.movements.smv2.repository;

import kr.movements.smv2.dto.MaterialDto;
import kr.movements.smv2.dto.MaterialInfo;
import kr.movements.smv2.entity.ContractEntity;
import kr.movements.smv2.entity.MaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public interface MaterialRepository extends JpaRepository<MaterialEntity, Long> {

    Optional<List<MaterialEntity>> findByContractIdAndHasDeleted(Long contractId, Boolean hasDeleted);

    @Query(value = "select\n" +
            "\tmaterial_code\n" +
            "from material m\n" +
            "where m.contract_id = :contractId and m.has_deleted = false", nativeQuery = true)
    List<String> ContractmaterialList(@Param("contractId") Long contractId);

    @Query(value = "select\n" +
            "\tmaterial.id as materialId,\n" +
            "\tmaterial.material_code as materialCode,\n" +
            "\tmaterial.contract_volume as contractVolume\n" +
            "from material \n" +
            "join contract on material.contract_id = contract.id\n" +
            "where contract.id = :contractId and material.has_deleted = false", nativeQuery = true)
    List<MaterialInfo> materialInfo(@Param("contractId") Long contractId);


    @Modifying
    @Query(value = "update material set\n" +
            "\thas_deleted = true\n" +
            "where id = :materialId", nativeQuery = true)
    void materialDelete(@Param("materialId") Long materialId);

    @Modifying
    @Query(value = "update material set\n" +
            "\tmaterial_code = :materialCode,\n" +
            "\tcontract_volume = :contractVolume\n" +
            "where id = :materialId", nativeQuery = true)
    void materialUpdate(@Param("materialCode") String materialCode, @Param("contractVolume") int contractVolume, @Param("materialId") Long materialId);

    Optional<MaterialEntity> findByContractAndHasDeleted(ContractEntity contractEntity, boolean hasDeleted);
    boolean existsByContractAndMaterialCodeAndHasDeleted(ContractEntity contractEntity, String materialCode, boolean hasDeleted);
    Optional<MaterialEntity> findByIdAndHasDeleted(Long id, boolean hasDeleted);
}
