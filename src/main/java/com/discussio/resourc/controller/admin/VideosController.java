package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.model.auto.Videos;
import com.discussio.resourc.model.auto.VideoFavorites;
import com.discussio.resourc.service.IStudentsService;
import com.discussio.resourc.service.IVideoFavoritesService;
import com.discussio.resourc.service.IVideosService;
import com.discussio.resourc.service.LoginDiscussionForumService;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频讲座播放 Controller
 */
@Api(value = "视频讲座播放")
@RestController
@RequestMapping("/VideosController")
@CrossOrigin(origins = "*")
public class VideosController extends BaseController {
    
    private String prefix = "admin/videos";
    
    @Autowired
    private IVideosService videosService;
    
    @Autowired
    private IVideoFavoritesService videoFavoritesService;
    
    @Autowired
    private IStudentsService studentsService;
    
    @Autowired
    private LoginDiscussionForumService loginService;
    
    @Value("${file.upload-path:./uploads}")
    private String uploadPath;
    
    /**
     * 检查用户是否有上传权限（讲师或管理员）
     */
    private boolean canUpload(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return false;
            String token = auth.startsWith("Bearer ") ? auth.substring("Bearer ".length()).trim() : auth.trim();
            String username = loginService.parseUsernameFromToken(token);
            if (username == null || username.trim().isEmpty()) return false;

            LoginDiscussionForum user = loginService.getUserInfo(username);
            // 0-普通用户 1-管理员
            if (user != null && user.getUserType() != null && user.getUserType() == 1) return true;

            // students.user_type: 1=学员 2=讲师 3=管理员
            if (studentsService != null) {
                Students s = studentsService.selectStudentsByPhone(username);
                return s != null && s.getUserType() != null && (s.getUserType() == 2 || s.getUserType() == 3);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取当前用户ID
     */
    private String getCurrentUserId(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return null;
            String token = auth.startsWith("Bearer ") ? auth.substring("Bearer ".length()).trim() : auth.trim();
            return loginService.parseUsernameFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    @ApiOperation(value = "获取视频列表", notes = "分页获取视频列表")
    @GetMapping("/list")
    public ResultTable list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String videoType,
            @RequestParam(required = false) String searchText,
            HttpServletRequest request) {
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        
        QueryWrapper<Videos> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "published"); // 只显示已发布的视频
        
        if (videoType != null && !videoType.trim().isEmpty() && !"全部专题".equals(videoType)) {
            queryWrapper.eq("video_type", videoType);
        }
        
        if (searchText != null && !searchText.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("video_title", searchText)
                .or()
                .like("description", searchText)
                .or()
                .like("instructor_name", searchText)
            );
        }
        
        queryWrapper.orderByDesc("publish_time");
        queryWrapper.orderByDesc("create_time");
        
        List<Videos> list = videosService.selectVideosList(queryWrapper);
        PageInfo<Videos> pageInfo = new PageInfo<>(list);
        
        // 获取当前用户的收藏列表
        String userId = getCurrentUserId(request);
        final Set<Long> favoriteIds = userId != null 
            ? new HashSet<>(videoFavoritesService.getFavoriteVideoIds(userId))
            : new HashSet<>();
        
        // 为每个视频添加是否收藏标记
        List<Map<String, Object>> resultList = list.stream().map(video -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", video.getId());
            map.put("videoId", video.getVideoId());
            map.put("videoTitle", video.getVideoTitle());
            map.put("videoUrl", video.getVideoUrl());
            map.put("videoType", video.getVideoType());
            map.put("description", video.getDescription());
            map.put("instructorId", video.getInstructorId());
            map.put("instructorName", video.getInstructorName());
            map.put("duration", video.getDuration());
            map.put("thumbnailUrl", video.getThumbnailUrl());
            map.put("viewCount", video.getViewCount());
            map.put("likeCount", video.getLikeCount());
            map.put("status", video.getStatus());
            map.put("publishTime", video.getPublishTime());
            map.put("createTime", video.getCreateTime());
            map.put("updateTime", video.getUpdateTime());
            map.put("isFavorited", favoriteIds.contains(video.getId()));
            return map;
        }).collect(Collectors.toList());
        
