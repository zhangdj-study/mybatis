package mytest.model;

/**
 * @author zhangdj
 * @date 2020/01/09 10:51
 */
public class UserDO {

    private Long id;

    private String userName;

    private String userEmail;

    private String userPhone;

    @Override
    public String toString() {
        return "UserDO{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhone='" + userPhone + '\'' +
                '}';
    }
}
