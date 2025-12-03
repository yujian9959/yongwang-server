package com.yongwang.controller.mini;

import com.yongwang.common.result.Result;
import com.yongwang.service.oss.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 小程序文件上传接口
 */
@Tag(name = "小程序-文件上传")
@RestController
@RequestMapping("/mini/upload")
@RequiredArgsConstructor
public class MiniUploadController {

    private final OssService ossService;

    @Operation(summary = "上传单张图片")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file,
                                      @RequestParam(defaultValue = "review") String folder) {
        return Result.success(ossService.upload(file, folder));
    }

    @Operation(summary = "批量上传图片")
    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files,
                                              @RequestParam(defaultValue = "review") String folder) {
        if (files.size() > 9) {
            return Result.fail("最多上传9张图片");
        }
        return Result.success(ossService.batchUpload(files, folder));
    }
}
