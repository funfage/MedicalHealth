package com.zrf.controller.system;

import com.zrf.config.upload.UploadService;
import com.zrf.controller.BaseController;
import com.zrf.vo.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 张润发
 * @date 2021/3/12
 */
@RestController
@RequestMapping("system/upload")
public class UploadController extends BaseController {

    @Autowired
    private UploadService uploadService;

    /**
     * 文件图片 ---检查结果
     */
    @PostMapping("doUploadImage")
    public AjaxResult uploadFile(MultipartFile mf) {
        Map<String, Object> map = new HashMap<>();
        if (null != mf) {
            map.put("name", mf.getOriginalFilename());
            String path = this.uploadService.uploadImage(mf);
            map.put("url", path);
            System.out.println(map);
            return AjaxResult.success(map);
        } else {
            return AjaxResult.error("上传文件失败");
        }
    }
}
