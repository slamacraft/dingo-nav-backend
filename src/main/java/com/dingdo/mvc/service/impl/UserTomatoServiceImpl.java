package com.dingdo.mvc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingdo.mvc.entities.UserTomatoEntity;
import com.dingdo.mvc.mapper.UserTomatoMapper;
import com.dingdo.mvc.service.UserTomatoService;
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
