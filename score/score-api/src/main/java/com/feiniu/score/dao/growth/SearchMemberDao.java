package com.feiniu.score.dao.growth;

import com.feiniu.score.dto.PartnerInfo;

import java.util.Map;

public interface SearchMemberDao{

	Map<String,Object> getMemberInfo(String memGuid);

	PartnerInfo getIsPartnerInfo(String memGuid);
}
