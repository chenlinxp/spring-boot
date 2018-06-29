package com.bootdo.system.log.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bootdo.system.log.domain.LogDO;
import com.bootdo.common.domain.PageDO;
import com.bootdo.common.utils.Query;

/**
 * 系统日志
 *
 * @author chenlin
 * @email 13233669915@qq.com
 * @date 2018-06-19 16:02:20
 */
@Service
public interface LogService {
	void save(LogDO logDO);
	PageDO<LogDO> queryList(Query query);
	int remove(Long id);
	int batchRemove(Long[] ids);
}
