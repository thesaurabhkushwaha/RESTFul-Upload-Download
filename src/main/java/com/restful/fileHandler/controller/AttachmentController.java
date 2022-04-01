package com.restful.fileHandler.controller;

import com.restful.fileHandler.DTO.ResponseData;
import com.restful.fileHandler.DTO.ResponseDataMultiFile;
import com.restful.fileHandler.entity.Attachment;
import com.restful.fileHandler.service.AttachmentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public ResponseData uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        Attachment attachment = attachmentService.saveAttachment(file);

        String downloadURL = attachmentService.generateDownloadURL(attachment);

        return new ResponseData(
                attachment.getFilename(),
                file.getContentType(),
                downloadURL,
                file.getSize()
        );
    }

    @PostMapping("/uploadMultiple")
    public ResponseData[] uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<Attachment> allAttachments = new ArrayList<>();
        List<String> downloadURLs = new ArrayList<>();
        for (MultipartFile file : files) {
            Attachment attachment = attachmentService.saveAttachment(file);
            String downloadURL = attachmentService.generateDownloadURL(attachment);
            allAttachments.add(attachment);
            downloadURLs.add(downloadURL);
        }

        ResponseData[] responseDataMultiFiles = new ResponseData[files.length];
        for (int i = 0; i < files.length; i++) {
            ResponseData data = new ResponseData(
                    allAttachments.get(i).getFilename(),
                    files[i].getContentType(),
                    downloadURLs.get(i),
                    files[i].getSize());
            responseDataMultiFiles[i] = data;
        }
        return responseDataMultiFiles;
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        Attachment attachment = attachmentService.getAttachment(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFilename() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }
}
