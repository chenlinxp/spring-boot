package com.bootdo.system.dict.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.bootdo.common.controller.BaseController;
import com.bootdo.common.config.Constant;
import com.bootdo.system.dict.domain.DictDO;
import com.bootdo.system.dicttype.domain.DictTypeDO;
import com.bootdo.system.dict.service.DictService;
import com.bootdo.system.dicttype.service.DictTypeService;
import com.bootdo.common.utils.PageUtils;
import com.bootdo.common.utils.Query;
import com.bootdo.common.utils.R;




/**
 * 字典表
 * @author chenlin
 * @email 13233669915@qq.com
 * @date 2018-06-19 16:02:20
 */

@Controller
@RequestMapping("/system/dict")
public class DictController extends BaseController {
	@Autowired
	private DictService dictService;

	@Autowired
	private DictTypeService dictTypeService;

	@GetMapping()
	@RequiresPermissions("system:dict:dict")
	String dict() {
		return "system/dict/dict";
	}

	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("system:dict:dict")
	public PageUtils list(@RequestParam Map<String, Object> params) {
		// 查询列表数据
		Query query = new Query(params);
		List<DictDO> dictList = dictService.list(query);
		int total = dictService.count(query);
		PageUtils pageUtils = new PageUtils(dictList, total);
		return pageUtils;
	}

	@GetMapping("/add")
	@RequiresPermissions("system:dict:add")
	String add() {
		return "system/dict/add";
	}

	@GetMapping("/edit/{id}")
	@RequiresPermissions("system:dict:edit")
	String edit(@PathVariable("id") Long id, Model model) {
		DictDO dict = dictService.get(id);
		model.addAttribute("dict", dict);
		return "system/dict/edit";
	}

	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("system:dict:add")
	public R save(DictDO dict) {
		if (Constant.DEMO_ACCOUNT.equals(getUsername())) {
			return R.error(1, "演示系统不允许修改,完整体验请部署程序");
		}
		if (dictService.save(dict) > 0) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("system:dict:edit")
	public R update(DictDO dict) {
		if (Constant.DEMO_ACCOUNT.equals(getUsername())) {
			return R.error(1, "演示系统不允许修改,完整体验请部署程序");
		}
		dictService.update(dict);
		return R.ok();
	}

	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ResponseBody
	@RequiresPermissions("system:dict:remove")
	public R remove(Long id) {
		if (Constant.DEMO_ACCOUNT.equals(getUsername())) {
			return R.error(1, "演示系统不允许修改,完整体验请部署程序");
		}
		if (dictService.remove(id) > 0) {
			return R.ok();
		}
		return R.error();
	}

	/**
	 * 删除
	 */
	@PostMapping("/batchRemove")
	@ResponseBody
	@RequiresPermissions("system:dict:batchRemove")
	public R remove(@RequestParam("ids[]") Long[] ids) {
		if (Constant.DEMO_ACCOUNT.equals(getUsername())) {
			return R.error(1, "演示系统不允许修改,完整体验请部署程序");
		}
		dictService.batchRemove(ids);
		return R.ok();
	}

	@GetMapping("/type")
	@ResponseBody
	public List<DictDO> listType() {

		return dictService.listType();
	};


	// 类别已经指定增加
	@GetMapping("/add/{type}/{description}")
	@RequiresPermissions("system:dict:add")
	String addD(Model model, @PathVariable("type") String type, @PathVariable("description") String description) {

		model.addAttribute("type", type);
		model.addAttribute("description", description);
		return "system/dict/add";
	}

	@ResponseBody
	@GetMapping("/list/{type}")
	public List<DictDO> listByType(@PathVariable("type") String type) {
		// 查询列表数据
		Map<String, Object> map = new HashMap<>(16);
		map.put("type", type);
		List<DictDO> dictList = dictService.list(map);
		return dictList;
	}

	/**
	 *数据字典索引的列表
	 */
	@GetMapping("/dicttype")
	@ResponseBody
	public List<DictTypeDO> listDictType(@RequestParam("dname") String dname) {

		return dictTypeService.list(dname);
	};

	@GetMapping("/addtype")
//	@RequiresPermissions("common:dict:add")
	String adddict() {
		return "system/dict/addtype";
	}


	/**
	 * 保存数据字典索引
	 */
	@ResponseBody
	@PostMapping("/savetype")
	//@RequiresPermissions("common:dict:add")
	public R savetype(DictTypeDO dicttype) {
		if (dictTypeService.save(dicttype) > 0) {
			return R.ok();
		}
		return R.error();
	}
}