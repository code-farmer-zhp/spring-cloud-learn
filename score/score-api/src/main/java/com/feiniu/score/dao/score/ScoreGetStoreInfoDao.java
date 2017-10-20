package com.feiniu.score.dao.score;

import com.feiniu.score.vo.StoreInfoVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ScoreGetStoreInfoDao {

    Map<String, StoreInfoVo> getStoreNoByOgSeq(String ogSeq, String memGuid);

    Map<String, String> getStoreNameBySellerNos(Set<String> sellerNos);

    Map<String, Boolean> isStore(List<String> sellerNos);

    Map<String, String> getMallNameBySellerNos(Set<String> sellerNos);
}
