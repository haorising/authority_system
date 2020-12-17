$(function (){
	//  给图标输入框绑定点击事件，控制下方图标div的显示与隐藏
	$('input[name="menuImg"]').on('click', function(){
		$('.icon-div').toggle();
	})

	layui.use('form', function() {
		var form = layui.form;

		form.on('radio(icon)', function(data){
	  		console.log(data.value);
	  		$('.icon-div').hide();
	  		$('input[name="menuImg"]').val(data.value);
	  		$('.layui-form-mid').empty().append("<span class='iconfont "+data.value+"'></span>");
		});
	});
})
