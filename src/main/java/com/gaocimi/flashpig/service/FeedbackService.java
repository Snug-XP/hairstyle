package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.Feedback;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-12-31 17:08:20
 * @description 对数据库feedback表进行相关操作
 */
public interface FeedbackService {

    public List<Feedback> getFeedbackList();

    public Feedback findFeedbackById(int id);

    public void save(Feedback feedback);

    public void edit(Feedback feedback);

    public void delete(int id);

    public Page<Feedback> findAll(int pageNum, int pageSize);

}
