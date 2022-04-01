package com.restful.fileHandler.repository;

import com.restful.fileHandler.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, String> {
}
