package kr.movements.smv2.repository;

import kr.movements.smv2.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByFilePathAndStoredName(String filePath, String storedName);
    @Query(value = "select * from file where file_path like :filePath%", nativeQuery = true)
    List<FileEntity> findAllByFilePathStartingWith(@Param("filePath") String filePath);
    List<FileEntity> findAllByFilePath(String filePath);


    Optional<FileEntity> findByIdAndHasDeleted(Long id, Boolean hasDeleted);

    boolean existsByFilePathAndStoredName(String filePath, String storedName);
}
