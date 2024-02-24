package com.tuituidan.openhub.controller;

import com.tuituidan.openhub.util.ResponseUtils;
import com.tuituidan.openhub.util.ZipUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileController.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/2/18
 */
@RestController
@RequestMapping("/api/v1")
public class FileController {

    /**
     * fileUpload
     *
     * @param file file
     * @param parentPath parentPath
     * @param zip zip
     * @throws IOException Exception
     */
    @PostMapping("/file/actions/upload")
    public void fileUpload(MultipartFile file, String parentPath, Boolean zip) throws IOException {
        if (BooleanUtils.isTrue(zip)) {
            ZipUtils.unzip(file.getInputStream(), parentPath);
        } else {
            FileUtils.copyInputStreamToFile(file.getInputStream(),
                    new File(parentPath + File.separator + file.getOriginalFilename()));
        }
    }

    /**
     * deleteFiles
     *
     * @param paths paths
     * @throws IOException IOException
     */
    @PostMapping("/file/delete")
    public void deleteFiles(@RequestBody String[] paths) throws IOException {
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        }
    }

    /**
     * downloadFile
     *
     * @param path path
     * @throws FileNotFoundException FileNotFoundException
     */
    @GetMapping("/file/action/download")
    public void downloadFile(String path) throws FileNotFoundException {
        ResponseUtils.download(FilenameUtils.getName(path), new FileInputStream(path));
    }

    /**
     * showFileContent
     *
     * @param path path
     * @throws IOException IOException
     */
    @GetMapping("/file/action/show")
    public String showFileContent(String path, HttpServletResponse response) throws IOException {
        return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    }

}
