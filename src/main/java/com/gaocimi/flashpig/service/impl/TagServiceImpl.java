package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Tag;
import com.gaocimi.flashpig.repository.TagRepository;
import com.gaocimi.flashpig.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xp
 * @date 2019-11-6 12:58:43
 * @description 对数据库tag表进行相关操作的实现类
 */
@Service
public class TagServiceImpl implements TagService {
    protected static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

	@Autowired
    public TagRepository tagRepository;

    @Override
    public List<Tag> getTagList() {
        return tagRepository.findAll();
    }

    @Override
    public Tag findById(int id) {
        return tagRepository.findById(id);
    }
    
    @Override
    public Tag findByTagName(String tagName) {
    	return tagRepository.findByTagName(tagName);
    }

    @Override
    public List<Tag> findByTagNameLike(String tagName){
        return tagRepository.findAllByTagNameLike("%"+tagName+"%");
    }


    @Override
    public void save(Tag tag) {
        tagRepository.save(tag);
    }

    @Override
    public void edit(Tag tag) {
        tagRepository.save(tag);
    }

    @Override
    public void delete(int id) {
        tagRepository.deleteById(id);
    }

     
    public List<Tag> findAll()
    {
    	return tagRepository.findAll();
    }
    
	// 分页获得列表
	@Override
	public Page<Tag> findAll(int pageNum, int pageSize) {
		Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
	    Pageable pageable = PageRequest.of(pageNum,pageSize,sort);
	    
	    Page<Tag> TagPage = null;
	    try {
	        TagPage = tagRepository.findAll(pageable);
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
            logger.info("查询记录出错");
	        return null;
	    }
	    return TagPage;
	}
}


