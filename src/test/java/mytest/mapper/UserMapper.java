package mytest.mapper;

import mytest.model.UserDO;

import java.util.List;

/**
 * @author zhangdj
 * @date 2020/01/09 10:54
 */
public interface UserMapper {

    List<UserDO> queryList();

    void insert();
}
