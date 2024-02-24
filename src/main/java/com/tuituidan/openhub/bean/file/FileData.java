package com.tuituidan.openhub.bean.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * FileData.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/2/5
 */
@Getter
@Setter
@Accessors(chain = true)
public class FileData {

    private String path;

    private String label;

    private String fileSize;

    private String lastModifyTime;

    private Boolean leaf;
}
