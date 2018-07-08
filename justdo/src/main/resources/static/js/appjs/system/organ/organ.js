
var prefix = "/system/organ"
$(function() {
	load();
});

function load() {
	$('#bTable')
			.bootstrapTable(
					{
						method : 'get', // 服务器数据的请求方式 get or post
						url : prefix + "/list", // 服务器数据的加载地址
					//	showRefresh : true,
					//	showToggle : true,
					//	showColumns : true,
						iconSize : 'outline',
						toolbar : '#bToolbar',
						striped : true, // 设置为true会有隔行变色效果
						dataType : "json", // 服务器返回的数据类型
						pagination : true, // 设置为true会在底部显示分页条
						// queryParamsType : "limit",
						// //设置为limit则会发送符合RESTFull格式的参数
						singleSelect : false, // 设置为true将禁止多选
						// contentType : "application/x-www-form-urlencoded",
						// //发送到服务器的数据编码类型
						pageSize : 10, // 如果设置了分页，每页数据条数
						pageNumber : 1, // 如果设置了分布，首页页码
						//search : true, // 是否显示搜索框
						showColumns : false, // 是否显示内容下拉框（选择显示的列）
						sidePagination : "server", // 设置在哪里进行分页，可选值为"client" 或者 "server"
						queryParams : function(params) {
							return {
								//说明：传入后台的参数包括offset开始索引，limit步长，sort排序列，order：desc或者,以及所有列的键值对
								limit: params.limit,
								offset:params.offset
					           // name:$('#searchName').val(),
					           // username:$('#searchName').val()
							};
						},
						// //请求服务器数据时，你可以通过重写参数的方式添加一些额外的参数，例如 toolbar 中的参数 如果
						// queryParamsType = 'limit' ,返回参数必须包含
						// limit, offset, search, sort, order 否则, 需要包含:
						// pageSize, pageNumber, searchText, sortName,
						// sortOrder.
						// 返回false将会终止请求
						columns : [
								{
									checkbox : true
								},
								{
									field : 'SerialNumber',
									title : '序号',
									align : 'center',
									width : '30px',
									formatter: function (value ,row ,index){
										var pageNumber=$('#bTable').bootstrapTable("getOptions").pageNumber;
										var pageSize=$('#bTable').bootstrapTable("getOptions").pageSize;
										return (pageNumber-1)*pageSize+index+1;
									}
								},
								{
									field : 'organid', 
									title : '机构ID',
	                                visible : false
								},
								{
									field : 'organpid', 
									title : '机构上级ID' 
								},
								{
									field : 'organname', 
									title : '机构名称' 
								},
								{
									field : 'areaid', 
									title : '机构所属的地区编号' 
								},
								{
									field : 'organcode', 
									title : '机构编码' 
								},
								{
									field : 'organalias', 
									title : '机构别名' 
								},
								{
									field : 'postcode', 
									title : '邮编' 
								},
								{
									field : 'address', 
									title : '机构地址' 
								},
								{
									field : 'telephone', 
									title : '机构电话' 
								},
								{
									field : 'fax', 
									title : '机构的传真号码' 
								},
								{
									field : 'isvalidation', 
									title : '是否有效' 
								},
								{
									field : 'organman', 
									title : '负责人姓名' 
								},
								{
									field : 'organmanid', 
									title : '' 
								},
								{
									field : 'remark', 
									title : '机构备注信息' 
								},
								{
									title : '操作',
									field : 'operation',
									align : 'center',
									formatter : function(value, row, index) {
										var e = '<a class="btn btn-primary btn-sm '+s_edit_h+'" href="#" mce_href="#" title="编辑" onclick="edit(\''
												+ row.organid
												+ '\')"><i class="fa fa-edit"></i></a> ';
										var d = '<a class="btn btn-warning btn-sm '+s_delete_h+'" href="#" title="删除"  mce_href="#" onclick="del(\''
												+ row.organid
												+ '\')"><i class="fa fa-remove"></i></a> ';
										return e + d ;
									}
								} ]
					});
}
function reLoad() {
	$('#bTable').bootstrapTable('refresh');
}
function add() {
	layer.open({
		type : 2,
		title : '增加',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : prefix + '/add' // iframe的url
	});
}
function edit(id) {
	if(id==undefined){
        var rows = $('#bTable').bootstrapTable('getSelections'); // 返回所有选择的行，当没有选择的记录时，返回一个空数组
        if (!rows.length == 1) {
            layer.msg("请选择一条要修改的数据");
            return;
        }else{
        	id=row[0]['organid'];
        }
    }
	layer.open({
		type : 2,
		title : '编辑',
		maxmin : true,
		shadeClose : false, // 点击遮罩关闭层
		area : [ '800px', '520px' ],
		content : prefix + '/edit/' + id // iframe的url
	});
}
function del(id) {
	layer.confirm('确定要删除选中的记录？', {
		btn : [ '确定', '取消' ]
	}, function() {
		$.ajax({
			url : prefix+"/del",
			type : "post",
			data : {
				'organid' : id
			},
			success : function(r) {
				if (r.code==0) {
					layer.msg(r.msg);
					reLoad();
				}else{
					layer.msg(r.msg);
				}
			}
		});
	})
}
function batchDel() {
	var rows = $('#bTable').bootstrapTable('getSelections'); // 返回所有选择的行，当没有选择的记录时，返回一个空数组
	if (rows.length == 0) {
		layer.msg("请选择要删除的数据");
		return;
	}
	layer.confirm("确认要删除选中的'" + rows.length + "'条数据吗?", {
		btn : [ '确定', '取消' ]
	// 按钮
	}, function() {
		var ids = new Array();
		// 遍历所有选择的行数据，取每条数据对应的ID
		$.each(rows, function(i, row) {
			ids[i] = row['organid'];
		});
		$.ajax({
			type : 'POST',
			data : {
				"ids" : ids
			},
			url : prefix + '/batchDel',
			success : function(r) {
				if (r.code == 0) {
					layer.msg(r.msg);
					reLoad();
				} else {
					layer.msg(r.msg);
				}
			}
		});
	}, function() {

	});
}