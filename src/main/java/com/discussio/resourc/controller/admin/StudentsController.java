package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.IStudentsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学员记录管理 Controller
 */
@Api(value = "学员记录管理")
@RestController
@RequestMapping("/StudentsController")
@CrossOrigin(origins = "*")
public class StudentsController extends BaseController {
    
    @Autowired
    private IStudentsService studentsService;

    @ApiOperation(value = "学员记录管理列表", notes = "学员记录管理列表")
    @GetMapping("/list")
    public ResultTable Studentslist(Tablepar tablepar) {
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        if (tablepar != null && tablepar.getSearchText() != null && !tablepar.getSearchText().isEmpty()) {
            queryWrapper.like("student_name", tablepar.getSearchText())
                    .or().like("phone", tablepar.getSearchText())
                    .or().like("employee_id", tablepar.getSearchText());
        }
        
        PageHelper.startPage(tablepar != null && tablepar.getPage() != null ? tablepar.getPage() : 1, 
                           tablepar != null && tablepar.getLimit() != null ? tablepar.getLimit() : 10);
        List<Students> list = studentsService.selectStudentsList(queryWrapper);
        PageInfo<Students> page = new PageInfo<>(list);
        
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "学员记录管理新增", notes = "学员记录管理新增")
    @PostMapping("/add")
    public AjaxResult Studentsadd(@RequestBody Students students) {
        return toAjax(studentsService.insertStudents(students));
    }

    @ApiOperation(value = "学员记录管理删除", notes = "学员记录管理删除")
    @DeleteMapping("/remove")
    public AjaxResult Studentsremove(@RequestParam String ids) {
        return toAjax(studentsService.deleteStudentsByIds(ids));
    }

    @ApiOperation(value = "学员记录管理详情", notes = "获取学员记录管理详情")
    @GetMapping("/detail/{id}")
    public AjaxResult Studentsdetail(@PathVariable("id") Long id) {
        return AjaxResult.success(studentsService.selectStudentsById(id));
    }

    @ApiOperation(value = "学员记录管理修改保存", notes = "学员记录管理修改保存")
    @PostMapping("/edit")
    public AjaxResult StudentseditSave(@RequestBody Students students) {
        return toAjax(studentsService.updateStudents(students));
    }
}
