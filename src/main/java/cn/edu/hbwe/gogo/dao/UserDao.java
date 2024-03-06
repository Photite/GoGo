package cn.edu.hbwe.gogo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.edu.hbwe.gogo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
