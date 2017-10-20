package com.feiniu.member.util;

public class PageUtil {
	/**
	 * 显示页码
	 */
	protected Integer showPageNum;
	/**
	 * 连接URL
	 */
	protected String url;
	/**
	 * 当前页码
	 */
	protected Integer pageNo;
	/**
	 * 总页码
	 */
	protected Integer totalPage;
	/**
	 * 总条数
	 */
	protected Integer totalSum;
	/**
	 * 每页显示条数
	 */
	protected Integer pageSize;

	public PageUtil(Integer pageNo, Integer totalSum, Integer pageSize, String url) {
		pageNo = pageNo < 1 ? 1 : pageNo; 
		this.pageNo = pageNo; 
		this.totalSum = totalSum; 
		this.url = url;
		this.totalPage = null;
		this.pageSize = pageSize; 
	} 
	public PageUtil(Integer pageNo, Integer totalSum, Integer pageSize, Integer totalPage, String url) {
		pageNo = pageNo < 1 ? 1 : pageNo; 
		this.pageNo = pageNo; 
		this.totalSum = totalSum; 
		this.url = url;
		this.totalPage = totalPage; 
		this.pageSize = pageSize; 
	}
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(Integer totalSum) {
		this.totalSum = totalSum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getShowPageNum() {
		return showPageNum;
	}

	public void setShowPageNum(Integer showPageNum) {
		this.showPageNum = showPageNum;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	@Override
	public String toString(){
		return "PageUtil {\"pageNo\":" + pageNo + ",\"totalSum\":" + totalSum + ","+
		"\"pageSize\":" + pageSize + ",\"totalPage\":" + totalPage + ","+
				"\"url\":" + url + ",\"showPageNum\":"+showPageNum;
	}
}
