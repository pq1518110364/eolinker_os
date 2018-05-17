package com.eolinker.pojo;
/**
 * 环境请求头部
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
public class EnvHeader {

	private Integer headerID;//请求头部ID
	private Integer envID;//环境ID
	private Integer applyProtocol;//请求协议
	private String headerName;//标签
	private String headerValue;//内容
	
	
	public Integer getHeaderID() {
		return headerID;
	}
	public void setHeaderID(Integer headerID) {
		this.headerID = headerID;
	}
	public Integer getEnvID() {
		return envID;
	}
	public void setEnvID(Integer envID) {
		this.envID = envID;
	}
	public Integer getApplyProtocol() {
		return applyProtocol;
	}
	public void setApplyProtocol(Integer applyProtocol) {
		this.applyProtocol = applyProtocol;
	}
	public String getHeaderName() {
		return headerName;
	}
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	public String getHeaderValue() {
		return headerValue;
	}
	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}
	
	
}
