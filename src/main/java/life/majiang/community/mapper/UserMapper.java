package life.majiang.community.mapper;

import life.majiang.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {

    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified) values " +
            "(#{name},#{account_id},#{token},#{gmt_create},#{gmt_modified})")
    void insert(User user);

    @Select("select * from user where token = #{token}")
    User findByToken(String token);
}
