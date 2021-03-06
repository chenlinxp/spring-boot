package com.justdo.blog.service;

import com.justdo.blog.domain.ContentDO;

import java.util.List;
import java.util.Map;

/**
 * 文章内容
 * 
 * @author chglee
 * @email
 * @date 2017-09-09 10:03:34
 */
public interface ContentService {
	
	ContentDO get(Long cid);
	
	List<ContentDO> list(Map<String, Object> map);
	
	int count(Map<String, Object> map);
	
	int save(ContentDO bContent);
	
	int update(ContentDO bContent);
	
	int remove(Long cid);
	
	int batchDel(Long[] cids);
}
