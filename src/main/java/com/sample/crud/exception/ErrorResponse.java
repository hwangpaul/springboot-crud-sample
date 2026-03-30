package com.sample.crud.dao;

import com.sample.crud.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserDAO {

    UserVO findById(@Param("id") Long id);

    List<UserVO> findAll();

    UserVO findByEmail(@Param("email") String email);

    boolean existsByEmail(@Param("email") String email);

    int insert(UserVO userVO);

    int update(UserVO userVO);

    int deleteById(@Param("id") Long id);
}
