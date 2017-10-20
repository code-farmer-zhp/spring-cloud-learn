package com.feiniu.score.entity.growth;

public class GrowthValueNum {
    private Long gvnSeq;

    private Integer value;

    private Integer num;
    
	private Integer version;

    public Long getGvnSeq() {
        return gvnSeq;
    }

    public void setGvnSeq(Long gvnSeq) {
        this.gvnSeq = gvnSeq;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
    
    
    public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}