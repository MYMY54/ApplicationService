package com.beaconfire.applicationservice.controller;

import com.beaconfire.applicationservice.service.DigitalDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class ApplicationController {

    private final DigitalDocumentService digitalDocumentService;
    private static final String MESSAGE_1 = "Uploaded the file successfully";
    private static final String MESSAGE_2 = "Cancel file upload successfully";
    @Autowired
    public ApplicationController(DigitalDocumentService digitalDocumentService){this.digitalDocumentService = digitalDocumentService;}


    @PostMapping("/api/files")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        digitalDocumentService.uploadFile(multipartFile);
        return new ResponseEntity<>(MESSAGE_1, HttpStatus.OK);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName){
        byte[] fileContent = digitalDocumentService.downloadFile(fileName);
        return ResponseEntity.ok()
                .contentType(contentType(fileName))
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(fileContent);
    }

    /**
     * get file type
     * @param fileName
     * @return
     */
    private MediaType contentType(String fileName) {
        String[] fileArrSplit = fileName.split("\\.");
        String fileExtension = fileArrSplit[fileArrSplit.length - 1];
        switch (fileExtension) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    /**
     * cancel file upload
     * @param fileName
     * @return
     */
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Object> cancelUpload(@PathVariable String fileName){
        digitalDocumentService.deleteFileFromS3Bucket(fileName);
        return new ResponseEntity<>(MESSAGE_2, HttpStatus.OK);
    }

}
