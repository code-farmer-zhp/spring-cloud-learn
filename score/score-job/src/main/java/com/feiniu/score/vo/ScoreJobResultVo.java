package com.feiniu.score.vo;

public class ScoreJobResultVo extends JobResultVo {
	
	private int scoreNumber;

	@Override
	public synchronized void addProcessResultVo(JobResultVo dataSyncVo) {
		ScoreJobResultVo mdsResultVo = (ScoreJobResultVo) dataSyncVo;
		
		this.scoreNumber = this.scoreNumber + mdsResultVo.getScoreNumber();
		super.addProcessResultVo(dataSyncVo);
	}
	
	/*
	 * 打印信息
	 */
	@Override
	public String getPrintString() {
		return super.getPrintString() + " 积分:" + scoreNumber;
	}
	
	public void addScoreNumber(int scoreNumber){
		this.scoreNumber = this.scoreNumber + scoreNumber;
	}
	
	public int getScoreNumber() {
		return scoreNumber;
	}

	public void setScoreNumber(int scoreNumber) {
		this.scoreNumber = scoreNumber;
	}
}
