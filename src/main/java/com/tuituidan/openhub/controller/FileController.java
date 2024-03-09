package com.tuituidan.openhub.controller;

import com.tuituidan.openhub.bean.file.FileData;
import com.tuituidan.openhub.util.ResponseUtils;
import com.tuituidan.openhub.util.StringExtUtils;
import com.tuituidan.openhub.util.ZipUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
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
     * @throws FileNotFoundException ex
     */
    @GetMapping("/file/action/download")
    public void downloadFile(String path) throws IOException {
        File file = new File(path);
        if (file.isFile()) {
            Assert.isTrue(file.length() > 0, "这是一个空文件");
            ResponseUtils.download(file.getName(), new FileInputStream(path));
            return;
        }
        File[] files = file.listFiles();
        Assert.isTrue(files != null && files.length > 0, "这是一个空文件夹");
        String zipPath = new File(path).getParentFile().getPath() + File.separator + StringExtUtils.getUuid() + ".zip";
        ZipUtils.zip(zipPath, Arrays.stream(files).map(File::getPath).collect(Collectors.toList()));
        ResponseUtils.download(FilenameUtils.getName(path) + ".zip", new FileInputStream(zipPath));
        FileUtils.forceDelete(new File(zipPath));
    }

    /**
     * showFileContent
     *
     * @param path path
     * @throws IOException IOException
     */
    @GetMapping("/file/action/show")
    public String showFileContent(String path) throws IOException {
        return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    }

    /**
     * loadFileData
     *
     * @param path path
     * @return List
     */
    @GetMapping("/site/files")
    public List<FileData> loadFileData(String rootPath, String path) {
        File[] files = new File(StringUtils.isNotBlank(path) ? path : rootPath).listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files).map(file -> new FileData()
                        .setLabel(file.getName())
                        .setPath(file.getPath())
                        .setFileSize(FileUtils.byteCountToDisplaySize(file.length()))
                        .setLastModifyTime(LocalDateTime
                                .ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .setLeaf(file.isFile()))
                .sorted(Comparator.comparing(FileData::getLeaf)
                        .thenComparing(FileData::getLabel, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

}
