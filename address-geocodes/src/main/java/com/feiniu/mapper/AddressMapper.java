package com.feiniu.mapper;

import com.feiniu.entity.Address;
import com.feiniu.entity.GeoEntity;
import com.feiniu.entity.Relation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {

    /**
     * 分页获取默认地址
     */
    List<Address> getList(@Param("start") long satrt, @Param("end") long end, @Param("table") String table);

    /**
     * 保存用户和门店的关系
     */
    int saveRelation(@Param("relations") List<Relation> relations, @Param("table") String table);

    int update(@Param("entities") List<GeoEntity> entities, @Param("table") String table);

    int updateRelation(@Param("relation") Relation relation, @Param("table") String table);
}
