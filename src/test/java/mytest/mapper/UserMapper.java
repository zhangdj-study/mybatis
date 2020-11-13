package mytest.mapper;

import mytest.model.UserDO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhangdj
 * @date 2020/01/09 10:54
 */
public interface UserMapper {

    @Select("select * from t_user")
    List<UserDO> queryList();

    int insert(UserDO userDO);
}
