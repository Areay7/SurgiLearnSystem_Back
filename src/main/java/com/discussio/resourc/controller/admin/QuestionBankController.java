package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.QuestionBank;
import com.discussio.resourc.service.IQuestionBankService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 在线题库 Controller
 */
@Api(value = "在线题库管理")
@RestController
@RequestMapping("/QuestionBankController")
@CrossOrigin(origins = "*")
public class QuestionBankController extends BaseController {
    
    @Autowired
    private IQuestionBankService questionBankService;

    @ApiOperation(value = "题库列表", notes = "获取题库列表")
    @GetMapping("/list")
    public ResultTable questionBankList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String questionType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        
        // 题目内容搜索
        if (searchText != null && !searchText.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("question_content", searchText.trim())
                .or()
                .like("category", searchText.trim())
            );
        }
        
        // 题目类型筛选
        if (questionType != null && !questionType.trim().isEmpty()) {
            queryWrapper.eq("question_type", questionType.trim());
        }
        
        // 分类筛选
        if (category != null && !category.trim().isEmpty()) {
            queryWrapper.eq("category", category.trim());
        }
        
        // 难度筛选
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            queryWrapper.eq("difficulty", difficulty.trim());
        }
        
        // 排序：按创建时间倒序
        queryWrapper.orderByDesc("create_time");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<QuestionBank> list = questionBankService.selectQuestionBankList(queryWrapper);
        PageInfo<QuestionBank> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "新增题目", notes = "新增题目")
    @PostMapping("/add")
    public AjaxResult questionBankAdd(@RequestBody QuestionBank questionBank) {
        try {
            return toAjax(questionBankService.insertQuestionBank(questionBank));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除题目", notes = "删除题目")
    @DeleteMapping("/remove")
    public AjaxResult questionBankRemove(@RequestParam String ids) {
        try {
            return toAjax(questionBankService.deleteQuestionBankByIds(ids));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "题目详情", notes = "获取题目详情")
    @GetMapping("/detail/{id}")
    public AjaxResult questionBankDetail(@PathVariable("id") Long id) {
        try {
            return AjaxResult.success(questionBankService.selectQuestionBankById(id));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "修改题目", notes = "修改题目")
    @PostMapping("/edit")
    public AjaxResult questionBankEdit(@RequestBody QuestionBank questionBank) {
        try {
            return toAjax(questionBankService.updateQuestionBank(questionBank));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "导入题库模板下载", notes = "下载Excel导入模板")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        Workbook workbook = new XSSFWorkbook();
        try {
            Sheet sheet = workbook.createSheet("题库导入模板");

            // 表头
            Row header = sheet.createRow(0);
            String[] headers = new String[] {
                    "题目类型(单选/多选/判断)",
                    "题目内容",
                    "选项A",
                    "选项B",
                    "选项C",
                    "选项D",
                    "正确答案(单选:A 多选:A,B 判断:正确/错误)",
                    "解析",
                    "分类",
                    "难度(基础/提高/挑战)",
                    "分值(数字)"
            };
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i, CellType.STRING).setCellValue(headers[i]);
                sheet.setColumnWidth(i, 22 * 256);
            }

            // 示例行：单选
            Row sample1 = sheet.createRow(1);
            sample1.createCell(0, CellType.STRING).setCellValue("单选");
            sample1.createCell(1, CellType.STRING).setCellValue("示例：无菌操作的关键步骤是？");
            sample1.createCell(2, CellType.STRING).setCellValue("洗手消毒");
            sample1.createCell(3, CellType.STRING).setCellValue("不戴手套");
            sample1.createCell(4, CellType.STRING).setCellValue("随意触碰无菌区");
            sample1.createCell(5, CellType.STRING).setCellValue("不铺无菌巾");
            sample1.createCell(6, CellType.STRING).setCellValue("A");
            sample1.createCell(7, CellType.STRING).setCellValue("解析示例：严格洗手消毒是无菌操作基础。");
            sample1.createCell(8, CellType.STRING).setCellValue("无菌技术");
            sample1.createCell(9, CellType.STRING).setCellValue("基础");
            sample1.createCell(10, CellType.NUMERIC).setCellValue(1);

            // 示例行：多选
            Row sample2 = sheet.createRow(2);
            sample2.createCell(0, CellType.STRING).setCellValue("多选");
            sample2.createCell(1, CellType.STRING).setCellValue("示例：下列哪些属于感染风险因素？");
            sample2.createCell(2, CellType.STRING).setCellValue("糖尿病");
            sample2.createCell(3, CellType.STRING).setCellValue("肥胖");
            sample2.createCell(4, CellType.STRING).setCellValue("吸烟");
            sample2.createCell(5, CellType.STRING).setCellValue("适当运动");
            sample2.createCell(6, CellType.STRING).setCellValue("A,B,C");
            sample2.createCell(7, CellType.STRING).setCellValue("解析示例：慢病、肥胖、吸烟均增加风险。");
            sample2.createCell(8, CellType.STRING).setCellValue("感染控制");
            sample2.createCell(9, CellType.STRING).setCellValue("提高");
            sample2.createCell(10, CellType.NUMERIC).setCellValue(5);

            // 示例行：判断
            Row sample3 = sheet.createRow(3);
            sample3.createCell(0, CellType.STRING).setCellValue("判断");
            sample3.createCell(1, CellType.STRING).setCellValue("示例：术后疼痛评估应覆盖部位、性质与评分。");
            sample3.createCell(2, CellType.STRING).setCellValue("正确");
            sample3.createCell(3, CellType.STRING).setCellValue("错误");
            sample3.createCell(6, CellType.STRING).setCellValue("正确");
            sample3.createCell(7, CellType.STRING).setCellValue("解析示例：疼痛评估维度应全面。");
            sample3.createCell(8, CellType.STRING).setCellValue("疼痛管理");
            sample3.createCell(9, CellType.STRING).setCellValue("基础");
            sample3.createCell(10, CellType.NUMERIC).setCellValue(1);

            String filename = "题库导入模板.xlsx";
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(filename, "UTF-8"));
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            // 忽略输出异常
        } finally {
            try { workbook.close(); } catch (Exception ignored) {}
        }
    }

    @ApiOperation(value = "批量导入题目", notes = "上传Excel批量导入题目")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult importQuestions(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return AjaxResult.error("请选择要上传的Excel文件");
        }
        String name = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (!name.endsWith(".xlsx")) {
            return AjaxResult.error("仅支持 .xlsx 格式的Excel文件");
        }

        int success = 0;
        List<String> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return AjaxResult.error("Excel中未找到工作表");
            }

            // 从第2行开始读取（第1行为表头）
            int lastRow = sheet.getLastRowNum();
            for (int r = 1; r <= lastRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String questionType = getCellString(row.getCell(0));
                String questionContent = getCellString(row.getCell(1));
                String optionA = getCellString(row.getCell(2));
                String optionB = getCellString(row.getCell(3));
                String optionC = getCellString(row.getCell(4));
                String optionD = getCellString(row.getCell(5));
                String correctAnswer = getCellString(row.getCell(6));
                String explanation = getCellString(row.getCell(7));
                String category = getCellString(row.getCell(8));
                String difficulty = getCellString(row.getCell(9));
                Integer score = getCellInteger(row.getCell(10));

                // 空行跳过：类型/内容/答案都为空
                if (isBlank(questionType) && isBlank(questionContent) && isBlank(correctAnswer)) {
                    continue;
                }

                try {
                    QuestionBank qb = new QuestionBank();
                    qb.setQuestionType(trimToNull(questionType));
                    qb.setQuestionContent(trimToNull(questionContent));
                    qb.setOptionA(trimToNull(optionA));
                    qb.setOptionB(trimToNull(optionB));
                    qb.setOptionC(trimToNull(optionC));
                    qb.setOptionD(trimToNull(optionD));
                    qb.setCorrectAnswer(trimToNull(correctAnswer));
                    qb.setExplanation(trimToNull(explanation));
                    qb.setCategory(trimToNull(category));
                    qb.setDifficulty(trimToNull(difficulty));
                    qb.setScore(score != null ? score : 0);
                    qb.setCreateTime(new Date());
                    qb.setUpdateTime(new Date());

                    // 复用现有单条校验+插入
                    questionBankService.insertQuestionBank(qb);
                    success++;
                } catch (Exception ex) {
                    errors.add("第" + (r + 1) + "行导入失败：" + ex.getMessage());
                }
            }

            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", success);
            result.put("errors", errors);
            return AjaxResult.success("导入完成", result);
        } catch (Exception e) {
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }

    private static String getCellString(Cell cell) {
        if (cell == null) return "";
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                double v = cell.getNumericCellValue();
                long lv = (long) v;
                if (Math.abs(v - lv) < 1e-9) return String.valueOf(lv);
                return String.valueOf(v);
            }
            cell.setCellType(CellType.STRING);
            return cell.getStringCellValue();
        } catch (Exception e) {
            return "";
        }
    }

    private static Integer getCellInteger(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            }
            String s = getCellString(cell);
            if (isBlank(s)) return null;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
