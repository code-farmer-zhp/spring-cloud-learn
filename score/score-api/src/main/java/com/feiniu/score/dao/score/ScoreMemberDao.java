package com.feiniu.score.dao.score;



import com.feiniu.score.entity.score.ScoreMember;


 public interface ScoreMemberDao {

	 ScoreMember getScoreMember(String memGuid);

	 int deductScore(String memGuid, Integer consumeScore, Integer type);
	
	 int deductAvailableScore(String memGuid, Integer consumeScore);
	 
	 int addAvailableScore(String memGuid, Integer consumeScore);

	 int updateLockedScoreMember(String memGuid, int totalScore);
	
	 int updateLockedAvailableScoreMember(String memGuid, int totalScore, int lockedScore, int availableScore);

	 int saveLockedScoreMember(String memGuid, int totalScore);

	 int addExpiredScore(String memGuid, int scoreCosume);

	 int deductLockedScore(String memGuid, int orderGetScore);

	 int addScoreBecauseReturn(String memGuid, int consumeScore);

	 int saveAvailableScoreMember(String memGuid, Integer bindPhoneScore);
	
	 int saveExpiredScoreMember(String memGuid, Integer score);

	 int updateAvailableScoreMember(String memGuid,
			Integer scoreNumber);

	 int updateExpiredScore(String memGuid, Integer scoreRestore);

	 ScoreMember getLockedAndExpired(String memGuid);
	
	
}
