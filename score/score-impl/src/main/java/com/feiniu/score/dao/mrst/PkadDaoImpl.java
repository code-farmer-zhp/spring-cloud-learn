package com.feiniu.score.dao.mrst;

import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.mapper.mrst.PkadMapper;
import com.feiniu.score.util.ShardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class PkadDaoImpl implements PkadDao {
	@Autowired
	private PkadMapper pkadMapper;
	@Override
    public int savePkad(String membId,Pkad Pkad) {
		return pkadMapper.savePkad(Pkad, ShardUtils.getTableNo(membId));
	}

	@Override
	public Pkad getPkadByPkadIdAndMembId(String membId,String pkadId,String membGradeF){
		return pkadMapper.getPkadByPkadIdAndMembId(pkadId, membId, membGradeF, ShardUtils.getTableNo(membId));
	 }
	@Override
	public Pkad getPkadByPkadIdAndMembIdForUpdate(String membId,String pkadId,String membGradeF){
		return pkadMapper.getPkadByPkadIdAndMembIdForUpdate(pkadId, membId, membGradeF, ShardUtils.getTableNo(membId));
	}
	@Override
	public List<Pkad> getPkadListBySelective(String membId,Map<String, Object> paramMap){
    	return pkadMapper.getPkadListBySelective(membId, paramMap, ShardUtils.getTableNo(membId));
    }
	@Override
	public int getPkadCountBySelective(String membId,Map<String, Object> paramMap){
		return pkadMapper.getPkadCountBySelective(membId, paramMap, ShardUtils.getTableNo(membId));
    }
	@Override
	public int updatePkad(String membId,Pkad Pkad){
    	return pkadMapper.updatePkad(Pkad, ShardUtils.getTableNo(membId));
    }
	
	@Override
	public Set<String> getMrstUisBySelective(String membId,Map<String, Object> paramMap){
    	return pkadMapper.getMrstUisBySelective(membId, paramMap, ShardUtils.getTableNo(membId));
    }
	@Override
	public Pkad getPkadByPkadSeqAndMembId(String membId, String pkadSeq) {
		return pkadMapper.getPkadByPkadSeqAndMembId(membId, pkadSeq, ShardUtils.getTableNo(membId));
	}

	@Override
	public List<Pkad> getPkadsByPkadSeqsAndMembId(String membId, Set pkadSeqs) {
		return pkadMapper.getPkadsByPkadSeqsAndMembId(membId, pkadSeqs, ShardUtils.getTableNo(membId));
	}

	@Override
	public Pkad getPkadByPkadSeqAndMembIdForUpdate(String membId, String pkadSeq) {
		return  pkadMapper.getPkadByPkadSeqAndMembIdForUpdate(membId, pkadSeq, ShardUtils.getTableNo(membId));
	}

	@Override
	public List<String> getTakeCardNoByMembId(String membId) {
		return pkadMapper.getTakeCardNoByMembId(membId, ShardUtils.getTableNo(membId));
	}

	@Override
	public Pkad getLastPkad(String memGuid) {
		return pkadMapper.getLastPkad(memGuid,ShardUtils.getTableNo(memGuid));
	}
	@Override
	public List<Pkad> getTakenC3Between(int tableNo, String startTime, String endTime) {
		return pkadMapper.getTakenC3Between(tableNo,startTime,endTime);
	}	@Override
	public List<Pkad> getNoTakenPkads(int tableNo, String today) {
		return pkadMapper.getNoTakenPkads(tableNo,today);
	}}