package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.ExamMapper;
import com.discussio.resourc.model.auto.Exam;
import com.discussio.resourc.service.IExamService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 考试系统Service业务处理
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam>
        implements IExamService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamServiceImpl.class);

    @Override
    public Exam selectExamById(Long id) {
        return this.baseMapper.selectExamById(id);
    }

    @Override
    public List<Exam> selectExamList(Wrapper<Exam> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertExam(Exam exam) {
        if (StringUtils.isEmpty(exam.getExamName())) {
            throw new RuntimeException("考试名称不能为空！");
        }
        if (exam.getExamDate() == null) {
            throw new RuntimeException("考试日期不能为空！");
        }
        if (exam.getDuration() == null || exam.getDuration() <= 0) {
            throw new RuntimeException("考试时长必须大于0！");
        }
        // 总分：允许先创建考试再选题（总分可为0），选题后由系统自动计算更新
        if (exam.getTotalScore() == null) {
            exam.setTotalScore(0);
        }
        if (exam.getTotalScore() < 0) {
            throw new RuntimeException("总分不能小于0！");
        }

        // 及格分：允许手动设置，但必须 >=0 且 <= 总分
        if (exam.getPassScore() == null) {
            exam.setPassScore(0);
        }
        if (exam.getPassScore() < 0) {
            throw new RuntimeException("及格分不能小于0！");
        }
        if (exam.getPassScore() > exam.getTotalScore()) {
            throw new RuntimeException("及格分不能大于总分！");
        }
        
        if (exam.getCreateTime() == null) {
            exam.setCreateTime(new Date());
        }
        if (exam.getUpdateTime() == null) {
            exam.setUpdateTime(new Date());
        }
        if (StringUtils.isEmpty(exam.getStatus())) {
            exam.setStatus("未开始");
        }
        
        return this.baseMapper.insert(exam);
    }

    @Override
    public int updateExam(Exam exam) {
        if (exam.getId() == null) {
            throw new RuntimeException("考试ID不能为空！");
        }
        
        if (StringUtils.isNotEmpty(exam.getExamName()) && StringUtils.isEmpty(exam.getExamName().trim())) {
            throw new RuntimeException("考试名称不能为空！");
        }
        if (exam.getDuration() != null && exam.getDuration() <= 0) {
            throw new RuntimeException("考试时长必须大于0！");
        }
        if (exam.getTotalScore() != null && exam.getTotalScore() < 0) {
            throw new RuntimeException("总分不能小于0！");
        }
        if (exam.getPassScore() != null && exam.getPassScore() < 0) {
            throw new RuntimeException("及格分不能小于0！");
        }
        if (exam.getTotalScore() != null && exam.getPassScore() != null 
            && exam.getPassScore() > exam.getTotalScore()) {
            throw new RuntimeException("及格分不能大于总分！");
        }
        
        exam.setUpdateTime(new Date());
        return this.baseMapper.updateById(exam);
    }

    @Override
    public int deleteExamByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteExamById(Long id) {
        if (id == null) {
            throw new RuntimeException("考试ID不能为空");
        }
        return this.baseMapper.deleteById(id);
    }
}
