package com.tuituidan.openhub.bean.file;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileUpload.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/2/18
 */
@Getter
@Setter
public class FileUploadDto {

    private String parentPath;

    private MultipartFile[] files;

    private MultipartFile zipFile;
}
