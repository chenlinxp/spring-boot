package com.bootdo.system.regexp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.bootdo.system.regexp.dao.RegexpDao;
import com.bootdo.system.regexp.domain.RegexpDO;
import com.bootdo.system.regexp.service.RegexpService;



@Service
public class RegexpServiceImpl implements RegexpService {
	@Autowired
	private RegexpDao regexpDao;
	
	@Override
	public RegexpDO get(String rid){
		return regexpDao.get(rid);
	}
	
	@Override
	public List<RegexpDO> list(Map<String, Object> map){
		return regexpDao.list(map);
	}
	
	@Override
	public int count(Map<String, Object> map){
		return regexpDao.count(map);
	}
	
	@Override
	public int save(RegexpDO regexp){
		return regexpDao.save(regexp);
	}
	
	@Override
	public int update(RegexpDO regexp){
		return regexpDao.update(regexp);
	}
	
	@Override
	public int remove(String rid){
		return regexpDao.remove(rid);
	}
	
	@Override
	public int batchRemove(String[] rids){
		return regexpDao.batchRemove(rids);
	}
	
}
