package com.feiniu.score.service;

import com.feiniu.score.entity.mrst.Pkad;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yue.teng on 2016/7/15.
 */
public interface PkadBaseServiceWithCache {
    int savePkad(String membId, Pkad Pkad);

    List<Pkad> getPkadListBySelective(String membId, Map<String, Object> paramMap, boolean withCache);

    int getPkadCountBySelective(String membId, Map<String, Object> paramMap, boolean withCache);

    int updatePkad(String membId, Pkad Pkad);

    Set<String> getMrstUisBySelective(String membId, Map<String, Object> paramMap, boolean withCache);

    Pkad getPkadByPkadIdAndMembId(String membId, String pkadId, String membGradeF);

    Pkad getPkadByPkadIdAndMembIdForUpdate(String membId, String pkadId, String membGradeF);

    Pkad getPkadByPkadSeqAndMembId(String membId, String pkadSeq);

    List<Pkad> getPkadsByPkadSeqsAndMembId(String membId, Set pkadSeqs);

    Pkad getPkadByPkadSeqAndMembIdForUpdate(String membId, String pkadSeq);

    List<String> getTakeCardNoByMembId(String membId);

    String getLastPkadMrsuUi(String membId, boolean withCache);
}
