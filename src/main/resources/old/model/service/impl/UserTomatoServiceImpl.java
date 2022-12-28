package com.dingo.model.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingo.model.entities.UserTomatoEntity;
import com.dingo.model.mapper.UserTomatoMapper;
import com.dingo.model.service.UserTomatoService;
import org.springframework.stereotype.Service;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/18 16:06
 * @since JDK 1.8
 */
@Service
public class UserTomatoServiceImpl extends ServiceImpl<UserTomatoMapper, UserTomatoEntity> implements UserTomatoService {
}
