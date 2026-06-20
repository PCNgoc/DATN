package com.gxsneaker.gxsneaker.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;


@RestController
@RequestMapping("/api/upload")
@CrossOrigin("*")
public class UploadController {

    private final String uploadFolder =
            System.getProperty("user.dir")
                    + "/src/main/resources/static/images/";


    @PostMapping
    public String uploadImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {


        if(file.isEmpty()){
            throw new RuntimeException("File ảnh rỗng");
        }


        String fileName =
                UUID.randomUUID()
                        + "_"
                        + file.getOriginalFilename();


        Path path = Paths.get(
                uploadFolder + fileName
        );


        Files.createDirectories(
                path.getParent()
        );


        Files.copy(
                file.getInputStream(),
                path,
                StandardCopyOption.REPLACE_EXISTING
        );


        System.out.println(
                "ẢNH ĐÃ LƯU: "
                        + path.toAbsolutePath()
        );


        return fileName;
    }
}