package github.studentpp1.advancedloginform.users.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import github.studentpp1.advancedloginform.users.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class UploadedFile extends AbstractEntity {
    @Lob
    private byte[] file;
    private Long size;
    private String originalFilename;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    private User user;

    public UploadedFile(byte[] file, Long size, String originalFilename, User user) {
        this.file = file;
        this.size = size;
        this.originalFilename = originalFilename;
        this.user = user;
    }
}
