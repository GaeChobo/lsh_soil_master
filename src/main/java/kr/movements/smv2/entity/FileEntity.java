package kr.movements.smv2.entity;


import kr.movements.smv2.entity.baseEntity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "file")
public class FileEntity extends BaseEntity {


/*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reason_id", foreignKey = @ForeignKey(name = "fk_file_reason"), nullable = false)
    private ReasonEntity reasonId;
 */

    @Column(nullable = false, length = 200)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize; // 바이트

    @Column(nullable = false, length = 45)
    private String fileType;

    @Column(nullable = false, length = 20)
    private String mime;

    @Column(nullable = false, length = 300)
    private String originalName;

    @Column(nullable = false, length = 30)
    private String storedName;

//    @Column
//    private String thumbnailUrl;

//	public void changeFilePath(String filePath){
//		this.filePath = filePath;
//	}

//    public void changeThumbnailUrl(String thumbnailUrl){
//        this.thumbnailUrl = thumbnailUrl;
//    }

    public void update(Long fileSize, String contentType, String mime, String originalName, String storedName){
        this.fileSize = fileSize;
        this.fileType = contentType;
        this.mime = mime;
        this.originalName = originalName;
        this.storedName = storedName;
    }
}
