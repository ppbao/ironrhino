<#ftl output_format='HTML'>
<!DOCTYPE html>
<html>
<head>
<title>${action.getText('service')}${action.getText('stats')}</title>
<style>
form.form-inline{
	margin-bottom: 0;
}
div.section{
	border: 1px solid #DEDEDE;
    border-radius: 5px;
    margin: 10px 4px;
    box-shadow: 0 0 10px rgba(189, 189, 189, 0.4);
	padding: 10px;
	margin: 20px 0;
}
#count:empty{
	display: none;
}
div.section ul{
	margin: 0;
}
</style>
<script src="<@url value="/assets/components/flot/jquery.flot.js"/>" type="text/javascript"></script>
<script src="<@url value="/assets/components/flot/jquery.flot.time.js"/>" type="text/javascript"></script>
<script src="<@url value="/assets/components/flot/ironrhino.flot.js"/>" type="text/javascript"></script>
<script>
function toggleUrl(url,type){
	if (url.indexOf('?') > 0) {
		var uri = url.substring(0, url.indexOf('?'));
		var query = url.substring(url.indexOf('?') + 1);
		var params = query.split('&');
		var arr = [];
		for (var i = 0; i < params.length; i++) {
			var arr2 = params[i].split('=', 2);
			if (arr2[0] == 'type') {
				continue;
			} else {
				arr.push(params[i]);
			}
		}
		arr.push('type=' + type);
		url = uri + '?' + arr.join('&');
	} else {
		url += '?type=' + type;
	}
	return url;
}
Initialization.stats = function() {
	$('#side button').click(function() {
				var type = $(this).data('type');
				$('a.ajax.view').each(function(){
					this.href = toggleUrl(this.href,type);
				});
				$('form.ajax.view').each(function(){
					this.action = toggleUrl(this.action,type);
				});
				$('.ajaxpanel').each(function() {
					var t = $(this);
					t.data('url', toggleUrl(t.data('url'),type));
					t.trigger('load');
				});
			});
}
</script>
</head>
<body>

<div class="row<#if fluidLayout>-fluid</#if>">
	<div id="side" class="btn-group btn-switch span2 offset5" style="margin-bottom:10px;">
	<#list statics['org.ironrhino.core.remoting.StatsType'].values() as var>
	  <button class="btn<#if !type??||type.name()==var.name()> active</#if>" data-type="${var.name()}">${var}</button>
	</#list>  
	</div>
</div>

<div class="accordion" id="services-accordion">
<#assign baseurl=actionBaseUrl>
<#if request.queryString?has_content>
<#list request.queryString?split('&') as pair>
	<#assign name=pair?keep_before('=')>
	<#if name!='_'&&name!='service'>
		<#assign baseurl+=baseurl?contains('?')?then('&','?')+pair>
	</#if>
</#list>
</#if>
<#list services as key,value>	
  <div class="accordion-group">
    <div class="accordion-heading">
      <a class="accordion-toggle" data-toggle="collapse" data-parent="#services-accordion" href="#service-${key?index}">
     	<h4>${key}</h4>
      </a>
    </div>
    <div id="service-${key?index}" class="accordion-body collapse<#if service?? && service?keep_before_last('(')?keep_before_last('.')==key> in</#if>">
      <div class="accordion-inner">
        <ul class="nav nav-list">
        	<#list value as var>
			<li><a href="<@url value="${baseurl+baseurl?contains('?')?then('&','?')+'service='+(key+'.'+var)?url}"/>" class="ajax view" data-replacement="count">${var}</a></li>
			</#list>
		</ul>
      </div>
    </div>
  </div>
 </#list> 
 </div> 
		  

<div id="count" class="section"><#if service?has_content>
<div class="row-fluid">
<div class="span12">
<h4>${service}</h4>
</div>
</div>
<div class="row-fluid">
<div class="span5 offset1">
<#assign baseaction=actionBaseUrl>
<#if request.queryString?has_content>
<#list request.queryString?split('&') as pair>
	<#assign name=pair?keep_before('=')>
	<#if name!='_'&&name!='date'&&name!='from'&&name!='to'>
		<#assign baseaction+=baseaction?contains('?')?then('&','?')+pair>
	</#if>
</#list>
</#if>
<form action="${baseaction}" class="ajax view form-inline" data-replacement="count_result">
<span>${action.getText('date')}</span>
<@s.textfield label="%{getText('date')}" theme="simple" id="" name="date" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
<div class="span5 offset1">
<form action="${baseaction}" class="ajax view form-inline" data-replacement="count_result">
<span>${action.getText('date')}${action.getText('range')}</span>
<@s.textfield label="%{getText('from')}" theme="simple" id="" name="from" class="date"/>
<i class="glyphicon glyphicon-arrow-right"></i>
<@s.textfield label="%{getText('to')}" theme="simple" id="" name="to" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
</div>
<div id="count_result">
<#assign dataurl=actionBaseUrl+"/count"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</#if><#if service?has_content>
<div class="ajaxpanel" data-url="${actionBaseUrl+"/samples?service="+service?url}" data-interval="60000" data-quiet="true"></div>
</#if></div>

<#assign dataurl=actionBaseUrl+"/hotspots"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}" data-interval="60000" data-quiet="true"></div>

<#assign dataurl=actionBaseUrl+"/warnings"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}" data-interval="60000" data-quiet="true"></div>

</body>
</html>
