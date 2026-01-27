package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.ExamResultMapper;
import com.discussio.resourc.model.auto.ExamResult;
import com.discussio.resourc.service.IExamResultService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 考试结果Service业务处理
 */
@Service
public class ExamResultServiceImpl extends ServiceImpl<ExamResultMapper, ExamResult>
        implements IExamResultService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamResultServiceImpl.class);

    @Override
    public ExamResult selectExamResultById(Long id) {
        return this.baseMapper.selectExamResultById(id);
    }

    @Override
    public List<ExamResult> selectExamResultList(Wrapper<ExamResult> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public ExamResult selectExamResultByExamIdAndStudentId(Long examId, String studentId) {
        QueryWrapper<ExamResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exam_id", examId);
        queryWrapper.eq("student_id", studentId);
        List<ExamResult> list = this.baseMapper.selectList(queryWrapper);
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    @Override
    public int insertExamResult(ExamResult examResult) {
        if (examResult.getExamId() == null) {
            throw new RuntimeException("考试ID不能为空！");
        }
        if (StringUtils.isEmpty(examResult.getStudentId())) {
            throw new RuntimeException("学员ID不能为空！");
        }
        
        if (examResult.getCreateTime() == null) {
            examResult.setCreateTime(new Date());
        }
        if (examResult.getUpdateTime() == null) {
            examResult.setUpdateTime(new Date());
        }
        if (StringUtils.isEmpty(examResult.getStatus())) {
            examResult.setStatus("进行中");
        }
        if (examResult.getStartTime() == null) {
            examResult.setStartTime(new Date());
        }
        
        return this.baseMapper.insert(examResult);
    }

    @Override
    public int updateExamResult(ExamResult examResult) {
        if (examResult.getId() == null) {
            throw new RuntimeException("结果ID不能为空！");
        }
        
        examResult.setUpdateTime(new Date());
        return this.baseMapper.updateById(examResult);
    }

    @Override
    public int deleteExamResultByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteExamResultById(Long id) {
        if (id == null) {
            throw new RuntimeException("结果ID不能为空");
        }
        return this.baseMapper.deleteById(id);
    }
}
