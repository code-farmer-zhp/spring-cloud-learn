package com.feiniu.score.dao.mrst;

import com.feiniu.score.entity.mrst.Pkad;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PkadDao {

	int savePkad(String membId, Pkad Pkad);

	List<Pkad> getPkadListBySelective(String membId,
									  Map<String, Object> paramMap);

	int updatePkad(String membId, Pkad Pkad);

	int getPkadCountBySelective(String membId, Map<String, Object> paramMap);

	Set<String> getMrstUisBySelective(String membId,
									   Map<String, Object> paramMap);

	Pkad getPkadByPkadIdAndMembId(String membId, String pkadId,
								  String membGradeF);

	Pkad getPkadByPkadIdAndMembIdForUpdate(String membId, String pkadId,
										   String membGradeF);

	Pkad getPkadByPkadSeqAndMembId(String membId, String pkadSeq);

    List<Pkad> getPkadsByPkadSeqsAndMembId(String membId, Set pkadSeqs);

    Pkad getPkadByPkadSeqAndMembIdForUpdate(String membId, String pkadSeq);


	List<String> getTakeCardNoByMembId(String membId);

	Pkad getLastPkad(String memGuid);
	List<Pkad> getNoTakenPkads(int tableNo, String today);
	List<Pkad> getTakenC3Between(int tableNo, String startTime, String endTime);}