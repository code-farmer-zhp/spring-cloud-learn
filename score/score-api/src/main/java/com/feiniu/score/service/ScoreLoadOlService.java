package com.feiniu.score.service;


import java.util.List;
import java.util.Map;

public interface ScoreLoadOlService {
    List<Map<String, Object>> loadOlScoreByType(String memGuid, Map<Integer, List<Map<String, Object>>> typeMap);
}
