package com.eolinker.service.impl;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eolinker.mapper.ApiCacheMapper;
import com.eolinker.mapper.EnvFrontUriMapper;
import com.eolinker.mapper.EnvHeaderMapper;
import com.eolinker.mapper.EnvMapper;
import com.eolinker.mapper.EnvParamAdditionalMapper;
import com.eolinker.mapper.EnvParamMapper;
import com.eolinker.mapper.ApiGroupMapper;
import com.eolinker.mapper.ApiMapper;
import com.eolinker.mapper.DocumentGroupMapper;
import com.eolinker.mapper.DocumentMapper;
import com.eolinker.mapper.PartnerMapper;
import com.eolinker.mapper.ProjectMapper;
import com.eolinker.mapper.StatusCodeGroupMapper;
import com.eolinker.mapper.StatusCodeMapper;
import com.eolinker.pojo.Api;
import com.eolinker.pojo.ApiCache;
import com.eolinker.pojo.Env;
import com.eolinker.pojo.EnvFrontUri;
import com.eolinker.pojo.EnvHeader;
import com.eolinker.pojo.EnvParam;
import com.eolinker.pojo.EnvParamAdditional;
import com.eolinker.pojo.ApiGroup;
import com.eolinker.pojo.ApiHeader;
import com.eolinker.pojo.ApiRequestParam;
import com.eolinker.pojo.ApiRequestValue;
import com.eolinker.pojo.ApiResultParam;
import com.eolinker.pojo.ApiResultValue;
import com.eolinker.pojo.Document;
import com.eolinker.pojo.DocumentGroup;
import com.eolinker.pojo.Partner;
import com.eolinker.pojo.Project;
import com.eolinker.pojo.StatusCode;
import com.eolinker.pojo.StatusCodeGroup;
import com.eolinker.service.ImportService;
/**
 * 导入项目[业务处理层]
 * @name eolinker ams open source，eolinker开源版本
 * @link https://www.eolinker.com/
 * @package eolinker
 * @author www.eolinker.com 广州银云信息科技有限公司 2015-2018
 * eoLinker是目前全球领先、国内最大的在线API接口管理平台，提供自动生成API文档、API自动化测试、Mock测试、团队协作等功能，旨在解决由于前后端分离导致的开发效率低下问题。
 * 如在使用的过程中有任何问题，欢迎加入用户讨论群进行反馈，我们将会以最快的速度，最好的服务态度为您解决问题。
 *
 * eoLinker AMS开源版的开源协议遵循Apache License2.0，如需获取最新的eolinker开源版以及相关资讯，请访问:https://www.eolinker.com/#/os/download
 *
 * 官方网站：https://www.eolinker.com/ 官方博客以及社区：http://blog.eolinker.com/
 * 使用教程以及帮助：http://help.eolinker.com/ 商务合作邮箱：market@eolinker.com
 * 用户讨论QQ群：707530721
 */
@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="java.lang.Exception")
public class ImportServiceImpl implements ImportService
{

	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ApiGroupMapper apiGroupMapper;
	@Autowired
	private PartnerMapper partnerMapper;
	@Autowired
	private ApiMapper apiMapper;
	@Autowired
	private ApiCacheMapper apiCacheMapper;
	@Autowired
	private StatusCodeMapper statusCodeMapper;
	@Autowired
	private EnvFrontUriMapper envFrontUriMapper;
	@Autowired
	private EnvHeaderMapper envHeaderMapper;
	@Autowired
	private EnvMapper envMapper;
	@Autowired
	private EnvParamMapper envParamMapper;
	@Autowired
	private EnvParamAdditionalMapper envParamAdditionalMapper;
	@Autowired
	private StatusCodeGroupMapper statusCodeGroupMapper;
	@Autowired
	private DocumentGroupMapper documentGroupMapper;
	@Autowired
	private DocumentMapper documentMapper;

