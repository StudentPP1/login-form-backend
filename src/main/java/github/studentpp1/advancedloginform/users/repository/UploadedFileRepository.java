package github.studentpp1.advancedloginform.users.repository;

import github.studentpp1.advancedloginform.users.models.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

}