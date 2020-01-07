package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Feedback;
import com.gaocimi.flashpig.repository.FeedbackRepository;
import com.gaocimi.flashpig.service.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xp
 * @date 2019-12-31 17:10:07
 * @description 对数据库feedback表进行相关操作的实现类
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {
    protected static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    @Autowired
    public FeedbackRepository feedbackRepository;

    @Override
    public List<Feedback> getFeedbackList() {
        return feedbackRepository.findAll();
    }

    @Override
    public Feedback findFeedbackById(int id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public void save(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    @Override
    public void edit(Feedback feedback) {
        feedbackRepository.save(feedback);
    }

    @Override
    public void delete(int id) {
        feedbackRepository.deleteById(id);
    }

    // 分页获得列表
    @Override
    public Page<Feedback> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");  //降序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Feedback> feedbackPage = null;
        try {
            feedbackPage = feedbackRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return feedbackPage;
    }

}


