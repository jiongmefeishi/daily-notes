package com.alibaba.asp.file.entity;

import com.alibaba.asp.common.annotations.DomainObject;
import com.alibaba.asp.general.entity.BaseEntity;
import lombok.*;

/**
 * @author: tao
 * @date: 2022/4/13
 * @email: zengqingtao@harmonycloud.cn
 */
@DomainObject
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AspFile extends BaseEntity {

    private String fileId;
    private String fileName;
    private String path;
    private String bucket;
    private String tenant;
}