	/**
	 * 导入eolinker项目
	 */
	@Override
	public boolean importEoapi(String data, Integer userID)
	{
		// TODO Auto-generated method stub
		JSONObject projectData = JSONObject.parseObject(data);
		if (projectData != null && !projectData.isEmpty())
		{
			JSONObject projectInfo = JSONObject.parseObject(projectData.getString("projectInfo"));
			Project project = new Project();
			project.setProjectName(projectInfo.getString("projectName"));
			project.setProjectType(projectInfo.getInteger("projectType"));
			project.setProjectVersion(projectInfo.getString("projectVersion"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = null;
			Timestamp updateTime = null;
			try
			{
				date = dateFormat.parse(projectInfo.getString("projectUpdateTime"));
				updateTime = new Timestamp(date.getTime());
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				date = new Date();
				updateTime = new Timestamp(date.getTime());
			}
			project.setProjectUpdateTime(updateTime);
			if (projectMapper.addProject(project) < 1)
				throw new RuntimeException("addProject error");
			Partner partner = new Partner();
			partner.setProjectID(project.getProjectID());
			partner.setUserID(userID);
			partner.setUserType(0);
			if (partnerMapper.addPartner(partner) < 1)
				throw new RuntimeException("addPartner error");
			JSONArray apiGroupList = JSONArray.parseArray(projectData.getString("apiGroupList"));
			if (apiGroupList != null && !apiGroupList.isEmpty())
			{
				for (Iterator<Object> iterator = apiGroupList.iterator(); iterator.hasNext();)
				{
					JSONObject groupData = (JSONObject) iterator.next();
					ApiGroup apiGroup = new ApiGroup();
					apiGroup.setGroupName(groupData.getString("groupName"));
					apiGroup.setProjectID(project.getProjectID());
					apiGroup.setIsChild(0);
					apiGroup.setParentGroupID(0);
					if (apiGroupMapper.addApiGroup(apiGroup) < 1)
						throw new RuntimeException("addApiGroup error");
					JSONArray apiList = JSONArray.parseArray(groupData.getString("apiList"));
					if (apiList != null && !apiList.isEmpty())
					{
						for (Iterator<Object> iterator1 = apiList.iterator(); iterator1.hasNext();)
						{
							JSONObject apiInfo = (JSONObject) iterator1.next();
							JSONObject baseInfo = (JSONObject) apiInfo.get("baseInfo");
							JSONObject mockInfo = (JSONObject) apiInfo.get("mockInfo");
							Api api = new Api();
							api.setApiName(baseInfo.getString("apiName"));
							api.setApiURI(baseInfo.getString("apiURI"));
							api.setApiProtocol(baseInfo.getInteger("apiProtocol"));
							api.setApiSuccessMock(baseInfo.getString("apiSuccessMock"));
							api.setApiFailureMock(baseInfo.getString("apiFailureMock"));
							api.setApiRequestType(baseInfo.getInteger("apiRequestType"));
							api.setApiStatus(baseInfo.getInteger("apiStatus"));
							api.setStarred(baseInfo.getInteger("starred"));
							api.setGroupID(apiGroup.getGroupID());
							api.setProjectID(project.getProjectID());
							api.setApiNoteType(baseInfo.getInteger("apiNoteType"));
							api.setApiNoteRaw(baseInfo.getString("apiNoteRaw"));
							api.setApiNote(baseInfo.getString("apiNote"));
							SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date date1 = null;
							Timestamp updateTime1 = null;
							try
							{
								date1 = dateFormat1.parse(baseInfo.getString("apiUpdateTime"));
								updateTime1 = new Timestamp(date1.getTime());
							}
							catch (ParseException e)
							{
								// TODO Auto-generated catch block
								date1 = new Date();
								updateTime1 = new Timestamp(date1.getTime());
							}
							api.setApiUpdateTime(updateTime1);
							api.setApiRequestParamType(baseInfo.getInteger("apiRequestParamType"));
							api.setApiRequestRaw(baseInfo.getString("apiRequestRaw"));
							api.setUpdateUserID(userID);
							api.setMockConfig(mockInfo.getString("mockConfig"));
							api.setMockRule(mockInfo.getString("mockRule"));
							api.setMockResult(mockInfo.getString("mockResult"));
							if (apiMapper.addApi(api) < 1)
								throw new RuntimeException("addApi error");
							ApiCache apiCache = new ApiCache();
							apiCache.setApiID(api.getApiID());
							apiCache.setApiJson(JSON.toJSONString(apiInfo));
							apiCache.setGroupID(api.getGroupID());
							apiCache.setProjectID(api.getProjectID());
							apiCache.setStarred(api.getStarred());
							apiCache.setUpdateUserID(api.getUpdateUserID());
							if (apiCacheMapper.addApiCache(apiCache) < 1)
								throw new RuntimeException("addApiCache error");
							JSONArray headerList = (JSONArray) apiInfo.get("headerInfo");
							if (headerList != null && !headerList.isEmpty())
							{
								for (Iterator<Object> iterator2 = headerList.iterator(); iterator2.hasNext();)
								{
									JSONObject headerInfo = (JSONObject) iterator2.next();
									ApiHeader header = new ApiHeader();
									header.setHeaderName(headerInfo.getString("headerName"));
									header.setHeaderValue(headerInfo.getString("headerValue"));
									header.setApiID(api.getApiID());
									if (apiMapper.addApiHeader(header) < 1)
										throw new RuntimeException("addApiHeader error");
								}
							}
							JSONArray requestParamList = (JSONArray) apiInfo.get("requestInfo");
							if (requestParamList != null && !requestParamList.isEmpty())
							{
								for (Iterator<Object> iterator2 = requestParamList.iterator(); iterator2.hasNext();)
								{
									JSONObject requestInfo = (JSONObject) iterator2.next();
									ApiRequestParam requestParam = new ApiRequestParam();
									requestParam.setApiID(api.getApiID());
									requestParam.setParamName(requestInfo.getString("paramName"));
									requestParam.setParamKey(requestInfo.getString("paramKey"));
									requestParam.setParamValue(requestInfo.getString("paramValue"));
									requestParam.setParamType(requestInfo.getInteger("paramType"));
									requestParam.setParamLimit(requestInfo.getString("paramLimit"));
									requestParam.setParamNotNull(requestInfo.getInteger("paramNotNull"));
									if (apiMapper.addRequestParam(requestParam) < 1)
										throw new RuntimeException("addRequestParam error");
									JSONArray paramValueList = (JSONArray) requestInfo.get("paramValueList");
									for (Iterator<Object> iterator21 = paramValueList.iterator(); iterator21.hasNext();)
									{
										JSONObject paramValue = (JSONObject) iterator21.next();
										ApiRequestValue apiRequestValue = new ApiRequestValue();
										apiRequestValue.setParamID(requestParam.getParamID());
										apiRequestValue.setValue(paramValue.getString("value"));
										apiRequestValue.setValueDescription(paramValue.getString("valueDescription"));
										if (apiMapper.addRequestValue(apiRequestValue) < 1)
											throw new RuntimeException("apiRequestValue error");
									}
								}
							}
							JSONArray resultParamList = (JSONArray) apiInfo.get("resultInfo");
							if (resultParamList != null && !resultParamList.isEmpty())
							{
								for (Iterator<Object> iterator2 = resultParamList.iterator(); iterator2.hasNext();)
								{
									JSONObject resultInfo = (JSONObject) iterator2.next();
									ApiResultParam resultParam = new ApiResultParam();
									resultParam.setApiID(api.getApiID());
									resultParam.setParamName(resultInfo.getString("paramName"));
									resultParam.setParamKey(resultInfo.getString("paramKey"));
									resultParam.setParamNotNull(resultInfo.getInteger("paramNotNull"));
									if (apiMapper.addResultParam(resultParam) < 1)
										throw new RuntimeException("addResultParam error");
									JSONArray paramValueList = (JSONArray) resultInfo.get("paramValueList");
									for (Iterator<Object> iterator21 = paramValueList.iterator(); iterator21.hasNext();)
									{
										JSONObject paramValue = (JSONObject) iterator21.next();
										ApiResultValue apiResultValue = new ApiResultValue();
										apiResultValue.setParamID(resultParam.getParamID());
										apiResultValue.setValue(paramValue.getString("value"));
										apiResultValue.setValueDescription(paramValue.getString("valueDescription"));
										if (apiMapper.addResultValue(apiResultValue) < 1)
											throw new RuntimeException("addResultValue error");
									}
								}
							}
						}
					}
					JSONArray childGroupList = JSONArray.parseArray(groupData.getString("apiGroupChildList"));
					if (childGroupList != null && !childGroupList.isEmpty())
					{
						for (Iterator<Object> iterator1 = childGroupList.iterator(); iterator1.hasNext();)
						{
							JSONObject childGroupInfo = (JSONObject) iterator1.next();
							ApiGroup apiChildGroup = new ApiGroup();
							apiChildGroup.setGroupName(childGroupInfo.getString("groupName"));
							apiChildGroup.setIsChild(1);
							apiChildGroup.setProjectID(project.getProjectID());
							apiChildGroup.setParentGroupID(apiGroup.getGroupID());
							if (apiGroupMapper.addApiGroup(apiChildGroup) < 1)
								throw new RuntimeException("addApiChildGroup error");
							JSONArray apiList1 = JSONArray.parseArray(childGroupInfo.getString("apiList"));
							if (apiList1 != null && !apiList1.isEmpty())
							{
								for (Iterator<Object> iterator11 = apiList1.iterator(); iterator11.hasNext();)
								{
									JSONObject apiInfo = (JSONObject) iterator11.next();
									JSONObject baseInfo = (JSONObject) apiInfo.get("baseInfo");
									JSONObject mockInfo = (JSONObject) apiInfo.get("mockInfo");
									Api api = new Api();
									api.setApiName(baseInfo.getString("apiName"));
									api.setApiURI(baseInfo.getString("apiURI"));
									api.setApiProtocol(baseInfo.getInteger("apiProtocol"));
									api.setApiSuccessMock(baseInfo.getString("apiSuccessMock"));
									api.setApiFailureMock(baseInfo.getString("apiFailureMock"));
									api.setApiRequestType(baseInfo.getInteger("apiRequestType"));
									api.setApiStatus(baseInfo.getInteger("apiStatus"));
									api.setStarred(baseInfo.getInteger("starred"));
									api.setGroupID(apiChildGroup.getGroupID());
									api.setProjectID(project.getProjectID());
									api.setApiNoteType(baseInfo.getInteger("apiNoteType"));
									api.setApiNoteRaw(baseInfo.getString("apiNoteRaw"));
									api.setApiNote(baseInfo.getString("apiNote"));
									SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									Date date1 = null;
									Timestamp updateTime1 = null;
									try
									{
										date1 = dateFormat1.parse(baseInfo.getString("apiUpdateTime"));
										updateTime1 = new Timestamp(date1.getTime());
									}
									catch (ParseException e)
									{
										// TODO Auto-generated catch block
										date1 = new Date();
										updateTime1 = new Timestamp(date1.getTime());
									}
									api.setApiUpdateTime(updateTime1);
									api.setApiRequestParamType(baseInfo.getInteger("apiRequestParamType"));
									api.setApiRequestRaw(baseInfo.getString("apiRequestRaw"));
									api.setUpdateUserID(userID);
									api.setMockConfig(mockInfo.getString("mockConfig"));
									api.setMockRule(mockInfo.getString("mockRule"));
									api.setMockResult(mockInfo.getString("mockResult"));
									if (apiMapper.addApi(api) < 1)
										throw new RuntimeException("addApi error");
									ApiCache apiCache = new ApiCache();
									apiCache.setApiID(api.getApiID());
									apiCache.setApiJson(JSON.toJSONString(apiInfo));
									apiCache.setGroupID(api.getGroupID());
									apiCache.setProjectID(api.getProjectID());
									apiCache.setStarred(api.getStarred());
									apiCache.setUpdateUserID(api.getUpdateUserID());
									if (apiCacheMapper.addApiCache(apiCache) < 1)
										throw new RuntimeException("addApiCache error");
									JSONArray headerList = (JSONArray) apiInfo.get("headerInfo");
									if (headerList != null && !headerList.isEmpty())
									{
										for (Iterator<Object> iterator111 = headerList.iterator(); iterator111
												.hasNext();)
										{
											JSONObject headerInfo = (JSONObject) iterator111.next();
											ApiHeader header = new ApiHeader();
											header.setHeaderName(headerInfo.getString("headerName"));
											header.setHeaderValue(headerInfo.getString("headerValue"));
											header.setApiID(api.getApiID());
											if (apiMapper.addApiHeader(header) < 1)
												throw new RuntimeException("addApiHeader error");
										}
									}
									JSONArray requestParamList = (JSONArray) apiInfo.get("requestInfo");
									if (requestParamList != null && !requestParamList.isEmpty())
									{
										for (Iterator<Object> iterator111 = requestParamList.iterator(); iterator111
												.hasNext();)
										{
											JSONObject requestInfo = (JSONObject) iterator111.next();
											ApiRequestParam requestParam = new ApiRequestParam();
											requestParam.setApiID(api.getApiID());
											requestParam.setParamName(requestInfo.getString("paramName"));
											requestParam.setParamKey(requestInfo.getString("paramKey"));
											requestParam.setParamValue(requestInfo.getString("paramValue"));
											requestParam.setParamType(requestInfo.getInteger("paramType"));
											requestParam.setParamLimit(requestInfo.getString("paramLimit"));
											requestParam.setParamNotNull(requestInfo.getInteger("paramNotNull"));
											if (apiMapper.addRequestParam(requestParam) < 1)
												throw new RuntimeException("addRequestParam error");
											JSONArray paramValueList = (JSONArray) requestInfo.get("paramValueList");
											for (Iterator<Object> iterator2 = paramValueList.iterator(); iterator2
													.hasNext();)
											{
												JSONObject paramValue = (JSONObject) iterator2.next();
												ApiRequestValue apiRequestValue = new ApiRequestValue();
												apiRequestValue.setParamID(requestParam.getParamID());
												apiRequestValue.setValue(paramValue.getString("value"));
												apiRequestValue
														.setValueDescription(paramValue.getString("valueDescription"));
												if (apiMapper.addRequestValue(apiRequestValue) < 1)
													throw new RuntimeException("apiRequestValue error");
											}
										}
									}
									JSONArray resultParamList = (JSONArray) apiInfo.get("resultInfo");
									if (resultParamList != null && !resultParamList.isEmpty())
									{
										for (Iterator<Object> iterator111 = resultParamList.iterator(); iterator111
												.hasNext();)
										{
											JSONObject resultInfo = (JSONObject) iterator111.next();
											ApiResultParam resultParam = new ApiResultParam();
											resultParam.setApiID(api.getApiID());
											resultParam.setParamName(resultInfo.getString("paramName"));
											resultParam.setParamKey(resultInfo.getString("paramKey"));
											resultParam.setParamNotNull(resultInfo.getInteger("paramNotNull"));
											if (apiMapper.addResultParam(resultParam) < 1)
												throw new RuntimeException("addResultParam error");
											JSONArray paramValueList = (JSONArray) resultInfo.get("paramValueList");
											for (Iterator<Object> iterator2 = paramValueList.iterator(); iterator2
													.hasNext();)
											{
												JSONObject paramValue = (JSONObject) iterator2.next();
												ApiResultValue apiResultValue = new ApiResultValue();
												apiResultValue.setParamID(resultParam.getParamID());
												apiResultValue.setValue(paramValue.getString("value"));
												apiResultValue
														.setValueDescription(paramValue.getString("valueDescription"));
												if (apiMapper.addResultValue(apiResultValue) < 1)
													throw new RuntimeException("addResultValue error");
											}
										}
									}
								}
							}
						}
					}
				}
			}
			JSONArray statusCodeGroupList = JSONArray.parseArray(projectData.getString("statusCodeGroupList"));
			if (statusCodeGroupList != null && !statusCodeGroupList.isEmpty())
			{
				for (Iterator<Object> iterator1 = statusCodeGroupList.iterator(); iterator1.hasNext();)
				{

					JSONObject statusCodeGroupData = (JSONObject) iterator1.next();
					StatusCodeGroup statusCodeGroup = new StatusCodeGroup();
					statusCodeGroup.setProjectID(project.getProjectID());
					statusCodeGroup.setGroupName(statusCodeGroupData.getString("groupName"));
					if (statusCodeGroupMapper.addGroup(statusCodeGroup) < 1)
						throw new RuntimeException("addStatusGroup error");
					JSONArray statusCodeList = JSONArray.parseArray(statusCodeGroupData.getString("statusCodeList"));
					// 插入文档
					if (statusCodeList != null && !statusCodeList.isEmpty())
					{
						for (Iterator<Object> iterator11 = statusCodeList.iterator(); iterator11.hasNext();)
						{
							JSONObject statusCodeData = (JSONObject) iterator11.next();
							StatusCode statusCode = new StatusCode();
							statusCode.setGroupID(statusCodeGroup.getGroupID());
							statusCode.setCode(statusCodeData.getString("code"));
							statusCode.setCodeDescription(statusCodeData.getString("codeDescription"));
							if (statusCodeMapper.addCode(statusCode) < 1)
								throw new RuntimeException("addCode error");
						}
					}
					JSONArray childGroupList = JSONArray
							.parseArray(statusCodeGroupData.getString("statusCodeGroupChildList"));
					if (childGroupList != null && !childGroupList.isEmpty())
					{
						for (Iterator<Object> iterator3 = childGroupList.iterator(); iterator3.hasNext();)
						{
							JSONObject groupData = (JSONObject) iterator3.next();
							StatusCodeGroup childStatusCodeGroup = new StatusCodeGroup();
							childStatusCodeGroup.setProjectID(project.getProjectID());
							childStatusCodeGroup.setGroupName(groupData.getString("groupName"));
							childStatusCodeGroup.setParentGroupID(statusCodeGroup.getGroupID());
							childStatusCodeGroup.setIsChild(1);
							if (statusCodeGroupMapper.addChildGroup(childStatusCodeGroup) < 1)
								throw new RuntimeException("addStatusCodeChildGroup error");
							JSONArray statusCodeList1 = JSONArray.parseArray(groupData.getString("statusCodeList"));
							if (statusCodeList1 != null && !statusCodeList1.isEmpty())
							{
								for (Iterator<Object> iterator2 = statusCodeList1.iterator(); iterator2.hasNext();)
								{
									JSONObject statusCodeData = (JSONObject) iterator2.next();
									StatusCode statusCode = new StatusCode();
									statusCode.setGroupID(statusCodeGroup.getGroupID());
									statusCode.setCode(statusCodeData.getString("code"));
									statusCode.setCodeDescription(statusCodeData.getString("codeDescription"));
									if (statusCodeMapper.addCode(statusCode) < 1)
										throw new RuntimeException("addCode error");
								}
							}

						}

					}

				}
			}
			JSONArray pageGroupList = JSONArray.parseArray(projectData.getString("pageGroupList"));
			if(pageGroupList != null && ! pageGroupList.isEmpty())
			{
				for(Iterator<Object> iterator = pageGroupList.iterator(); iterator.hasNext();)
				{
					JSONObject pageGroupData = (JSONObject)iterator.next();
					DocumentGroup documentGroup = new DocumentGroup();
					documentGroup.setGroupName(pageGroupData.getString("groupName"));
					documentGroup.setProjectID(project.getProjectID());
					if(documentGroupMapper.addDocumentGroup(documentGroup) < 1)
						throw new RuntimeException("addDocumentGroup error");
					JSONArray pageList = JSONArray.parseArray(pageGroupData.getString("pageList"));
					if(pageList != null &&!pageList.isEmpty())
					{
						for(Iterator<Object> iterator2 = pageList.iterator(); iterator2.hasNext();)
						{
							JSONObject pageData = (JSONObject)iterator2.next();
							Document document = new Document();
							document.setGroupID(documentGroup.getGroupID());
							document.setProjectID(project.getProjectID());
							document.setContentType(pageData.getInteger("contentType"));
							document.setContentRaw(pageData.getString("contentRaw"));
							document.setContent(pageData.getString("content"));
							document.setTitle(pageData.getString("title"));
							SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date date1 = null;
							Timestamp updateTime1 = null;
							try
							{
								date1 = dateFormat1.parse(pageData.getString("updateTime"));
								updateTime1 = new Timestamp(date1.getTime());
							}
							catch (ParseException e)
							{
								// TODO Auto-generated catch block
								date1 = new Date();
								updateTime1 = new Timestamp(date1.getTime());
							}
							document.setUpdateTime(updateTime1);
							document.setUserID(userID);
							if(documentMapper.addDocument(document) < 1)
								throw new RuntimeException("addDocument error");
						}
					}
					JSONArray childGroupList = JSONArray.parseArray(pageGroupData.getString("pageGroupChildList"));
					for(Iterator<Object> iterator1 = childGroupList.iterator(); iterator1.hasNext();)
					{
						JSONObject pageGroupData1 = (JSONObject)iterator1.next();
						DocumentGroup documentGroup1 = new DocumentGroup();
						documentGroup1.setGroupName(pageGroupData1.getString("groupName"));
						documentGroup1.setProjectID(project.getProjectID());
						documentGroup1.setParentGroupID(documentGroup.getGroupID());
						if(documentGroupMapper.addChildGroup(documentGroup1) < 1)
							throw new RuntimeException("addDocumentGroup error");
						JSONArray pageList1 = JSONArray.parseArray(pageGroupData1.getString("pageList"));
						if(pageList1 != null &&!pageList1.isEmpty())
						{
							for(Iterator<Object> iterator2 = pageList1.iterator(); iterator2.hasNext();)
							{
								JSONObject pageData = (JSONObject)iterator2.next();
								Document document = new Document();
								document.setGroupID(documentGroup1.getGroupID());
								document.setProjectID(project.getProjectID());
								document.setContentType(pageData.getInteger("contentType"));
								document.setContentRaw(pageData.getString("contentRaw"));
								document.setContent(pageData.getString("content"));
								document.setTitle(pageData.getString("title"));
								SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date date1 = null;
								Timestamp updateTime1 = null;
								try
								{
									date1 = dateFormat1.parse(pageData.getString("updateTime"));
									updateTime1 = new Timestamp(date1.getTime());
								}
								catch (ParseException e)
								{
									// TODO Auto-generated catch block
									date1 = new Date();
									updateTime1 = new Timestamp(date1.getTime());
									
								}
								document.setUpdateTime(updateTime1);
								document.setUserID(userID);
								if(documentMapper.addDocument(document) < 1)
									throw new RuntimeException("addDocument error");
							}
						}
					}
				}
			}
			JSONArray envList = JSONArray.parseArray(projectData.getString("env"));
			if(envList != null && !envList.isEmpty())
			{
				for(Iterator<Object> iterator = envList.iterator(); iterator.hasNext();)
				{
					JSONObject envData = (JSONObject)iterator.next();
					Env env = new Env();
					env.setEnvName(envData.getString("envName"));
					env.setProjectID(project.getProjectID());
					if(envMapper.addEnv(env) < 1)
						throw new RuntimeException("addEnv error");
					EnvFrontUri envFrontUri = new EnvFrontUri();
					if(envData.get("frontURI") != null)
					{
						JSONObject frontURIData = (JSONObject) envData.get("frontURI");
						envFrontUri.setUri(frontURIData.getString("uri"));
						envFrontUri.setApplyProtocol(frontURIData.getInteger("applyProtocol"));
					}
					else
					{
						envFrontUri.setUri(null);
						envFrontUri.setApplyProtocol(-1);
					}
					envFrontUri.setEnvID(env.getEnvID());
					if(envFrontUriMapper.addEnvFrontUri(envFrontUri) < 1)
						throw new RuntimeException("addEnvFrontUri error");
					JSONArray headerList = JSONArray.parseArray(envData.getString("headerList"));
					if(headerList != null && !headerList.isEmpty())
					{
						for(Iterator<Object> iterator2 = headerList.iterator(); iterator2.hasNext();)
						{
							JSONObject headerData = (JSONObject) iterator2.next();
							EnvHeader envHeader = new EnvHeader();
							envHeader.setApplyProtocol(headerData.getInteger("applyProtocol"));
							envHeader.setEnvID(env.getEnvID());
							envHeader.setHeaderName(headerData.getString("headerName"));
							envHeader.setHeaderValue(headerData.getString("headerValue"));
							if(envHeaderMapper.addEnvHeader(envHeader) < 1)
								throw new RuntimeException("addEnvHeader error");
						}
					}
					JSONArray paramList = JSONArray.parseArray(envData.getString("paramList"));
					if(paramList != null && !paramList.isEmpty())
					{
						for(Iterator<Object> iterator2 = paramList.iterator(); iterator2.hasNext();)
						{
							JSONObject paramData = (JSONObject) iterator2.next();
							EnvParam envParam = new EnvParam();
							envParam.setEnvID(env.getEnvID());
							envParam.setParamKey(paramData.getString("paramKey"));
							envParam.setParamValue(paramData.getString("paramValue"));
							if(envParamMapper.addEnvParam(envParam) < 1)
								throw new RuntimeException("addEnvParam error");
						}
					}
					JSONArray additionalParamList = JSONArray.parseArray(envData.getString("additionalParamList"));
					if(additionalParamList != null && !additionalParamList.isEmpty())
					{
						for(Iterator<Object> iterator2 = additionalParamList.iterator(); iterator2.hasNext();)
						{
							JSONObject paramData = (JSONObject) iterator2.next();
							EnvParamAdditional envParamAdditional = new EnvParamAdditional();
							envParamAdditional.setEnvID(env.getEnvID());
							envParamAdditional.setParamKey(paramData.getString("paramKey"));
							envParamAdditional.setParamValue(paramData.getString("paramValue"));
							if(envParamAdditionalMapper.addEnvParamAdditional(envParamAdditional) < 1)
								throw new RuntimeException("addEnvParamAdditional error");
						}
					}
				}
			}
			return true;
		}
		return false;
	}

}
