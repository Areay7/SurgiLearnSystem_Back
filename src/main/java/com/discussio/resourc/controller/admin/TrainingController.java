package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.Training;
import com.discussio.resourc.model.auto.TrainingMaterial;
import com.discussio.resourc.model.auto.TrainingContentBlock;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.ITrainingContentBlockService;
import com.discussio.resourc.service.ITrainingMaterialService;
import com.discussio.resourc.service.ITrainingService;
import com.discussio.resourc.service.LoginDiscussionForumService;
import com.discussio.resourc.service.IStudentsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 护理培训管理（课程+关联资料）
 */
@Api(value = "护理培训管理")
@RestController
@RequestMapping("/TrainingController")
@CrossOrigin(origins = "*")
public class TrainingController extends BaseController {

    @Autowired
    private ITrainingService trainingService;

    @Autowired
    private ITrainingMaterialService trainingMaterialService;

    @Autowired
    private ITrainingContentBlockService trainingContentBlockService;

    @Autowired
    private LoginDiscussionForumService loginService;

    @Autowired(required = false)
    private IStudentsService studentsService;

    @ApiOperation(value = "培训列表", notes = "分页、搜索、类型/状态过滤")
    @GetMapping("/list")
    public ResultTable list(@RequestParam(required = false) Integer page,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) String searchText,
                            @RequestParam(required = false) String trainingType,
                            @RequestParam(required = false) String status) {
        QueryWrapper<Training> qw = new QueryWrapper<>();
        if (StringUtils.isNotBlank(searchText)) {
            qw.and(w -> w.like("training_name", searchText).or().like("description", searchText));
        }
        if (StringUtils.isNotBlank(trainingType)) {
            qw.eq("training_type", trainingType.trim());
        }
        if (StringUtils.isNotBlank(status)) {
            qw.eq("status", status.trim());
        }
        qw.orderByDesc("update_time");

        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<Training> list = trainingService.selectTrainingList(qw);
        PageInfo<Training> pageInfo = new PageInfo<>(list);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "培训详情")
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        return AjaxResult.success(trainingService.selectTrainingById(id));
    }

    @ApiOperation(value = "新增培训")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody Training training, HttpServletRequest request) {
        try {
            UserRole role = resolveUserRole(request);
            if (!role.isAdmin && !role.isInstructor) {
                return AjaxResult.error(403, "无权限操作（仅管理员/讲师可创建培训）");
            }

            // 讲师创建时，强制讲师为自己
            if (role.isInstructor && role.student != null) {
                training.setInstructorId(String.valueOf(role.student.getId()));
                training.setInstructorName(role.student.getStudentName() != null
                        ? role.student.getStudentName()
                        : role.student.getPhone());
            }

            // 管理员：如果未选择讲师则报错
            if (role.isAdmin && (training.getInstructorId() == null || training.getInstructorId().trim().isEmpty())) {
                return AjaxResult.error("请先选择讲师");
            }

            if (training.getCreateTime() == null) training.setCreateTime(new Date());
            if (training.getUpdateTime() == null) training.setUpdateTime(new Date());
            return toAjax(trainingService.insertTraining(training));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "修改培训")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody Training training, HttpServletRequest request) {
        try {
            UserRole role = resolveUserRole(request);
            if (!role.isAdmin && !role.isInstructor) {
                return AjaxResult.error(403, "无权限操作（仅管理员/讲师可编辑培训）");
            }
            return toAjax(trainingService.updateTraining(training));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除培训")
    @DeleteMapping("/remove")
    public AjaxResult remove(@RequestParam String ids, HttpServletRequest request) {
        UserRole role = resolveUserRole(request);
        if (!role.isAdmin && !role.isInstructor) {
            return AjaxResult.error(403, "无权限操作（仅管理员/讲师可删除培训）");
        }
        return toAjax(trainingService.deleteTrainingByIds(ids));
    }

    @ApiOperation(value = "获取培训关联资料")
    @GetMapping("/materials/{trainingId}")
    public AjaxResult listMaterials(@PathVariable("trainingId") Long trainingId) {
        return AjaxResult.success(trainingMaterialService.listByTrainingId(trainingId));
    }

    @ApiOperation(value = "设置培训关联资料", notes = "替换该培训的资料列表")
    @PostMapping("/materials/{trainingId}")
    public AjaxResult replaceMaterials(@PathVariable("trainingId") Long trainingId,
                                       @RequestBody List<TrainingMaterial> items) {
        try {
            int n = trainingMaterialService.replaceTrainingMaterials(trainingId, items);
            return AjaxResult.success("保存成功", n);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取培训资料白板内容块", notes = "按 sort_order 从上往下")
    @GetMapping("/content-blocks/{trainingId}")
    public AjaxResult listContentBlocks(@PathVariable("trainingId") Long trainingId) {
        return AjaxResult.success(trainingContentBlockService.listByTrainingId(trainingId));
    }

    @ApiOperation(value = "保存培训资料白板", notes = "替换该培训的所有内容块（文字/图片/视频/PDF/文件）")
    @PostMapping("/content-blocks/{trainingId}")
    public AjaxResult saveContentBlocks(@PathVariable("trainingId") Long trainingId,
                                        @RequestBody List<TrainingContentBlock> items) {
        try {
            int n = trainingContentBlockService.replaceBlocks(trainingId, items);
            return AjaxResult.success("保存成功", n);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private static class UserRole {
        boolean isAdmin;
        boolean isInstructor;
        Students student;
    }

    private UserRole resolveUserRole(HttpServletRequest request) {
        UserRole r = new UserRole();
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return r;
            String token = auth.startsWith("Bearer ") ? auth.substring("Bearer ".length()).trim() : auth.trim();
            String username = loginService.parseUsernameFromToken(token);
            if (username == null || username.trim().isEmpty()) return r;

            LoginDiscussionForum user = loginService.getUserInfo(username);
            // 0-普通用户 1-管理员
            if (user != null && user.getUserType() != null && user.getUserType() == 1) {
                r.isAdmin = true;
            }
            if (studentsService != null) {
                Students s = studentsService.selectStudentsByPhone(username);
                r.student = s;
                if (s != null && s.getUserType() != null && s.getUserType() == 2) {
                    r.isInstructor = true;
                }
            }
        } catch (Exception ignore) {
        }
        return r;
    }
}

