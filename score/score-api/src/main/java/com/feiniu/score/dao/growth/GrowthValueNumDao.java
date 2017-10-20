package com.feiniu.score.dao.growth;

public interface GrowthValueNumDao {

	double getPercentLessThanMyGrowthValue(int growthvalue);

	void changeTableGrowthValueNum(Integer myGrowthValueOld,
								   Integer myGrowthValueNew);
}
