package com.feiniu.dao;


import com.feiniu.entity.Address;
import com.feiniu.entity.GeoEntity;
import com.feiniu.entity.Relation;
import com.feiniu.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AddressDao {

    @Autowired
    private AddressMapper addressMapper;

    @Value("${table}")
    private String table;

    @Value("${relationTable}")
    private String relationTable;

    /**
     * 分页获取默认地址
     */
    public List<Address> getList(long satrt, long end) {
        return addressMapper.getList(satrt, end, table);
    }

    /**
     * 保存用户和门店的关系
     */
    public int saveRelation(List<Relation> relations) {
        return addressMapper.saveRelation(relations, relationTable);
    }


    public int update(List<GeoEntity> entities) {
        return addressMapper.update(entities, table);
    }

    public int updateRelation(Relation relation) {
        return addressMapper.updateRelation(relation, relationTable);
    }
}
