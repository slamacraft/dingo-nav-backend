package com.dingdo.model.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingdo.model.entities.UserTomatoEntity;
import com.dingdo.model.mapper.UserTomatoMapper;
import com.dingdo.model.service.UserTomatoService;
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
