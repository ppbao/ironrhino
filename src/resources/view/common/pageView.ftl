<#ftl output_format='HTML'>
<!DOCTYPE html>
<html>
<head>
<title>${action.getText('pageView')}</title>
<style>
form.form-inline{
	margin-bottom: 0;
}
.row-fluid strong{
	font-size: 18px;
}
div.section{
	border: 1px solid #DEDEDE;
    border-radius: 5px;
    margin: 10px 4px;
    box-shadow: 0 0 10px rgba(189, 189, 189, 0.4);
	padding: 10px;
	margin: 20px 0;
}
div.section ul{
	margin: 0;
}
</style>
<script src="<@url value="/assets/components/flot/jquery.flot.js"/>" type="text/javascript"></script>
<script src="<@url value="/assets/components/flot/jquery.flot.time.js"/>" type="text/javascript"></script>
<script src="<@url value="/assets/components/flot/jquery.flot.pie.js"/>" type="text/javascript"></script>
<script src="<@url value="/assets/components/flot/ironrhino.flot.js"/>" type="text/javascript"></script>
<script>
Initialization.pageView = function() {
	$('#select_domain').change(function() {
				var domain = $(this).val();
				$('form').each(function() {
							$('input[name="domain"]', this).val(domain);
						});
				$('.ajaxpanel').each(function() {
							var t = $(this);
							var url = t.data('url');
							if (url.indexOf('?') > 0) {
								var uri = url.substring(0, url.indexOf('?'));
								var query = url.substring(url.indexOf('?') + 1);
								var params = query.split('&');
								var arr = [];
								for (var i = 0; i < params.length; i++) {
									var arr2 = params[i].split('=', 2);
									if (arr2[0] == 'domain') {
										if (arr2[1] == domain)
											return;
									} else {
										arr.push(params[i]);
									}
								}
								if (domain)
									arr.push('domain=' + domain);
								url = uri + '?' + arr.join('&');
							} else if (domain) {
								url += '?domain=' + domain;
							}
							t.data('url', url);
							t.trigger('load');
						});
			});
}
</script>
</head>
<body>

<#if domains?? && domains?size gt 0 && !Parameters.domain??>
<div class="row<#if fluidLayout>-fluid</#if>" style="margin-bottom:10px;">
	<span class="pull-right">
	<select id="select_domain">
		<option value=""></option>
		<#list domains as var>
		<option value="${var}"<#if domain?? && domain==var>selected</#if>>${action.getText(var)}</option>
		</#list>
	</select>
	</span>
</div>
</#if>

<div class="section pv">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('pv')}</strong>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="pv_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}</span>
<@s.textfield label="%{getText('date')}" theme="simple" id="" name="date" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="pv_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}${action.getText('range')}</span>
<@s.textfield label="%{getText('from')}" theme="simple" id="" name="from" class="date"/>
<i class="glyphicon glyphicon-arrow-right"></i>
<@s.textfield label="%{getText('to')}" theme="simple" id="" name="to" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
</div>
<div id="pv_result">
<#assign dataurl=actionBaseUrl+"/pv"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section uip">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('uip')}</strong>
</div>
<div class="span4 offset4">
<form class="ajax view form-inline" data-replacement="uip_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}${action.getText('range')}</span>
<@s.textfield label="%{getText('from')}" theme="simple" id="" name="from" class="date"/>
<i class="glyphicon glyphicon-arrow-right"></i>
<@s.textfield label="%{getText('to')}" theme="simple" id="" name="to" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
</div>
<div id="uip_result">
<#assign dataurl=actionBaseUrl+"/uip"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section usid">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('usid')}</strong>
</div>
<div class="span4 offset4">
<form class="ajax view form-inline" data-replacement="usid_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}${action.getText('range')}</span>
<@s.textfield label="%{getText('from')}" theme="simple" id="" name="from" class="date"/>
<i class="glyphicon glyphicon-arrow-right"></i>
<@s.textfield label="%{getText('to')}" theme="simple" id="" name="to" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
</div>
<div id="usid_result">
<#assign dataurl=actionBaseUrl+"/usid"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section uu">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('uu')}</strong>
</div>
<div class="span4 offset4">
<form class="ajax view form-inline" data-replacement="uu_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}${action.getText('range')}</span>
<@s.textfield label="%{getText('from')}" theme="simple" id="" name="from" class="date"/>
<i class="glyphicon glyphicon-arrow-right"></i>
<@s.textfield label="%{getText('to')}" theme="simple" id="" name="to" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
</div>
<div id="uu_result">
<#assign dataurl=actionBaseUrl+"/uu"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section url">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('url')}</strong>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="url_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}</span>
<@s.textfield label="%{getText('date')}" theme="simple" id="" name="date" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="url_result">
<@s.hidden name="domain" id=""/>
<input type="hidden" name="date" value=""/>
<@s.submit value="%{getText('total')}" theme="simple"/>
</form>
</div>
</div>
<div id="url_result">
<#assign dataurl=actionBaseUrl+"/url"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section fr">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('fr')}</strong>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="fr_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}</span>
<@s.textfield label="%{getText('date')}" theme="simple" id="" name="date" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="fr_result">
<@s.hidden name="domain" id=""/>
<input type="hidden" name="date" value=""/>
<@s.submit value="%{getText('total')}" theme="simple"/>
</form>
</div>
</div>
<div id="fr_result">
<#assign dataurl=actionBaseUrl+"/fr"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section province">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('province')}</strong>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="pr_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}</span>
<@s.textfield label="%{getText('date')}" theme="simple" id="" name="date" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="pr_result">
<@s.hidden name="domain" id=""/>
<input type="hidden" name="date" value=""/>
<@s.submit value="%{getText('total')}" theme="simple"/>
</form>
</div>
</div>
<div id="pr_result">
<#assign dataurl=actionBaseUrl+"/pr"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section city">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('city')}</strong>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="ct_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}</span>
<@s.textfield label="%{getText('date')}" theme="simple" id="" name="date" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="ct_result">
<@s.hidden name="domain" id=""/>
<input type="hidden" name="date" value=""/>
<@s.submit value="%{getText('total')}" theme="simple"/>
</form>
</div>
</div>
<div id="ct_result">
<#assign dataurl=actionBaseUrl+"/ct"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section keyword">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('keyword')}</strong>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="kw_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}</span>
<@s.textfield label="%{getText('date')}" theme="simple" id="" name="date" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="kw_result">
<@s.hidden name="domain" id=""/>
<input type="hidden" name="date" value=""/>
<@s.submit value="%{getText('total')}" theme="simple"/>
</form>
</div>
</div>
<div id="kw_result">
<#assign dataurl=actionBaseUrl+"/kw"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

<div class="section searchengine">
<div class="row-fluid">
<div class="span2 offset2">
<strong>${action.getText('searchengine')}</strong>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="se_result">
<@s.hidden name="domain" id=""/>
<span>${action.getText('date')}</span>
<@s.textfield label="%{getText('date')}" theme="simple" id="" name="date" class="date"/>
<@s.submit value="%{getText('query')}" theme="simple"/>
</form>
</div>
<div class="span4">
<form class="ajax view form-inline" data-replacement="se_result">
<@s.hidden name="domain" id=""/>
<input type="hidden" name="date" value=""/>
<@s.submit value="%{getText('total')}" theme="simple"/>
</form>
</div>
</div>
<div id="se_result">
<#assign dataurl=actionBaseUrl+"/se"/>
<#if request.queryString?has_content>
<#assign dataurl+='?'+request.queryString/>
</#if>
<div class="ajaxpanel" data-url="${dataurl}"></div>
</div>
</div>

</body>
</html>
