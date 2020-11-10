package mytest;

import mytest.mapper.UserMapper;
import mytest.model.UserDO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.List;

/**
 * @author zhangdj
 * @date 2020/01/09 10:39
 */
public class Main {
    public static void main(String[] args) throws Exception{
        SqlSession sqlSession = null;
        String resource = "mytest/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession= sqlSessionFactory.openSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//        userMapper.insert(new UserDO("lisi",null));
        List<UserDO> userDOS = userMapper.queryList();
        System.out.println(userDOS);
//        System.out.println(id);

    }
}
