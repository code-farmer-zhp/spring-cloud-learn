package com.feiniu.score.mapper.score;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ScoreMember;

public interface ScoreMemberMapper {

    ScoreMember getScoreMember(@Param("memGuid") String memGuid,
                               @Param("tableNo") Integer tableNo);

    int deductScore(@Param("memGuid") String memGuid,
                    @Param("consumeScore") Integer consumeScore,
                    @Param("type") Integer type, @Param("tableNo") int tableNo);

    int addAvailableScore(@Param("memGuid") String memGuid,
                          @Param("consumeScore") Integer consumeScore,
                          @Param("tableNo") int tableNo);

    int updateLockedScoreMember(@Param("memGuid") String memGuid,
                                @Param("lockedScore") int totalScore, @Param("tableNo") int tableNo);

    int updateLockedAvailableScoreMember(
            @Param("memGuid") String memGuid,
            @Param("totalScore") int totalScore,
            @Param("lockedScore") int lockedScore,
            @Param("availableScore") int availableScore,
            @Param("tableNo") int tableNo);


    int saveLockedScoreMember(@Param("memGuid") String memGuid,
                              @Param("lockedScore") int totalScore, @Param("tableNo") int tableNo);

    int addExpiredScore(@Param("memGuid") String memGuid,
                        @Param("consumeScore") int consumeScore,
                        @Param("tableNo") int tableNo);

    int deductLockedScore(@Param("memGuid") String memGuid,
                          @Param("lockedScore") int orderGetScore,
                          @Param("tableNo") int tableNo);

    int addScoreBecauseReturn(@Param("memGuid") String memGuid,
                              @Param("consumeScore") int consumeScore,
                              @Param("tableNo") int tableNo);

    int saveAvailableScoreMember(@Param("memGuid") String memGuid, @Param("availableScore") Integer availableScore,
                                 @Param("tableNo") int tableNo);

    int saveExpiredScoreMember(@Param("memGuid") String memGuid, @Param("expiredScore") Integer expiredScore,
                               @Param("tableNo") int tableNo);

    int updateAvailableScoreMember(@Param("memGuid") String memGuid, @Param("availableScore") Integer availableScore,
                                   @Param("tableNo") int tableNo);


    int updateExpiredScore(@Param("memGuid") String memGuid, @Param("scoreNumber") Integer scoreNumber,
                           @Param("tableNo") int tableNo);

    int deductAvailableScore(@Param("memGuid") String memGuid, @Param("consumeScore") Integer consumeScore,
                             @Param("tableNo") int tableNo);


    ScoreMember getLockedAndExpired(@Param("memGuid") String memGuid, @Param("tableNo") int tableNo);

}