        return pageTable(resultList, pageInfo.getTotal());
    }

    @ApiOperation(value = "获取视频详情", notes = "根据ID获取视频详情")
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id, HttpServletRequest request) {
        Videos video = videosService.selectVideosById(id);
        if (video == null) {
            return AjaxResult.error("视频不存在");
        }
        
        // 增加观看次数
        video.setViewCount((video.getViewCount() == null ? 0 : video.getViewCount()) + 1);
        videosService.updateVideos(video);
        
        // 检查是否收藏
        String userId = getCurrentUserId(request);
        boolean isFavorited = false;
        if (userId != null) {
            isFavorited = videoFavoritesService.isFavorited(userId, id);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", video.getId());
        result.put("videoId", video.getVideoId());
        result.put("videoTitle", video.getVideoTitle());
        result.put("videoUrl", video.getVideoUrl());
        result.put("videoType", video.getVideoType());
        result.put("description", video.getDescription());
        result.put("instructorId", video.getInstructorId());
        result.put("instructorName", video.getInstructorName());
        result.put("duration", video.getDuration());
        result.put("thumbnailUrl", video.getThumbnailUrl());
        result.put("viewCount", video.getViewCount());
        result.put("likeCount", video.getLikeCount());
        result.put("status", video.getStatus());
        result.put("publishTime", video.getPublishTime());
        result.put("createTime", video.getCreateTime());
        result.put("updateTime", video.getUpdateTime());
        result.put("isFavorited", isFavorited);
        
        return AjaxResult.success(result);
    }

    @ApiOperation(value = "上传视频", notes = "上传视频文件")
    @PostMapping("/upload")
    public AjaxResult uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("videoTitle") String videoTitle,
            @RequestParam("videoType") String videoType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("instructorId") String instructorId,
            @RequestParam("instructorName") String instructorName,
            HttpServletRequest request) {
        
        // 检查权限
        if (!canUpload(request)) {
            return AjaxResult.error("无权限上传视频，仅讲师和管理员可以上传");
        }
        
        try {
            if (file.isEmpty()) {
                return AjaxResult.error("文件不能为空");
            }
            
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || 
                (!originalFilename.toLowerCase().endsWith(".mp4") && 
                 !originalFilename.toLowerCase().endsWith(".avi") &&
                 !originalFilename.toLowerCase().endsWith(".mov") &&
                 !originalFilename.toLowerCase().endsWith(".wmv"))) {
                return AjaxResult.error("仅支持视频文件格式：mp4, avi, mov, wmv");
            }
            
            // 创建上传目录
            File uploadDir = new File(uploadPath + File.separator + "videos");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = uploadDir.getAbsolutePath() + File.separator + uniqueFileName;
            
            // 保存文件
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 计算视频时长（简化处理，实际应该使用FFmpeg等工具）
            // 这里暂时设为0，需要前端或后端使用工具解析
            int duration = 0;
            
            // 保存视频信息到数据库
            Videos video = new Videos();
            video.setVideoTitle(videoTitle);
            video.setVideoUrl("/uploads/videos/" + uniqueFileName);
            video.setVideoType(videoType);
            video.setDescription(description);
            video.setInstructorId(instructorId);
            video.setInstructorName(instructorName);
            video.setDuration(duration);
            video.setThumbnailUrl(null); // 可以后续生成缩略图
            video.setViewCount(0);
            video.setLikeCount(0);
            video.setStatus("published");
            video.setPublishTime(new Date());
            
            int result = videosService.insertVideos(video);
            
            if (result > 0) {
                return AjaxResult.success("上传成功", video);
            } else {
                // 如果保存失败，删除已上传的文件
                Files.deleteIfExists(targetPath);
                return AjaxResult.error("保存视频信息失败");
            }
        } catch (IOException e) {
            return AjaxResult.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "删除视频", notes = "删除视频")
    @DeleteMapping("/remove")
    public AjaxResult remove(@RequestParam String ids, HttpServletRequest request) {
        // 检查权限
        if (!canUpload(request)) {
            return AjaxResult.error("无权限删除视频，仅讲师和管理员可以删除");
        }
        
        try {
            String[] idArray = ids.split(",");
            for (String idStr : idArray) {
                Long id = Long.parseLong(idStr.trim());
                Videos video = videosService.selectVideosById(id);
                if (video != null && video.getVideoUrl() != null) {
                    // 删除文件
                    String filePath = uploadPath + File.separator + "videos" + 
                        File.separator + video.getVideoUrl().substring(video.getVideoUrl().lastIndexOf("/") + 1);
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
            
            int result = videosService.deleteVideosByIds(ids);
            if (result > 0) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("删除失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "添加收藏", notes = "添加视频收藏")
    @PostMapping("/favorite/{id}")
    public AjaxResult addFavorite(@PathVariable("id") Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        if (userId == null) {
            return AjaxResult.error("请先登录");
        }
        
        boolean result = videoFavoritesService.addFavorite(userId, id);
        if (result) {
            return AjaxResult.success("收藏成功");
        } else {
            return AjaxResult.error("收藏失败");
        }
    }

    @ApiOperation(value = "取消收藏", notes = "取消视频收藏")
    @DeleteMapping("/favorite/{id}")
    public AjaxResult removeFavorite(@PathVariable("id") Long id, HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        if (userId == null) {
            return AjaxResult.error("请先登录");
        }
        
        boolean result = videoFavoritesService.removeFavorite(userId, id);
        if (result) {
            return AjaxResult.success("取消收藏成功");
        } else {
            return AjaxResult.error("取消收藏失败");
        }
    }

    @ApiOperation(value = "获取我的收藏", notes = "获取当前用户的收藏视频列表")
    @GetMapping("/myFavorites")
    public ResultTable myFavorites(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            HttpServletRequest request) {
        String userId = getCurrentUserId(request);
        if (userId == null) {
            return pageTable(new ArrayList<>(), 0);
        }
        
        List<Long> favoriteIds = videoFavoritesService.getFavoriteVideoIds(userId);
        if (favoriteIds.isEmpty()) {
            return pageTable(new ArrayList<>(), 0);
        }
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        
        QueryWrapper<Videos> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", favoriteIds);
        queryWrapper.orderByDesc("publish_time");
        
        List<Videos> list = videosService.selectVideosList(queryWrapper);
        PageInfo<Videos> pageInfo = new PageInfo<>(list);
        
        // 标记为已收藏
        List<Map<String, Object>> resultList = list.stream().map(video -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", video.getId());
            map.put("videoId", video.getVideoId());
            map.put("videoTitle", video.getVideoTitle());
            map.put("videoUrl", video.getVideoUrl());
            map.put("videoType", video.getVideoType());
            map.put("description", video.getDescription());
            map.put("instructorId", video.getInstructorId());
            map.put("instructorName", video.getInstructorName());
            map.put("duration", video.getDuration());
            map.put("thumbnailUrl", video.getThumbnailUrl());
            map.put("viewCount", video.getViewCount());
            map.put("likeCount", video.getLikeCount());
            map.put("status", video.getStatus());
            map.put("publishTime", video.getPublishTime());
            map.put("createTime", video.getCreateTime());
            map.put("updateTime", video.getUpdateTime());
            map.put("isFavorited", true);
            return map;
        }).collect(Collectors.toList());
        
        return pageTable(resultList, pageInfo.getTotal());
    }

    @ApiOperation(value = "获取视频类型列表", notes = "获取所有视频类型（专题）")
    @GetMapping("/types")
    public AjaxResult getTypes() {
        QueryWrapper<Videos> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT video_type");
        queryWrapper.isNotNull("video_type");
        queryWrapper.ne("video_type", "");
        queryWrapper.eq("status", "published");
        
        List<Videos> list = videosService.selectVideosList(queryWrapper);
        List<String> types = list.stream()
                .map(Videos::getVideoType)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        return AjaxResult.success(types);
    }

    @ApiOperation(value = "预览视频文件", notes = "根据ID在线预览视频（浏览器内播放）")
    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> preview(@PathVariable("id") Long id) {
        try {
            Videos video = videosService.selectVideosById(id);
            if (video == null || video.getVideoUrl() == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 解析文件路径
            String videoUrl = video.getVideoUrl();
            File file;
            if (videoUrl.startsWith("/")) {
                // 相对路径，从上传目录解析
                String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
                file = new File(uploadPath + File.separator + "videos" + File.separator + fileName);
            } else {
                // 绝对路径
                file = new File(videoUrl);
            }
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource fileResource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "video/mp4"; // 默认视频类型
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + video.getVideoTitle() + "\"")
                    .body(fileResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
