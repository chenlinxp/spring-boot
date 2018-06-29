package com.bootdo.system.log.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bootdo.system.log.dao.LogDao;
import com.bootdo.system.log.domain.LogDO;
import com.bootdo.common.domain.PageDO;
import com.bootdo.system.log.service.LogService;
import com.bootdo.common.utils.Query;

/**
 * 系统日志
 *
 * @author chenlin
 * @email 13233669915@qq.com
 * @date 2018-06-19 16:02:20
 */
@Service
public class LogServiceImpl implements LogService {
	@Autowired
	LogDao logdao;

	@Async
	@Override
	public void save(LogDO logDO) {
		logdao.save(logDO);
	}

	@Override
	public PageDO<LogDO> queryList(Query query) {
		int total = logdao.count(query);
		List<LogDO> logs = logdao.list(query);
		PageDO<LogDO> page = new PageDO<>();
		page.setTotal(total);
		page.setRows(logs);
		return page;
	}

	@Override
	public int remove(Long id) {
		int count = logdao.remove(id);
		return count;
	}

	@Override
	public int batchRemove(Long[] ids){
		return logdao.batchRemove(ids);
	}
}
