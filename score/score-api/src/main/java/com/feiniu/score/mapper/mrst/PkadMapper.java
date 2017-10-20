package com.feiniu.score.mapper.mrst;

import com.feiniu.score.entity.mrst.Pkad;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PkadMapper {

    int savePkad(@Param("pkad") Pkad Pkad, @Param("tableNo") int tableNo);
    
    Pkad getPkadByPkadIdAndMembId(@Param("pkadId") String pkadId, @Param("membId") String membId, @Param("membGradeF") String membGradeF, @Param("tableNo") int tableNo);
    
    Pkad getPkadByPkadIdAndMembIdForUpdate(@Param("pkadId") String pkadId, @Param("membId") String membId, @Param("membGradeF") String membGradeF, @Param("tableNo") int tableNo);
    
    List<Pkad> getPkadListBySelective(@Param("membId") String memGuid, @Param("paramMap") Map<String, Object> paramMap, @Param("tableNo") int tableNo);
    
    int updatePkad(@Param("pkad") Pkad Pkad, @Param("tableNo") int tableNo);

	int getPkadCountBySelective(@Param("membId") String memGuid, @Param("paramMap") Map<String, Object> paramMap, @Param("tableNo") int tableNo);

	Set<String> getMrstUisBySelective(@Param("membId") String memGuid, @Param("paramMap") Map<String, Object> paramMap, @Param("tableNo") int tableNo);
	
	Pkad getPkadByPkadSeqAndMembId(@Param("membId") String membId, @Param("pkadSeq") String pkadSeq, @Param("tableNo") int tableNo);

    List<Pkad> getPkadsByPkadSeqsAndMembId(@Param("membId") String membId, @Param("pkadSeqs") Set pkadSeq, @Param("tableNo") int tableNo);

    Pkad getPkadByPkadSeqAndMembIdForUpdate(@Param("membId") String membId, @Param("pkadSeq") String pkadSeq, @Param("tableNo") int tableNo);

    List<String> getTakeCardNoByMembId(@Param("membId") String membId, @Param("tableNo") int tableNo);

    Pkad getLastPkad(@Param("memGuid") String memGuid,@Param("tableNo") int tableNo);
    List<Pkad> getNoTakenPkads(@Param("tableNo") int tableNo,@Param("today") String today);
	List<Pkad> getTakenC3Between(@Param("tableNo") int tableNo,@Param("startTime") String startTime,@Param("endTime") String endTime);}